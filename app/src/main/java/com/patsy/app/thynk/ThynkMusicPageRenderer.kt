package com.patsy.app.thynk

/**
 * Stable render registry for every visible THyNK Music destination.
 *
 * Keeping the catalog-to-render mapping explicit prevents a page from being added to navigation
 * while silently rendering an empty body. The Compose host consumes these kinds when deciding
 * which native page body to show.
 */
internal enum class ThynkMusicRenderKind {
    HOME,
    CREATE_MUSIC,
    AI_MUSIC,
    RECORDING,
    TRACK_EDITOR,
    MIXER,
    EQUALIZER,
    EFFECTS,
    LYRICS_VOCALS,
    DJ_STUDIO,
    AUTO_TUNER,
    MASTERING,
    VIDEO_HOME,
    VIDEO_PLAYER,
    VIDEO_EDITOR,
    EXPORT,
}

internal object ThynkMusicPageRenderer {
    fun kind(pageId: String): ThynkMusicRenderKind? = when (pageId) {
        "music-home" -> ThynkMusicRenderKind.HOME
        "create-music" -> ThynkMusicRenderKind.CREATE_MUSIC
        "ai-music-generator" -> ThynkMusicRenderKind.AI_MUSIC
        "recording" -> ThynkMusicRenderKind.RECORDING
        "track-editor" -> ThynkMusicRenderKind.TRACK_EDITOR
        "mixer" -> ThynkMusicRenderKind.MIXER
        "equalizer" -> ThynkMusicRenderKind.EQUALIZER
        "effects" -> ThynkMusicRenderKind.EFFECTS
        "lyrics-vocals" -> ThynkMusicRenderKind.LYRICS_VOCALS
        "dj-studio" -> ThynkMusicRenderKind.DJ_STUDIO
        "auto-tuner" -> ThynkMusicRenderKind.AUTO_TUNER
        "mastering" -> ThynkMusicRenderKind.MASTERING
        "video-home" -> ThynkMusicRenderKind.VIDEO_HOME
        "video-player" -> ThynkMusicRenderKind.VIDEO_PLAYER
        "video-editor" -> ThynkMusicRenderKind.VIDEO_EDITOR
        "export" -> ThynkMusicRenderKind.EXPORT
        else -> null
    }
}
