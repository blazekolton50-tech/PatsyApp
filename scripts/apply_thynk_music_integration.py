from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if new in text:
        return
    if old not in text:
        raise SystemExit(f"Expected integration anchor missing in {path}: {old[:80]!r}")
    file.write_text(text.replace(old, new, 1), encoding="utf-8")


def replace_if_present(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if (new and new in text) or old not in text:
        return
    file.write_text(text.replace(old, new, 1), encoding="utf-8")


def ensure_import(path: str, anchor_import: str, import_line: str) -> None:
    """Insert one Kotlin import only when that exact import is absent.

    Integration runs on branches whose imports may be reordered by native feature work, so checking
    for a multi-import adjacent block is not idempotent. Exact-line presence is the authority.
    """
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    exact = import_line.rstrip("\n") + "\n"
    if exact in text:
        return
    anchor = anchor_import.rstrip("\n") + "\n"
    if anchor not in text:
        raise SystemExit(f"Expected import anchor missing in {path}: {anchor_import!r}")
    file.write_text(text.replace(anchor, anchor + exact, 1), encoding="utf-8")


def remove_from_marker(path: str, marker: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if marker not in text:
        return
    file.write_text(text.split(marker, 1)[0].rstrip() + "\n", encoding="utf-8")


# Preserve the secure FinalMainActivity shell; only swap the destination surfaces.
final_main = "app/src/main/java/com/patsy/app/FinalMainActivity.kt"
ensure_import(
    final_main,
    "import com.patsy.app.ui.finaldesign.FinalWhite",
    "import com.patsy.app.thynk.LockedCameraHub",
)
ensure_import(
    final_main,
    "import com.patsy.app.thynk.LockedCameraHub",
    "import com.patsy.app.thynk.ThynkStudioScreen",
)
replace_once(final_main, "FinalAppPage.THYNK -> Chat()", "FinalAppPage.THYNK -> ThynkStudioScreen()")
replace_once(final_main, "FinalAppPage.CREATE -> CreateStudio()", "FinalAppPage.CREATE -> LockedCameraHub()")
ensure_import(
    final_main,
    "import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar",
    "import com.patsy.app.ui.finaldesign.FinalVisualContract",
)

# Latest SAVE MAIN APP semantic navigation keeps Camera in the centre.
visual_contract = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalVisualContract.kt"
replace_once(
    visual_contract,
    'val primaryNavigation = listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE")',
    'val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")',
)

# Exact approved navigation visual:
# Home • THyNK mark only • large centre + • PDMs • Profile.
# Keep semantic routes underneath, but do not restore superseded secondary labels.
nav_bar = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt"
old_center = '''            Box(
                Modifier
                    .size(66.dp)
                    .background(FinalRainbow, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onNavigate(FinalHomeDestination.CREATE) },
                contentAlignment = Alignment.Center,
            ) {
                Text("+", color = Color.Black, fontSize = 38.sp, fontWeight = FontWeight.Black)
            }
'''
camera_center = '''            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(60.dp)
                        .background(FinalRainbow, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigate(FinalHomeDestination.CREATE) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", color = Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
                Text("CAMERA", color = if (selected == FinalHomeDestination.CREATE) Color(0xFFFF56AB) else FinalMuted, fontSize = 8.sp, maxLines = 1)
            }
'''
plus_only_center = '''            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(60.dp)
                        .background(FinalRainbow, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigate(FinalHomeDestination.CREATE) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", color = Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
            }
'''
replace_if_present(nav_bar, old_center, plus_only_center)
replace_if_present(nav_bar, camera_center, plus_only_center)
replace_if_present(
    nav_bar,
    'FinalNavItem("THyNK", "THyNK", selected == FinalHomeDestination.THYNK)',
    'FinalNavItem("THyNK", "", selected == FinalHomeDestination.THYNK)',
)
replace_if_present(
    nav_bar,
    'FinalNavItem("◌", "PATSY DMS", selected == FinalHomeDestination.PATSY_DMS)',
    'FinalNavItem("◌", "PDMs", selected == FinalHomeDestination.PATSY_DMS)',
)

# Home keeps the approved content, but no longer owns a private bottom-navigation copy.
home = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt"
replace_once(
    home,
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.CREATE) })",
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) })",
)
replace_if_present(home, old_center, plus_only_center)
replace_if_present(home, camera_center.replace("selected == FinalHomeDestination.CREATE", "false"), plus_only_center)
replace_if_present(
    home,
    'NavItem("THyNK", "THyNK", false)',
    'NavItem("THyNK", "", false)',
)
replace_if_present(
    home,
    'NavItem("◌", "PATSY DMS", false)',
    'NavItem("◌", "PDMs", false)',
)
replace_if_present(
    home,
    '''        FinalHomeBottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            onNavigate = onNavigate,
        )
''',
    '',
)
remove_from_marker(home, "\n@Composable\nprivate fun FinalHomeBottomNavigation(")

