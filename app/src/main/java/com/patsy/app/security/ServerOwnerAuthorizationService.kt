package com.patsy.app.security

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.ServiceFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

sealed interface RemoteOwnerAuthorizationDecision {
    data class Allowed(
        val authorizationId: String,
        val capability: OwnerCapability,
        val expiresAtEpochMillis: Long,
        val auditCorrelationId: String,
    ) : RemoteOwnerAuthorizationDecision
    data class Denied(val reason: OwnerDenialReason) : RemoteOwnerAuthorizationDecision
    data object Unavailable : RemoteOwnerAuthorizationDecision
}

interface OwnerAuthorizationTransport {
    suspend fun authorize(accessToken: String, capability: OwnerCapability): RemoteOwnerAuthorizationDecision
}

class ServerOwnerAuthorizationService(
    private val sessionStore: AuthSessionStore,
    private val transport: OwnerAuthorizationTransport,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : OwnerAuthorizationService {
    override suspend fun verify(session: PublicSession, capability: OwnerCapability): OwnerAuthorizationDecision {
        val now = nowEpochMillis()
        if (session.expiresAtEpochMillis <= now) {
            return OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_EXPIRED)
        }
        val stored = sessionStore.read()
            ?: return OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_AUTHENTICATED)
        if (stored.publicSession.sessionId != session.sessionId || stored.publicSession.userId != session.userId) {
            return OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED)
        }
        return when (val remote = transport.authorize(stored.accessToken, capability)) {
            is RemoteOwnerAuthorizationDecision.Allowed -> when {
                remote.capability != capability -> OwnerAuthorizationDecision.Denied(OwnerDenialReason.CAPABILITY_NOT_GRANTED)
                remote.expiresAtEpochMillis <= now -> OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_EXPIRED)
                else -> OwnerAuthorizationDecision.Allowed(
                    remote.authorizationId,
                    remote.capability,
                    remote.expiresAtEpochMillis,
                    remote.auditCorrelationId,
                )
            }
            is RemoteOwnerAuthorizationDecision.Denied -> OwnerAuthorizationDecision.Denied(remote.reason)
            RemoteOwnerAuthorizationDecision.Unavailable -> OwnerAuthorizationDecision.Unavailable(ServiceFailure.ServerError)
        }
    }
}

/** Authenticated client for the server capability endpoint backed by private.user_access. */
class SupabaseHttpOwnerAuthorizationTransport(
    baseUrl: String,
    private val publishableKey: String,
) : OwnerAuthorizationTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/functions/v1/owner-authorize"

    override suspend fun authorize(accessToken: String, capability: OwnerCapability) = withContext(Dispatchers.IO) {
        try {
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 20_000
                useCaches = false
                doOutput = true
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("apikey", publishableKey)
                setRequestProperty("Authorization", "Bearer $accessToken")
            }
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use {
                it.write(JSONObject().put("capability", capability.name).toString())
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            connection.disconnect()
            when {
                status in 200..299 -> parseAllowed(body, capability)
                status == 401 -> RemoteOwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED)
                status == 403 -> RemoteOwnerAuthorizationDecision.Denied(OwnerDenialReason.CAPABILITY_NOT_GRANTED)
                else -> RemoteOwnerAuthorizationDecision.Unavailable
            }
        } catch (_: SocketTimeoutException) {
            RemoteOwnerAuthorizationDecision.Unavailable
        } catch (_: IOException) {
            RemoteOwnerAuthorizationDecision.Unavailable
        } catch (_: Exception) {
            RemoteOwnerAuthorizationDecision.Unavailable
        }
    }

    private fun parseAllowed(body: String, requested: OwnerCapability): RemoteOwnerAuthorizationDecision {
        val root = JSONObject(body)
        if (!root.optBoolean("allowed", false)) {
            return RemoteOwnerAuthorizationDecision.Denied(OwnerDenialReason.CAPABILITY_NOT_GRANTED)
        }
        val returned = runCatching { OwnerCapability.valueOf(root.getString("capability")) }.getOrNull()
            ?: return RemoteOwnerAuthorizationDecision.Unavailable
        if (returned != requested) {
            return RemoteOwnerAuthorizationDecision.Denied(OwnerDenialReason.CAPABILITY_NOT_GRANTED)
        }
        return RemoteOwnerAuthorizationDecision.Allowed(
            authorizationId = root.getString("authorization_id"),
            capability = returned,
            expiresAtEpochMillis = root.getLong("expires_at_epoch_millis"),
            auditCorrelationId = root.getString("audit_correlation_id"),
        )
    }
}
