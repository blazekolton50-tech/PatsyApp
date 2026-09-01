package com.patsy.app.studio

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

sealed interface StudioPersistenceResult {
    data class Loaded(
        val state: StudioProjectPersistenceState,
        val layers: List<StudioLayerPersistenceRecord>,
        val latestRevision: StudioRevisionRecord?,
    ) : StudioPersistenceResult
    data object NotFound : StudioPersistenceResult
    data object Unauthorized : StudioPersistenceResult
    data object Unavailable : StudioPersistenceResult
}

data class StudioProjectPersistenceState(
    val projectId: String,
    val editorMode: String,
    val canvasWidthPx: Int?,
    val canvasHeightPx: Int?,
    val durationMs: Int?,
    val fps: Double?,
    val autosaveRevision: Long,
    val lastAutosavedAtEpochMillis: Long?,
)

data class StudioLayerPersistenceRecord(
    val id: String,
    val projectId: String,
    val parentLayerId: String?,
    val layerType: String,
    val name: String,
    val zIndex: Int,
    val startMs: Int?,
    val endMs: Int?,
    val isLocked: Boolean,
    val isHidden: Boolean,
    val opacity: Double,
    val transformJson: String,
    val cropJson: String?,
    val styleJson: String,
    val effectsJson: String,
    val contentJson: String,
)

data class StudioRevisionRecord(
    val id: String,
    val projectId: String,
    val revisionNo: Long,
    val revisionType: String,
    val snapshotJson: String,
    val createdAtEpochMillis: Long,
)

sealed interface RemoteStudioPersistenceResult {
    data class Loaded(
        val state: StudioProjectPersistenceState,
        val layers: List<StudioLayerPersistenceRecord>,
        val latestRevision: StudioRevisionRecord?,
    ) : RemoteStudioPersistenceResult
    data object NotFound : RemoteStudioPersistenceResult
    data object Unauthorized : RemoteStudioPersistenceResult
    data object Unavailable : RemoteStudioPersistenceResult
}

interface StudioPersistenceTransport {
    suspend fun load(accessToken: String, userId: String, projectId: String): RemoteStudioPersistenceResult
}

interface StudioPersistenceService {
    suspend fun load(session: PublicSession, projectId: String): StudioPersistenceResult
}

class ServerStudioPersistenceService(
    private val sessionStore: AuthSessionStore,
    private val transport: StudioPersistenceTransport,
) : StudioPersistenceService {
    override suspend fun load(session: PublicSession, projectId: String): StudioPersistenceResult {
        if (projectId.isBlank()) return StudioPersistenceResult.NotFound
        val stored = sessionStore.read() ?: return StudioPersistenceResult.Unauthorized
        if (
            stored.publicSession.sessionId != session.sessionId ||
            stored.publicSession.userId != session.userId ||
            stored.accessToken.isBlank()
        ) return StudioPersistenceResult.Unauthorized

        return when (val result = transport.load(stored.accessToken, session.userId, projectId)) {
            is RemoteStudioPersistenceResult.Loaded -> StudioPersistenceResult.Loaded(
                state = result.state,
                layers = result.layers.sortedBy { it.zIndex },
                latestRevision = result.latestRevision,
            )
            RemoteStudioPersistenceResult.NotFound -> StudioPersistenceResult.NotFound
            RemoteStudioPersistenceResult.Unauthorized -> StudioPersistenceResult.Unauthorized
            RemoteStudioPersistenceResult.Unavailable -> StudioPersistenceResult.Unavailable
        }
    }
}

