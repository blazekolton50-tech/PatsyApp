package com.patsy.app.studio.export

import com.patsy.app.studio.media.StudioMediaReference

enum class StudioExportType { IMAGE, VIDEO, AUDIO, PROJECT_PACKAGE }
enum class StudioExportState { IDLE, QUEUED, RUNNING, COMPLETE, FAILED, CANCELLED, NOT_CONFIGURED }

data class StudioExportRequest(
    val projectId: String,
    val type: StudioExportType,
    val clientRequestId: String,
)

data class StudioExportResult(
    val state: StudioExportState,
    val output: StudioMediaReference? = null,
    val safeFailureCode: String? = null,
) {
    init {
        require(state != StudioExportState.COMPLETE || output != null) {
            "complete export requires real output metadata"
        }
    }
}

interface StudioExportService {
    suspend fun export(request: StudioExportRequest): StudioExportResult
    suspend fun cancel(clientRequestId: String): StudioExportResult
}
