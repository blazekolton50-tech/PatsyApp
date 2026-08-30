package com.patsy.app.studio.camera

enum class CameraCapabilityState { AVAILABLE, PERMISSION_REQUIRED, UNAVAILABLE, NOT_CONFIGURED, UNSUPPORTED_DEVICE }

data class CameraCapabilities(
    val state: CameraCapabilityState,
    val photoCapture: Boolean = false,
    val videoCapture: Boolean = false,
    val frontCamera: Boolean = false,
    val rearCamera: Boolean = false,
) {
    init {
        if (state != CameraCapabilityState.AVAILABLE) {
            require(!photoCapture && !videoCapture) { "unavailable camera cannot advertise capture" }
        }
    }
}

interface StudioCameraGateway {
    suspend fun capabilities(): CameraCapabilities
}
