package com.patsy.app.studio.media

enum class MediaAvailability { BUNDLED, LOCAL_DEVICE, DEVELOPMENT_STAGED, REMOTE_AVAILABLE, UNAVAILABLE }

data class MediaLicence(
    val origin: String,
    val licence: String? = null,
    val evidenceReference: String? = null,
    val approvedForBundling: Boolean = false,
)

data class StudioMediaReference(
    val stableId: String,
    val mimeType: String,
    val sizeBytes: Long? = null,
    val sha256: String? = null,
    val version: String? = null,
    val availability: MediaAvailability,
    val licence: MediaLicence,
)

interface DevelopmentAssetLocator {
    /** Resolve by stable ID only. Implementations must never accept arbitrary filesystem paths from UI input. */
    suspend fun resolve(stableId: String): StudioMediaReference?
}
