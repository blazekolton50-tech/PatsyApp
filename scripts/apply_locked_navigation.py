from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/MainActivity.kt")
text = PATH.read_text(encoding="utf-8")


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"Refusing unsafe Preview C patch: {label} matched {count} times; expected exactly 1")
    text = text.replace(old, new, 1)


replace_once(
    "import com.patsy.app.ui.PatsyHeader\nimport com.patsy.app.ui.PatsyPrimaryButton",
    "import com.patsy.app.ui.PatsyHeader\nimport com.patsy.app.ui.PatsyPrimaryButton\nimport com.patsy.app.ui.PatsyTopMenuAction\nimport com.patsy.app.ui.PatsyWorkspaceHeader",
    "Preview C workspace-header imports",
)

replace_once(
    "enum class Screen { WELCOME, MODE, PROFILE, PASSWORD_SETUP, EMAIL_LINKED, LOGIN, HOME, THYNK, CREATE, DMS, PROFILE_HOME, CHAT, SCHEDULE, MORE, THYNK_TEMPLATES, THYNK_EDITOR, THYNK_AI_IMAGE, THYNK_AI_VIDEO, THYNK_PROJECTS, THYNK_BRAND_KIT, THYNK_INSPIRATION, OWNER_PROFILE, OWNER_TOOLS, SAFE_STATE }",
    "enum class Screen { WELCOME, MODE, PROFILE, PASSWORD_SETUP, EMAIL_LINKED, LOGIN, HOME, THYNK, CAMERA, CREATE, DMS, PROFILE_HOME, CHAT, SCHEDULE, MORE, ACCOUNT, ABOUT, SETTINGS, REMEMBER_ME, THYNK_TEMPLATES, THYNK_EDITOR, THYNK_AI_IMAGE, THYNK_AI_VIDEO, THYNK_PROJECTS, THYNK_BRAND_KIT, THYNK_INSPIRATION, OWNER_PROFILE, OWNER_TOOLS, SAFE_STATE }",
    "Preview C screen enum",
)

replace_once(
    """    Column(Modifier.fillMaxSize()){
        Header()
        Box(Modifier.weight(1f).fillMaxWidth()){""",
    """    Column(Modifier.fillMaxSize()){
        PatsyWorkspaceHeader(
            onAction={action->
                when(action){
                    PatsyTopMenuAction.ACCOUNT->navigate(Screen.ACCOUNT)
                    PatsyTopMenuAction.ABOUT->navigate(Screen.ABOUT)
                    PatsyTopMenuAction.PROFILE->navigate(Screen.PROFILE_HOME)
                    PatsyTopMenuAction.SETTINGS->navigate(Screen.SETTINGS)
                    PatsyTopMenuAction.REMEMBER_ME->navigate(Screen.REMEMBER_ME)
                }
            },
            modifier=Modifier.padding(top=6.dp),
        )
        Box(Modifier.weight(1f).fillMaxWidth()){""",
    "Preview C workspace top bar",
)

replace_once(
    """                Screen.HOME->HomeFeed(profile){navigate(it)}
                Screen.CHAT->Chat()
                Screen.THYNK->ThynkStudioHome{navigate(it)}
                Screen.CREATE->CreateNewHome{navigate(it)}""",
    """                Screen.HOME->HomeFeed(profile){navigate(it)}
                Screen.CHAT->Chat()
                Screen.THYNK->ThynkStudioHome{navigate(it)}
                Screen.CAMERA->CameraCreateHome{navigate(it)}
                Screen.CREATE->CreateNewHome{navigate(it)}""",
    "Camera workspace route",
)

replace_once(
    """                Screen.DMS->Dms()
                Screen.THYNK_TEMPLATES->ThynkSectionPage(""",
    """                Screen.DMS->Dms()
                Screen.ACCOUNT->PreviewCSecondaryPage(
                    title="Account",
                    detail="Account controls stay behind the verified Patsy account/session boundary. Preview C does not grant OWNER authority or bypass production security.",
                    back={selected=Screen.PROFILE_HOME},
                )
                Screen.ABOUT->PreviewCSecondaryPage(
                    title="About",
                    detail="About Patsy App and THyNK Studio. This Preview C surface is for visual walkthrough and does not claim unfinished providers are live.",
                    back={selected=Screen.HOME},
                )
                Screen.SETTINGS->PreviewCSecondaryPage(
                    title="Settings",
                    detail="Settings are connected as a secondary route. Provider, privacy and account settings remain fail-closed until their real integrations are verified.",
                    back={selected=Screen.PROFILE_HOME},
                )
                Screen.REMEMBER_ME->PreviewCSecondaryPage(
                    title="Remember Me",
                    detail="Saved pictures and videos belong here using the approved Remember Me paw asset and user-controlled storage rules. Preview C shows the route without inventing stored media.",
                    back={selected=Screen.PROFILE_HOME},
                )
                Screen.THYNK_TEMPLATES->ThynkSectionPage(""",
    "Preview C three-dot menu pages",
)

