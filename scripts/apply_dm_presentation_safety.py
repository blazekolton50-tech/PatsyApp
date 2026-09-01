from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileDmScreens.kt")

OLD = '''                            if (thread.unreadCount > 0) Text(thread.unreadCount.toString(), color = FinalWhite, fontSize = 11.sp)'''
NEW = '''                            thread.unreadCount?.takeIf { it > 0 }?.let { unread ->
                                Text(unread.toString(), color = FinalWhite, fontSize = 11.sp)
                            }'''


def main() -> None:
    source = PATH.read_text(encoding="utf-8")
    if NEW in source:
        print("DM nullable unread badge safety already applied")
        return
    if OLD not in source:
        raise RuntimeError("Expected DM unread badge anchor missing; refusing blind presentation rewrite")
    PATH.write_text(source.replace(OLD, NEW, 1), encoding="utf-8")
    print("DM nullable unread badge safety applied")


if __name__ == "__main__":
    main()