# Route the current VIDEO & CAMERA category to the shared Media3 editor without
# importing the obsolete PR #23 THyNK screen or inventing sample media.
thynk_screen = "app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt"
replace_once(
    thynk_screen,
    "import com.patsy.app.ui.finaldesign.FinalWhite\n",
    "import com.patsy.app.ui.finaldesign.FinalWhite\nimport com.patsy.app.studio.StudioEditorState\nimport com.patsy.app.studio.StudioVideoPlayer\nimport com.patsy.app.studio.reduceStudioState\n",
)
replace_once(
    thynk_screen,
    '''private sealed interface ThynkRoute {
    data object Hub : ThynkRoute
    data class Category(val category: ThynkCategory) : ThynkRoute
    data class Music(val pageId: String) : ThynkRoute
}
''',
    '''private sealed interface ThynkRoute {
    data object Hub : ThynkRoute
    data class Category(val category: ThynkCategory) : ThynkRoute
    data class Music(val pageId: String) : ThynkRoute
    data class Editor(val pageId: String) : ThynkRoute
}
''',
)
replace_once(
    thynk_screen,
    '''                route = when (route) {
                    is ThynkRoute.Music -> ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == "music" })
                    is ThynkRoute.Category -> ThynkRoute.Hub
                    ThynkRoute.Hub -> ThynkRoute.Hub
                }
''',
    '''                route = when (route) {
                    is ThynkRoute.Music -> ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == "music" })
                    is ThynkRoute.Editor -> ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == "video" })
                    is ThynkRoute.Category -> ThynkRoute.Hub
                    ThynkRoute.Hub -> ThynkRoute.Hub
                }
''',
)
replace_once(
    thynk_screen,
    '''                is ThynkRoute.Category -> ThynkCategoryScreen(current.category) { item ->
                    if (current.category.id == "music") {
                        route = ThynkRoute.Music(musicPageForItem(item))
                    }
                }
                is ThynkRoute.Music -> ThynkMusicScreen(
                    pageId = current.pageId,
                    onOpenPage = { route = ThynkRoute.Music(it) },
                )
''',
    '''                is ThynkRoute.Category -> ThynkCategoryScreen(current.category) { item ->
                    if (current.category.id == "music") {
                        route = ThynkRoute.Music(musicPageForItem(item))
                    } else {
                        editorPageForThynkItem(item)?.let { editorPage ->
                            route = ThynkRoute.Editor(editorPage)
                        }
                    }
                }
                is ThynkRoute.Music -> ThynkMusicScreen(
                    pageId = current.pageId,
                    onOpenPage = { route = ThynkRoute.Music(it) },
                )
                is ThynkRoute.Editor -> ThynkVideoEditorScreen()
''',
)
replace_once(
    thynk_screen,
    '''private fun musicPageForItem(item: String): String = when (item) {
''',
    '''@Composable
private fun ThynkVideoEditorScreen() {
    var editorState by remember { mutableStateOf(StudioEditorState.video(durationMs = 0)) }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("VIDEO EDITOR", color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
        Text(
            "Preview and edit a real selected video clip. No sample media is substituted.",
            color = FinalMuted,
            fontSize = 12.sp,
        )
        StudioVideoPlayer(
            sourceUri = editorState.sourceUri,
            state = editorState,
            onAction = { action -> editorState = reduceStudioState(editorState, action) },
            modifier = Modifier.fillMaxWidth(),
        )
        InfoPanel(
            "MEDIA",
            "No video is loaded yet. Android media picking is the next verified slice; this editor remains truthfully EMPTY until a real URI is selected.",
        )
    }
}

private fun musicPageForItem(item: String): String = when (item) {
''',
)

