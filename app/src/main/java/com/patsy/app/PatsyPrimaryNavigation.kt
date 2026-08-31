package com.patsy.app

data class PatsyPrimaryNavigationItem(
    val label: String,
    val screen: Screen,
)

object PatsyPrimaryNavigation {
    val items = listOf(
        PatsyPrimaryNavigationItem("HOME", Screen.HOME),
        PatsyPrimaryNavigationItem("THyNK", Screen.CHAT),
        PatsyPrimaryNavigationItem("CREATE", Screen.CREATE),
        PatsyPrimaryNavigationItem("PATSY DMS", Screen.DMS),
        PatsyPrimaryNavigationItem("PROFILE", Screen.MORE),
    )

    init {
        require(items.size == 5) { "Primary navigation locked to 5" }
        require(items.map { it.label }.distinct().size == items.size) { "Labels must be distinct" }
    }
}
