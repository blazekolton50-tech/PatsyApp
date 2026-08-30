from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/MainActivity.kt")
text = PATH.read_text(encoding="utf-8")

import_anchor = "import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter\n"
import_block = (
    import_anchor
    + "import com.patsy.app.ui.PatsyHeader\n"
    + "import com.patsy.app.ui.PatsyPrimaryButton\n"
)
old_header = '@Composable fun Header(){ Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth().padding(top=18.dp)){ Image(painter=painterResource(R.drawable.patsy_logo_official_white),contentDescription="Patsy",modifier=Modifier.width(190.dp).height(88.dp),contentScale=ContentScale.Fit); Text("YOUR AI. YOUR WORKSPACE. YOUR CONTROL.",fontSize=10.sp,color=Muted,letterSpacing=1.sp) } }'
new_header = '@Composable fun Header(){ PatsyHeader(modifier=Modifier.padding(top=18.dp)) }'
old_primary = '@Composable fun Primary(text:String,onClick:()->Unit,enabled:Boolean=true){ Button(onClick=onClick,enabled=enabled,modifier=Modifier.fillMaxWidth().height(58.dp),colors=ButtonDefaults.buttonColors(containerColor=White,contentColor=Color.Black),shape=RoundedCornerShape(30.dp)){ Text(text,fontWeight=FontWeight.Bold,fontSize=16.sp) } }'
new_primary = '@Composable fun Primary(text:String,onClick:()->Unit,enabled:Boolean=true){ PatsyPrimaryButton(text=text,onClick=onClick,enabled=enabled) }'

targets = {
    "Rive import anchor": (import_anchor, import_block),
    "legacy Header wrapper": (old_header, new_header),
    "legacy Primary wrapper": (old_primary, new_primary),
}

for name, (old, _) in targets.items():
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"Refusing unsafe patch: {name} matched {count} times; expected exactly 1")

for old, new in targets.values():
    text = text.replace(old, new, 1)

if "YOUR AI. YOUR WORKSPACE. YOUR CONTROL." in text:
    raise SystemExit("Legacy tagline remained after patch")

PATH.write_text(text, encoding="utf-8")
print("Locked wrapper migration applied safely.")
