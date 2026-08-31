package com.patsy.app.thynk

internal fun editorPageForThynkItem(item: String): String? = when (item) {
    "VIDEO EDITOR",
    "TRIM & CUT",
    "SPLIT",
    "TRANSITIONS",
    "SUBTITLES",
    "OVERLAYS",
    "GREEN SCREEN",
    "VIDEO FILTERS",
    "SLOW MOTION",
    "SLIDESHOW",
    "LOOP",
    "ASPECT RESIZER" -> "video-editor"
    else -> null
}
