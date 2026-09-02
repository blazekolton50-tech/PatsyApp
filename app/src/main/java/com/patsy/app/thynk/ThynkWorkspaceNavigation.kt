package com.patsy.app.thynk

/** Which specialist THyNK hub the five-logo app panel opened. */
enum class ThynkStudioEntry {
    MUSIC,
    IT,
}

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

internal data class ThynkWorkspaceChrome(
    val showBack: Boolean,
    val showOverflowHome: Boolean,
    val showGlobalHomebar: Boolean,
)

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

    fun initialRoute(entry: ThynkStudioEntry): ThynkWorkspaceRoute = when (entry) {
        ThynkStudioEntry.IT -> ThynkWorkspaceRoute.Hub
        ThynkStudioEntry.MUSIC -> ThynkWorkspaceRoute.Music("music-home")
    }

    fun isEditingBoard(route: ThynkWorkspaceRoute): Boolean = when (route) {
        is ThynkWorkspaceRoute.Editor -> true
        is ThynkWorkspaceRoute.Music -> route.pageId in musicEditingBoardPageIds
        ThynkWorkspaceRoute.Hub,
        is ThynkWorkspaceRoute.Category -> false
    }

    fun chrome(route: ThynkWorkspaceRoute): ThynkWorkspaceChrome {
        val editingBoard = isEditingBoard(route)
        val root = route is ThynkWorkspaceRoute.Hub ||
            (route is ThynkWorkspaceRoute.Music && route.pageId == "music-home")
        return ThynkWorkspaceChrome(
            showBack = !root,
            showOverflowHome = editingBoard,
            showGlobalHomebar = !editingBoard,
        )
    }

    fun back(route: ThynkWorkspaceRoute): ThynkWorkspaceRoute = when (route) {
        is ThynkWorkspaceRoute.Editor -> ThynkWorkspaceRoute.Category(route.categoryId)
        is ThynkWorkspaceRoute.Music -> when {
            route.pageId == "music-home" -> route
            else -> ThynkWorkspaceRoute.Music("music-home")
        }
        is ThynkWorkspaceRoute.Category -> ThynkWorkspaceRoute.Hub
        ThynkWorkspaceRoute.Hub -> ThynkWorkspaceRoute.Hub
    }
}