replace_once(
    """private fun Screen.routeId():String?=when(this){
    Screen.HOME->"home";Screen.THYNK->"thynk";Screen.CREATE->"create";Screen.DMS->"patsy_dms";Screen.PROFILE_HOME,Screen.MORE->"profile"
    Screen.CHAT->"chat";Screen.SCHEDULE->"schedule";Screen.THYNK_TEMPLATES->"thynk_templates";Screen.THYNK_EDITOR->"thynk_editor"
    Screen.THYNK_AI_IMAGE->"thynk_ai_image";Screen.THYNK_AI_VIDEO->"thynk_ai_video";Screen.THYNK_PROJECTS->"thynk_projects"
    Screen.THYNK_BRAND_KIT->"thynk_brand_kit";Screen.THYNK_INSPIRATION->"thynk_inspiration";Screen.OWNER_PROFILE->"owner_profile";Screen.OWNER_TOOLS->"owner_tools"
    else->null
}""",
    """private fun Screen.routeId():String?=when(this){
    Screen.HOME->"home";Screen.THYNK->"thynk";Screen.CAMERA->"camera";Screen.CREATE->"create";Screen.DMS->"patsy_dms";Screen.PROFILE_HOME,Screen.MORE->"profile"
    Screen.CHAT->"chat";Screen.SCHEDULE->"schedule";Screen.ACCOUNT->"account";Screen.ABOUT->"about";Screen.SETTINGS->"settings";Screen.REMEMBER_ME->"remember_me"
    Screen.THYNK_TEMPLATES->"thynk_templates";Screen.THYNK_EDITOR->"thynk_editor";Screen.THYNK_AI_IMAGE->"thynk_ai_image";Screen.THYNK_AI_VIDEO->"thynk_ai_video";Screen.THYNK_PROJECTS->"thynk_projects"
    Screen.THYNK_BRAND_KIT->"thynk_brand_kit";Screen.THYNK_INSPIRATION->"thynk_inspiration";Screen.OWNER_PROFILE->"owner_profile";Screen.OWNER_TOOLS->"owner_tools"
    else->null
}""",
    "Preview C route ids",
)

replace_once(
    """private fun ShellDestination.toScreen():Screen=when(this){
    ShellDestination.HOME_FEED->Screen.HOME;ShellDestination.THYNK->Screen.THYNK;ShellDestination.CREATE->Screen.CREATE;ShellDestination.DMS->Screen.DMS;ShellDestination.PROFILE->Screen.PROFILE_HOME
    ShellDestination.CHAT->Screen.CHAT;ShellDestination.SCHEDULE->Screen.SCHEDULE;ShellDestination.THYNK_TEMPLATES->Screen.THYNK_TEMPLATES;ShellDestination.THYNK_EDITOR->Screen.THYNK_EDITOR
    ShellDestination.THYNK_AI_IMAGE->Screen.THYNK_AI_IMAGE;ShellDestination.THYNK_AI_VIDEO->Screen.THYNK_AI_VIDEO;ShellDestination.THYNK_PROJECTS->Screen.THYNK_PROJECTS
    ShellDestination.THYNK_BRAND_KIT->Screen.THYNK_BRAND_KIT;ShellDestination.THYNK_INSPIRATION->Screen.THYNK_INSPIRATION;ShellDestination.OWNER_PROFILE->Screen.OWNER_PROFILE;ShellDestination.OWNER_TOOLS->Screen.OWNER_TOOLS
    else->Screen.SAFE_STATE
}""",
    """private fun ShellDestination.toScreen():Screen=when(this){
    ShellDestination.HOME_FEED->Screen.HOME;ShellDestination.THYNK->Screen.THYNK;ShellDestination.CAMERA->Screen.CAMERA;ShellDestination.CREATE->Screen.CREATE;ShellDestination.DMS->Screen.DMS;ShellDestination.PROFILE->Screen.PROFILE_HOME
    ShellDestination.CHAT->Screen.CHAT;ShellDestination.SCHEDULE->Screen.SCHEDULE;ShellDestination.ACCOUNT->Screen.ACCOUNT;ShellDestination.ABOUT->Screen.ABOUT;ShellDestination.SETTINGS->Screen.SETTINGS;ShellDestination.REMEMBER_ME->Screen.REMEMBER_ME
    ShellDestination.THYNK_TEMPLATES->Screen.THYNK_TEMPLATES;ShellDestination.THYNK_EDITOR->Screen.THYNK_EDITOR;ShellDestination.THYNK_AI_IMAGE->Screen.THYNK_AI_IMAGE;ShellDestination.THYNK_AI_VIDEO->Screen.THYNK_AI_VIDEO;ShellDestination.THYNK_PROJECTS->Screen.THYNK_PROJECTS
    ShellDestination.THYNK_BRAND_KIT->Screen.THYNK_BRAND_KIT;ShellDestination.THYNK_INSPIRATION->Screen.THYNK_INSPIRATION;ShellDestination.OWNER_PROFILE->Screen.OWNER_PROFILE;ShellDestination.OWNER_TOOLS->Screen.OWNER_TOOLS
    else->Screen.SAFE_STATE
}""",
    "Preview C destination mappings",
)