class SupabaseStudioPersistenceTransport(
    baseUrl: String,
    private val publishableKey: String,
) : StudioPersistenceTransport {
    private val restBase = "${baseUrl.trimEnd('/')}/rest/v1"

    override suspend fun load(
        accessToken: String,
        userId: String,
        projectId: String,
    ): RemoteStudioPersistenceResult = withContext(Dispatchers.IO) {
        try {
            val uid = URLEncoder.encode(userId, Charsets.UTF_8.name())
            val pid = URLEncoder.encode(projectId, Charsets.UTF_8.name())
            val stateResponse = get(
                "$restBase/studio_project_state?select=project_id,editor_mode,canvas_width_px,canvas_height_px,duration_ms,fps,autosave_revision,last_autosaved_at&user_id=eq.$uid&project_id=eq.$pid&limit=1",
                accessToken,
            )
            if (stateResponse.status == 401 || stateResponse.status == 403) return@withContext RemoteStudioPersistenceResult.Unauthorized
            if (stateResponse.status !in 200..299) return@withContext RemoteStudioPersistenceResult.Unavailable
            val stateRows = JSONArray(stateResponse.body)
            if (stateRows.length() == 0) return@withContext RemoteStudioPersistenceResult.NotFound
            if (stateRows.length() != 1) return@withContext RemoteStudioPersistenceResult.Unavailable
            val stateJson = stateRows.getJSONObject(0)
            val state = StudioProjectPersistenceState(
                projectId = stateJson.getString("project_id"),
                editorMode = stateJson.optString("editor_mode"),
                canvasWidthPx = stateJson.optNullableInt("canvas_width_px"),
                canvasHeightPx = stateJson.optNullableInt("canvas_height_px"),
                durationMs = stateJson.optNullableInt("duration_ms"),
                fps = if (stateJson.has("fps") && !stateJson.isNull("fps")) stateJson.getDouble("fps") else null,
                autosaveRevision = stateJson.optLong("autosave_revision", 0L),
                lastAutosavedAtEpochMillis = stateJson.optNullableString("last_autosaved_at")?.let(::parseIsoMillis),
            )

            val layersResponse = get(
                "$restBase/studio_layers?select=id,project_id,parent_layer_id,layer_type,name,z_index,start_ms,end_ms,is_locked,is_hidden,opacity,transform,crop,style,effects,content&user_id=eq.$uid&project_id=eq.$pid&order=z_index.asc",
                accessToken,
            )
            if (layersResponse.status == 401 || layersResponse.status == 403) return@withContext RemoteStudioPersistenceResult.Unauthorized
            if (layersResponse.status !in 200..299) return@withContext RemoteStudioPersistenceResult.Unavailable
            val layerRows = JSONArray(layersResponse.body)
            val layers = buildList {
                repeat(layerRows.length()) {
                    val item = layerRows.getJSONObject(it)
                    add(
                        StudioLayerPersistenceRecord(
                            id = item.getString("id"),
                            projectId = item.getString("project_id"),
                            parentLayerId = item.optNullableString("parent_layer_id"),
                            layerType = item.optString("layer_type"),
                            name = item.optString("name").ifBlank { "Layer" },
                            zIndex = item.optInt("z_index", 0),
                            startMs = item.optNullableInt("start_ms"),
                            endMs = item.optNullableInt("end_ms"),
                            isLocked = item.optBoolean("is_locked", false),
                            isHidden = item.optBoolean("is_hidden", false),
                            opacity = if (item.has("opacity") && !item.isNull("opacity")) item.getDouble("opacity") else 1.0,
                            transformJson = item.opt("transform")?.toString() ?: "{}",
                            cropJson = if (item.has("crop") && !item.isNull("crop")) item.get("crop").toString() else null,
                            styleJson = item.opt("style")?.toString() ?: "{}",
                            effectsJson = item.opt("effects")?.toString() ?: "[]",
                            contentJson = item.opt("content")?.toString() ?: "{}",
                        ),
                    )
                }
            }

            val revisionResponse = get(
                "$restBase/studio_revisions?select=id,project_id,revision_no,revision_type,snapshot,created_at&user_id=eq.$uid&project_id=eq.$pid&order=revision_no.desc&limit=1",
                accessToken,
            )
            if (revisionResponse.status == 401 || revisionResponse.status == 403) return@withContext RemoteStudioPersistenceResult.Unauthorized
            if (revisionResponse.status !in 200..299) return@withContext RemoteStudioPersistenceResult.Unavailable
            val revisionRows = JSONArray(revisionResponse.body)
            val revision = if (revisionRows.length() == 1) {
                val item = revisionRows.getJSONObject(0)
                StudioRevisionRecord(
                    id = item.getString("id"),
                    projectId = item.getString("project_id"),
                    revisionNo = item.getLong("revision_no"),
                    revisionType = item.optString("revision_type"),
                    snapshotJson = item.opt("snapshot")?.toString() ?: "{}",
                    createdAtEpochMillis = parseIsoMillis(item.optString("created_at")),
                )
            } else null

            RemoteStudioPersistenceResult.Loaded(state, layers, revision)
        } catch (_: Exception) {
            RemoteStudioPersistenceResult.Unavailable
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

    private fun org.json.JSONObject.optNullableString(name: String): String? =
        if (!has(name) || isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun org.json.JSONObject.optNullableInt(name: String): Int? =
        if (!has(name) || isNull(name)) null else getInt(name)

    private fun parseIsoMillis(value: String): Long = try {
        java.time.Instant.parse(value).toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}
