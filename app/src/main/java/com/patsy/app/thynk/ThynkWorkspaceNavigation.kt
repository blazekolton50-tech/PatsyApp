package com.patsy.app.thynk

/**
 * Internal THyNK workspace history. The authenticated app shell still owns global destinations;
 * this model only decides when THyNK is at a hub/category versus a full-screen editing board.
 */
internal sealed interface ThynkWorkspaceRoute {
    data object Hub : ThynkWorkspaceRoute
    data class Category(val categoryId: String) : ThynkWorkspaceRoute
    data class Music(val pageId: String) : ThynkWorkspaceRoute
    data class Editor(
        val pageId: String,
        val categoryId: String,
    ) : ThynkWorkspaceRoute
}

internal object ThynkWorkspaceNavigation {
    private val musicEditingBoardPageIds = setOf(
        "track-editor",
        "mixer",
        "equalizer",
        "effects",
        "lyrics-vocals",
        "mastering",
        "export",
    )

    fun isEditingBoard(route: ThynkWorkspaceRoute): Boolean = when (route) {
        is ThynkWorkspaceRoute.Editor -> true
        is ThynkWorkspaceRoute.Music -> route.pageId in musicEditingBoardPageIds
        ThynkWorkspaceRoute.Hub,
        is ThynkWorkspaceRoute.Category -> false
    }

    fun back(route: ThynkWorkspaceRoute): ThynkWorkspaceRoute = when (route) {
        is ThynkWorkspaceRoute.Editor -> ThynkWorkspaceRoute.Category(route.categoryId)
        is ThynkWorkspaceRoute.Music -> when {
            route.pageId in musicEditingBoardPageIds -> ThynkWorkspaceRoute.Music("music-home")
            route.pageId == "music-home" -> ThynkWorkspaceRoute.Category("music")
            else -> ThynkWorkspaceRoute.Music("music-home")
        }
        is ThynkWorkspaceRoute.Category -> ThynkWorkspaceRoute.Hub
        ThynkWorkspaceRoute.Hub -> ThynkWorkspaceRoute.Hub
    }
}