replace_once(
    """@Composable fun AppNavigationBar(selected:Screen,onNavigate:(Screen)->Unit){
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF0A0A0B)).padding(6.dp),
        horizontalArrangement=Arrangement.SpaceEvenly,
    ){
        ShellNavigationContract.primaryRoutes.forEach{route->
            val s=route.destination.toScreen();val t=route.label
            TextButton(onClick={onNavigate(s)}){
                Text(t,color=if(selected==s)White else Muted,fontSize=11.sp)
            }
        }
    }
}""",
    """@Composable fun AppNavigationBar(selected:Screen,onNavigate:(Screen)->Unit){
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF151518)).padding(horizontal=4.dp,vertical=6.dp),
        horizontalArrangement=Arrangement.SpaceEvenly,
        verticalAlignment=Alignment.CenterVertically,
    ){
        PreviewPrimaryNavItem(Screen.HOME,"⌂","HOME",selected,onNavigate)
        PreviewPrimaryNavItem(Screen.THYNK,"TH","THyNK",selected,onNavigate)
        Box(Modifier.weight(1f),contentAlignment=Alignment.Center){
            Button(
                onClick={onNavigate(Screen.CAMERA)},
                modifier=Modifier.size(56.dp),
                shape=RoundedCornerShape(28.dp),
                contentPadding=PaddingValues(0.dp),
                colors=ButtonDefaults.buttonColors(containerColor=White,contentColor=Color.Black),
            ){
                Text("+",color=Color.Black,fontSize=28.sp,fontWeight=FontWeight.Black)
            }
        }
        PreviewPrimaryNavItem(Screen.DMS,"✉","PATSY DMs",selected,onNavigate)
        PreviewPrimaryNavItem(Screen.PROFILE_HOME,"◍","PROFILE",selected,onNavigate)
    }
}

@Composable
private fun RowScope.PreviewPrimaryNavItem(
    screen:Screen,
    icon:String,
    label:String,
    selected:Screen,
    onNavigate:(Screen)->Unit,
){
    TextButton(
        onClick={onNavigate(screen)},
        modifier=Modifier.weight(1f),
        contentPadding=PaddingValues(horizontal=2.dp,vertical=2.dp),
    ){
        Column(horizontalAlignment=Alignment.CenterHorizontally){
            Text(icon,color=if(selected==screen)White else Muted,fontSize=17.sp,fontWeight=FontWeight.Bold)
            Text(label,color=if(selected==screen)White else Muted,fontSize=9.sp,maxLines=1)
        }
    }
}""",
    "Preview C bottom navigation",
)

if 'ShellNavigationContract.primaryRoutes.forEach' in text:
    raise SystemExit("Legacy generated primary navigation remained after Preview C patch")
if 'Screen.CAMERA->"camera"' not in text:
    raise SystemExit("Camera route was not connected")
if 'PatsyWorkspaceHeader(' not in text:
    raise SystemExit("Preview C top-right menu header was not connected")

PATH.write_text(text, encoding="utf-8")
print("Preview C locked navigation, camera, header menu and routes applied safely.")
