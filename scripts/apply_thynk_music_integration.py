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
    if new in text or old not in text:
        return
    file.write_text(text.replace(old, new, 1), encoding="utf-8")


# Preserve the secure FinalMainActivity shell; only swap the destination surfaces.
final_main = "app/src/main/java/com/patsy/app/FinalMainActivity.kt"
replace_once(
    final_main,
    "import com.patsy.app.ui.finaldesign.FinalWhite\n",
    "import com.patsy.app.ui.finaldesign.FinalWhite\nimport com.patsy.app.thynk.LockedCameraHub\nimport com.patsy.app.thynk.ThynkStudioScreen\n",
)
replace_once(final_main, "FinalAppPage.THYNK -> Chat()", "FinalAppPage.THYNK -> ThynkStudioScreen()")
replace_once(final_main, "FinalAppPage.CREATE -> CreateStudio()", "FinalAppPage.CREATE -> LockedCameraHub()")
replace_if_present(
    final_main,
    "import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar\n",
    "import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar\nimport com.patsy.app.ui.finaldesign.FinalVisualContract\n",
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

# Home keeps the approved same navigation visual.
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

# SAVE MAIN APP / LOCK IN SAVE: the same navigation system is visible on ALL pages.
# Primary pages already own the bar. Add the same bar to account/Owner/protected pages without
# weakening auth or capability gates. Logged-out auth pages show it disabled.
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
                    page !in listOf(
                        FinalAppPage.HOME,
                        FinalAppPage.THYNK,
                        FinalAppPage.CREATE,
                        FinalAppPage.DMS,
                        FinalAppPage.PROFILE,
                    )
                ) {
                    val selectedDestination = when (page) {
                        FinalAppPage.OWNER_PROFILE,
                        FinalAppPage.OWNER_TOOLS -> FinalHomeDestination.PROFILE
                        else -> null
                    }
                    FinalPrimaryNavigationBar(
                        selected = selectedDestination,
                        onNavigate = { destination ->
                            if (session == null && !debugPreview) {
                                page = FinalAppPage.LOGIN
                            } else {
                                navigate(destination)
                            }
                        },
                        enabled = session != null || debugPreview,
                    )
                }
            }
        }
    }
}
'''
replace_if_present(final_main, global_shell_close_old, global_shell_close_new)

print("THyNK Studio / THyNK Music / Camera / global navigation integration applied")