# SAVE MAIN APP / LOCK IN SAVE: one canonical primary bar is owned by the outer authenticated
# shell. Login / Set Password remain pre-auth and never expose functional authenticated routes.
global_shell_open_old = '''        Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {
            when (page) {
'''
global_shell_open_new = '''        Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {
            Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when (page) {
'''
replace_if_present(final_main, global_shell_open_old, global_shell_open_new)

global_shell_close_old = '''                FinalAppPage.PROTECTED -> FinalProtectedAccessState(
                    message = "This area isn't available until secure account access is verified.",
                    onSignOut = ::signOut,
                )
            }
        }
    }
}
'''
global_shell_close_new = '''                FinalAppPage.PROTECTED -> FinalProtectedAccessState(
                    message = "This area isn't available until secure account access is verified.",
                    onSignOut = ::signOut,
                )
                    }
                }

                if (
                    FinalVisualContract.navigationVisibleOnAllPages &&
                    page !in listOf(FinalAppPage.LOGIN, FinalAppPage.DEBUG_SET_PASSWORD) &&
                    (session != null || debugPreview)
                ) {
                    val selectedDestination = when (page) {
                        FinalAppPage.HOME -> FinalHomeDestination.HOME
                        FinalAppPage.THYNK -> FinalHomeDestination.THYNK
                        FinalAppPage.CREATE -> FinalHomeDestination.CREATE
                        FinalAppPage.DMS -> FinalHomeDestination.PATSY_DMS
                        FinalAppPage.PROFILE,
                        FinalAppPage.OWNER_PROFILE,
                        FinalAppPage.OWNER_TOOLS -> FinalHomeDestination.PROFILE
                        FinalAppPage.PROTECTED -> null
                        FinalAppPage.LOGIN,
                        FinalAppPage.DEBUG_SET_PASSWORD -> null
                    }
                    FinalPrimaryNavigationBar(
                        selected = selectedDestination,
                        onNavigate = ::navigate,
                    )
                }
            }
        }
    }
}
'''
replace_if_present(final_main, global_shell_close_old, global_shell_close_new)

# Upgrade branches that already had the earlier outer-shell version so every authenticated
# primary destination is included in that one shell instead of owning another copy.
replace_if_present(
    final_main,
    '''                    page !in listOf(
                        FinalAppPage.HOME,
                        FinalAppPage.THYNK,
                        FinalAppPage.CREATE,
                        FinalAppPage.DMS,
                        FinalAppPage.PROFILE,
                    )
''',
    '''                    page !in listOf(FinalAppPage.LOGIN, FinalAppPage.DEBUG_SET_PASSWORD) &&
                    (session != null || debugPreview)
''',
)
replace_if_present(
    final_main,
    '''                    val selectedDestination = when (page) {
                        FinalAppPage.OWNER_PROFILE,
                        FinalAppPage.OWNER_TOOLS -> FinalHomeDestination.PROFILE
                        else -> null
                    }
''',
    '''                    val selectedDestination = when (page) {
                        FinalAppPage.HOME -> FinalHomeDestination.HOME
                        FinalAppPage.THYNK -> FinalHomeDestination.THYNK
                        FinalAppPage.CREATE -> FinalHomeDestination.CREATE
                        FinalAppPage.DMS -> FinalHomeDestination.PATSY_DMS
                        FinalAppPage.PROFILE,
                        FinalAppPage.OWNER_PROFILE,
                        FinalAppPage.OWNER_TOOLS -> FinalHomeDestination.PROFILE
                        else -> null
                    }
''',
)
replace_if_present(
    final_main,
    '''                        onNavigate = { destination ->
                            if (session == null && !debugPreview) {
                                page = FinalAppPage.LOGIN
                            } else {
                                navigate(destination)
                            }
                        },
                        enabled = session != null || debugPreview,
''',
    '''                        onNavigate = ::navigate,
''',
)

# Remove the primary-page inner copy; authorization and Owner gates around the content stay intact.
replace_if_present(
    final_main,
    '''                    } else {
                        Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                when (page) {
''',
    '''                    } else {
                        when (page) {
''',
)
replace_if_present(
    final_main,
    '''                                    else -> Unit
                                }
                            }
                            FinalPrimaryNavigationBar(
                                selected = selectedDestination,
                                onNavigate = ::navigate,
                            )
                        }
''',
    '''                            else -> Unit
                        }
''',
)

print("THyNK Studio / THyNK Music / Camera / single-owner global navigation integration applied")