from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/MainActivity.kt")
text = PATH.read_text(encoding="utf-8")

import_anchor = "import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter\n"
companion_imports = (
    "import com.patsy.app.patsy.ui.PatsyAction\n"
    "import com.patsy.app.patsy.ui.PatsyMotion\n"
)

if companion_imports not in text:
    if text.count(import_anchor) != 1:
        raise SystemExit("Refusing unsafe patch: Rive import anchor was not unique")
    text = text.replace(import_anchor, import_anchor + companion_imports, 1)

actor_start_marker = "/**\n * Animated Patsy actor architecture."
panel_anchor = "@Composable fun Panel(content:@Composable ColumnScope.()->Unit)"

if text.count(actor_start_marker) != 1:
    raise SystemExit("Refusing unsafe patch: legacy Patsy actor start marker was not unique")
if text.count(panel_anchor) != 1:
    raise SystemExit("Refusing unsafe patch: Panel anchor was not unique")

actor_start = text.index(actor_start_marker)
panel_start = text.index(panel_anchor)
if actor_start >= panel_start:
    raise SystemExit("Refusing unsafe patch: legacy actor block boundaries were invalid")

text = text[:actor_start] + text[panel_start:]

old_welcome = '@Composable fun Welcome(start:()->Unit,login:()->Unit){ Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){ Header(); PatsyMotion("Hey there! 🐾",action=PatsyAction.HAPPY); Text("Let’s get you",fontSize=28.sp,color=White,fontWeight=FontWeight.Bold); Text("all set up…",style=TextStyle(brush=Rainbow,fontSize=30.sp,fontWeight=FontWeight.ExtraBold)); Spacer(Modifier.height(18.dp)); Primary("Get Started  →",start); Spacer(Modifier.height(10.dp)); OutlinedButton(login,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(28.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=White)){Text("I already have an account")}; Spacer(Modifier.height(18.dp)); Text("Patsy’s ready when you are! 🐾",color=Muted) } }'
new_welcome = '@Composable fun Welcome(start:()->Unit,login:()->Unit){ Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){ Header(); PatsyMotion("Hey! Incase you can’t tell, I’m Patsy.",action=PatsyAction.HAPPY); Text("I\'m Patsy. Your personal AI PetPal. Log in and I\'ll show you what I can do!",fontSize=24.sp,color=White,fontWeight=FontWeight.Bold); Spacer(Modifier.height(18.dp)); Primary("Get Started  →",start); Spacer(Modifier.height(10.dp)); OutlinedButton(login,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(28.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=White)){Text("I already have an account")}; Spacer(Modifier.height(18.dp)); Text("Patsy’s ready when you are!",color=Muted) } }'

if text.count(old_welcome) != 1:
    raise SystemExit(f"Refusing unsafe patch: welcome screen matched {text.count(old_welcome)} times; expected exactly 1")
text = text.replace(old_welcome, new_welcome, 1)

if "enum class PatsyAction" in text:
    raise SystemExit("Legacy MainActivity PatsyAction remained after extraction")
if "@Composable fun PatsyMotion(" in text:
    raise SystemExit("Legacy MainActivity PatsyMotion remained after extraction")
if "Text(\"🐾\"" in text or "Text(\"↗\"" in text:
    raise SystemExit("Legacy decorative Patsy motion substitute remained after extraction")
if "I'M Patsy" in text:
    raise SystemExit("Unexpected signup copy mutation")

PATH.write_text(text, encoding="utf-8")
print("Unboxed Patsy companion extraction applied safely.")
