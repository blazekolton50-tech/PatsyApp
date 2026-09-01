package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

sealed interface DmDataResult {
    data class Loaded(val threads: List<DmThreadRecord>) : DmDataResult
    data object Unauthorized : DmDataResult
    data object Unavailable : DmDataResult
}

data class DmThreadRecord(
    val id: String,
    val updatedAtEpochMillis: Long,
    val lastMessage: RemoteDmMessage?,
    val title: String? = null,
    val unreadCount: Int? = null,
    val isGroup: Boolean? = null,
    val archived: Boolean? = null,
)

data class RemoteDmThread(
    val id: String,
    val updatedAtEpochMillis: Long,
    val lastMessage: RemoteDmMessage?,
)

data class RemoteDmMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
)

sealed interface RemoteDmDataResult {
    data class Loaded(val threads: List<RemoteDmThread>) : RemoteDmDataResult
    data object Unauthorized : RemoteDmDataResult
    data object Unavailable : RemoteDmDataResult
}

interface DmDataTransport {
    suspend fun fetch(accessToken: String, userId: String): RemoteDmDataResult
}

interface DmDataService {
    suspend fun load(session: PublicSession): DmDataResult
}

class ServerDmDataService(
    private val sessionStore: AuthSessionStore,
    private val transport: DmDataTransport,
) : DmDataService {
    override suspend fun load(session: PublicSession): DmDataResult {
        val stored = sessionStore.read() ?: return DmDataResult.Unauthorized
        if (
            stored.publicSession.sessionId != session.sessionId ||
            stored.publicSession.userId != session.userId ||
            stored.accessToken.isBlank()
        ) return DmDataResult.Unauthorized

        return when (val result = transport.fetch(stored.accessToken, session.userId)) {
            is RemoteDmDataResult.Loaded -> DmDataResult.Loaded(
                result.threads
                    .sortedByDescending { it.updatedAtEpochMillis }
                    .map {
                        DmThreadRecord(
                            id = it.id,
                            updatedAtEpochMillis = it.updatedAtEpochMillis,
                            lastMessage = it.lastMessage,
                        )
                    },
            )
            RemoteDmDataResult.Unauthorized -> DmDataResult.Unauthorized
            RemoteDmDataResult.Unavailable -> DmDataResult.Unavailable
        }
    }
}

class SupabaseDmDataTransport(
    baseUrl: String,
    private val publishableKey: String,
) : DmDataTransport {
    private val restBase = "${baseUrl.trimEnd('/')}/rest/v1"

    override suspend fun fetch(accessToken: String, userId: String): RemoteDmDataResult =
        withContext(Dispatchers.IO) {
            try {
                val encodedUserId = URLEncoder.encode(userId, Charsets.UTF_8.name())
                val membership = get(
                    "$restBase/dm_members?select=thread_id&user_id=eq.$encodedUserId",
                    accessToken,
                )
                if (membership.status == 401 || membership.status == 403) {
                    return@withContext RemoteDmDataResult.Unauthorized
                }
                if (membership.status !in 200..299) return@withContext RemoteDmDataResult.Unavailable

                val membershipRows = JSONArray(membership.body)
                val threadIds = buildList {
                    repeat(membershipRows.length()) {
                        add(membershipRows.getJSONObject(it).getString("thread_id"))
                    }
                }.distinct()
                if (threadIds.isEmpty()) return@withContext RemoteDmDataResult.Loaded(emptyList())

                val threads = mutableListOf<RemoteDmThread>()
                for (threadId in threadIds) {
                    val encodedThreadId = URLEncoder.encode(threadId, Charsets.UTF_8.name())
                    val threadResponse = get(
                        "$restBase/dm_threads?select=id,updated_at&id=eq.$encodedThreadId&limit=1",
                        accessToken,
                    )
                    if (threadResponse.status == 401 || threadResponse.status == 403) {
                        return@withContext RemoteDmDataResult.Unauthorized
                    }
                    if (threadResponse.status !in 200..299) return@withContext RemoteDmDataResult.Unavailable
                    val threadRows = JSONArray(threadResponse.body)
                    if (threadRows.length() != 1) continue
                    val threadJson = threadRows.getJSONObject(0)

                    val messageResponse = get(
                        "$restBase/dm_messages?select=id,thread_id,sender_id,body,created_at,expires_at&thread_id=eq.$encodedThreadId&order=created_at.desc&limit=1",
                        accessToken,
                    )
                    if (messageResponse.status == 401 || messageResponse.status == 403) {
                        return@withContext RemoteDmDataResult.Unauthorized
                    }
                    if (messageResponse.status !in 200..299) return@withContext RemoteDmDataResult.Unavailable
                    val messageRows = JSONArray(messageResponse.body)
                    val lastMessage = if (messageRows.length() == 1) {
                        val message = messageRows.getJSONObject(0)
                        RemoteDmMessage(
                            id = message.getString("id"),
                            threadId = message.getString("thread_id"),
                            senderId = message.getString("sender_id"),
                            body = message.optString("body"),
                            createdAtEpochMillis = parseIsoMillis(message.optString("created_at")),
                            expiresAtEpochMillis = message.optString("expires_at")
                                .takeIf { it.isNotBlank() && it != "null" }
                                ?.let(::parseIsoMillis),
                        )
                    } else null

                    threads += RemoteDmThread(
                        id = threadJson.getString("id"),
                        updatedAtEpochMillis = parseIsoMillis(threadJson.optString("updated_at")),
                        lastMessage = lastMessage,
                    )
                }
                RemoteDmDataResult.Loaded(threads)
            } catch (_: Exception) {
                RemoteDmDataResult.Unavailable
            }
        }

    private data class Response(val status: Int, val body: String)

    private fun get(url: String, accessToken: String): Response {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 20_000
            useCaches = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("apikey", publishableKey)
            setRequestProperty("Authorization", "Bearer $accessToken")
        }
        return try {
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            Response(status, stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty())
        } finally {
            connection.disconnect()
        }
    }

    private fun parseIsoMillis(value: String): Long = try {
        java.time.Instant.parse(value).toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}
