package com.patsy.app.thynk

private val designEditorItems = setOf(
    "POSTERS",
    "FLYERS",
    "INVITATIONS",
    "CARDS",
    "MENUS",
    "PRICE LISTS",
    "SIGNS",
    "CERTIFICATES",
    "BROCHURES",
    "LABELS",
    "BLANK DESIGNS",
    "CUSTOM SIZE",
    "TEMPLATES",
)

internal fun editorPageForThynkItem(item: String): String? = when {
    item in designEditorItems -> "design-editor"
    item in setOf(
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
        "ASPECT RESIZER",
    ) -> "video-editor"
    else -> null
}
