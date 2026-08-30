from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/MainActivity.kt")
text = PATH.read_text(encoding="utf-8")

replacements = [
    (
        'enum class Screen { WELCOME, MODE, PROFILE, PASSWORD_SETUP, EMAIL_LINKED, LOGIN, HOME, CHAT, CREATE, SCHEDULE, MORE, DMS, OWNER_PROFILE, OWNER_TOOLS }',
        'enum class Screen { WELCOME, MODE, PROFILE, PASSWORD_SETUP, EMAIL_LINKED, LOGIN, HOME, THYNK, CREATE, DMS, PROFILE_HOME, CHAT, SCHEDULE, MORE, THYNK_TEMPLATES, THYNK_EDITOR, THYNK_AI_IMAGE, THYNK_AI_VIDEO, THYNK_PROJECTS, THYNK_BRAND_KIT, THYNK_INSPIRATION, OWNER_PROFILE, OWNER_TOOLS }',
        "screen enum",
    ),
    (
        'Screen.HOME->Home(profile){selected=it}',
        'Screen.HOME->HomeFeed(profile){selected=it}',
        "Home feed route",
    ),
    (
        'Screen.CREATE->CreateStudio()',
        'Screen.THYNK->ThynkStudioHome{selected=it}\n                Screen.CREATE->CreateNewHome{selected=it}',
        "THyNK/Create primary routes",
    ),
    (
        'Screen.MORE->More(',
        'Screen.PROFILE_HOME,Screen.MORE->More(',
        "Profile route",
    ),
    (
        '                Screen.DMS->Dms()\n',
        '''                Screen.DMS->Dms()\n                Screen.THYNK_TEMPLATES->ThynkSectionPage(\n                    title="Templates",\n                    detail="The THyNK template library route is connected. Editable template content is loaded only from approved Patsy assets; the full 100-image / 50-video target is not being pretended complete.",\n                    back={selected=Screen.THYNK},\n                )\n                Screen.THYNK_EDITOR->CreateStudio()\n                Screen.THYNK_AI_IMAGE->ThynkSectionPage(\n                    title="AI Image Generator",\n                    detail="This page is connected to the THyNK flow. A production image-generation provider is not configured yet, so no fake generated result is shown.",\n                    back={selected=Screen.THYNK},\n                )\n                Screen.THYNK_AI_VIDEO->ThynkSectionPage(\n                    title="AI Video Generator",\n                    detail="This is the locked 10-second video workflow route. The production video provider is not configured yet, so generation remains unavailable rather than simulated.",\n                    back={selected=Screen.THYNK},\n                )\n                Screen.THYNK_PROJECTS->ThynkSectionPage(\n                    title="My Projects",\n                    detail="Project continuation is connected at the UI route. Cross-device project persistence remains a backend integration task.",\n                    back={selected=Screen.THYNK},\n                )\n                Screen.THYNK_BRAND_KIT->ThynkSectionPage(\n                    title="Brand Kit",\n                    detail="The Brand Kit route is connected and reserved for user-owned brand assets, colours, fonts and approved reusable elements.",\n                    back={selected=Screen.THYNK},\n                )\n                Screen.THYNK_INSPIRATION->ThynkSectionPage(\n                    title="Inspiration",\n                    detail="The Inspiration route is connected. Live AI/search suggestions will remain unavailable until the secure provider gateway is configured.",\n                    back={selected=Screen.THYNK},\n                )\n''',
        "THyNK secondary routes",
    ),
    (
        'OwnerProfile(profile){selected=Screen.MORE} else OwnerAccessDenied{selected=Screen.MORE}',
        'OwnerProfile(profile){selected=Screen.PROFILE_HOME} else OwnerAccessDenied{selected=Screen.PROFILE_HOME}',
        "Owner Profile back route",
    ),
    (
        'OwnerTools{selected=Screen.MORE} else OwnerAccessDenied{selected=Screen.MORE}',
        'OwnerTools{selected=Screen.PROFILE_HOME} else OwnerAccessDenied{selected=Screen.PROFILE_HOME}',
        "Owner Tools back route",
    ),
    (
        'else->Home(profile){selected=it}',
        'else->HomeFeed(profile){selected=it}',
        "fallback Home route",
    ),
    (
        'Text("CREATION STUDIO",fontSize=24.sp,fontWeight=FontWeight.Bold)',
        'Text("THyNK EDITOR",fontSize=24.sp,fontWeight=FontWeight.Bold)',
        "THyNK editor title",
    ),
    (
        '@Composable fun AppNavigationBar(selected:Screen,onNavigate:(Screen)->Unit){ Row(Modifier.fillMaxWidth().background(Color(0xFF0A0A0B)).padding(6.dp),horizontalArrangement=Arrangement.SpaceEvenly){listOf(Screen.HOME to "⌂\\nHome",Screen.CHAT to "💬\\nChat",Screen.CREATE to "✎\\nCreate",Screen.SCHEDULE to "▣\\nSchedule",Screen.MORE to "•••\\nMore").forEach{(s,t)->TextButton(onClick={onNavigate(s)}){Text(t,color=if(selected==s)White else Muted,fontSize=11.sp)}}} }',
        '''@Composable fun AppNavigationBar(selected:Screen,onNavigate:(Screen)->Unit){\n    Row(\n        Modifier.fillMaxWidth().background(Color(0xFF0A0A0B)).padding(6.dp),\n        horizontalArrangement=Arrangement.SpaceEvenly,\n    ){\n        listOf(\n            Screen.HOME to "Home",\n            Screen.THYNK to "THyNK",\n            Screen.CREATE to "Create",\n            Screen.DMS to "Patsy DMs",\n            Screen.PROFILE_HOME to "Profile",\n        ).forEach{(s,t)->\n            TextButton(onClick={onNavigate(s)}){\n                Text(t,color=if(selected==s)White else Muted,fontSize=11.sp)\n            }\n        }\n    }\n}''',
        "locked bottom navigation",
    ),
]

for old, new, label in replacements:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"Refusing unsafe patch: {label} matched {count} times; expected exactly 1")
    text = text.replace(old, new, 1)

if 'Screen.CHAT to "💬\\nChat"' in text or 'Screen.SCHEDULE to "▣\\nSchedule"' in text:
    raise SystemExit("Legacy primary navigation remained after patch")

PATH.write_text(text, encoding="utf-8")
print("Latest locked Patsy page connections applied safely.")
