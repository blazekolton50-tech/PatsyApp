from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if new in text:
        return
    if old not in text:
        raise SystemExit(f"Expected integration anchor missing in {path}: {old[:80]!r}")
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

# Latest SAVE MAIN APP visible navigation is Camera in the centre.
visual_contract = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalVisualContract.kt"
replace_once(
    visual_contract,
    'val primaryNavigation = listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE")',
    'val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")',
)

# Permanent navigation bar: keep the existing protected CREATE route internally, present it as + CAMERA.
nav_bar = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt"
replace_once(
    nav_bar,
    "/** Locked primary navigation: HOME • THyNK • CREATE • PATSY DMS • PROFILE. */",
    "/** Locked primary navigation: HOME • THyNK • + CAMERA • PATSY DMS • PROFILE. */",
)
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
new_center = '''            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
replace_once(nav_bar, old_center, new_center)

# Home uses its own locked bottom bar. New Design opens THyNK; the centre plus is Camera.
home = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt"
replace_once(
    home,
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.CREATE) })",
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) })",
)
replace_once(home, old_center, new_center.replace("selected == FinalHomeDestination.CREATE", "false"))

print("THyNK Studio / THyNK Music / Camera integration applied")
