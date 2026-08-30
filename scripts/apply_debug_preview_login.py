from pathlib import Path

path = Path("app/src/main/java/com/patsy/app/MainActivity.kt")
text = path.read_text(encoding="utf-8")

anchor = '''            TextButton(onClick={
                val identifier=AuthValidation.loginIdentifier(user)
'''
insert = '''            if(debugPreviewEnabled){
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick={ createDebugPreviewSession()?.let(onDone) },
                    modifier=Modifier.fillMaxWidth(),
                    shape=RoundedCornerShape(28.dp),
                    colors=ButtonDefaults.outlinedButtonColors(contentColor=White),
                ){
                    Text("Preview app (DEBUG ONLY)")
                }
                Text(
                    "Development preview only — this does not sign in, verify email, or grant OWNER access.",
                    color=Muted,
                    fontSize=11.sp,
                    modifier=Modifier.padding(top=4.dp),
                )
            }
            TextButton(onClick={
                val identifier=AuthValidation.loginIdentifier(user)
'''

if text.count(anchor) != 1:
    raise SystemExit(f"Refusing unsafe patch: login reset anchor matched {text.count(anchor)} times; expected 1")
if "Preview app (DEBUG ONLY)" in text:
    raise SystemExit("Preview login action already exists")

text = text.replace(anchor, insert, 1)
path.write_text(text, encoding="utf-8")
print("Debug-only preview login action applied.")
