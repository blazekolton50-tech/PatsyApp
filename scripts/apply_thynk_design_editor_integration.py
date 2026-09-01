from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

OLD_BACK = '''                    is ThynkRoute.Editor -> ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == "video" })'''
NEW_BACK = '''                    is ThynkRoute.Editor -> {
                        val categoryId = editorDestinationForPage((route as ThynkRoute.Editor).pageId)?.categoryId
                        categoryId?.let { id ->
                            ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == id })
                        } ?: ThynkRoute.Hub
                    }'''

OLD_RENDER = '''                is ThynkRoute.Editor -> ThynkVideoEditorScreen()'''
NEW_RENDER = '''                is ThynkRoute.Editor -> when (editorDestinationForPage(current.pageId)?.kind) {
                    ThynkEditorKind.DESIGN -> ThynkDesignEditorScreen()
                    ThynkEditorKind.VIDEO -> ThynkVideoEditorScreen()
                    null -> InfoPanel("EDITOR UNAVAILABLE", "This editor route is not configured.")
                }'''


def replace_once(source: str, old: str, new: str, label: str) -> str:
    if new in source:
        return source
    if old not in source:
        raise RuntimeError(f"Expected {label} anchor was not found; refusing to guess at host wiring")
    return source.replace(old, new, 1)


def main() -> None:
    source = HOST.read_text(encoding="utf-8")
    updated = replace_once(source, OLD_BACK, NEW_BACK, "editor Back ownership")
    updated = replace_once(updated, OLD_RENDER, NEW_RENDER, "editor render dispatch")
    if updated != source:
        HOST.write_text(updated, encoding="utf-8")
        print("THyNK Design editor host integration applied")
    else:
        print("THyNK Design editor host integration already applied")


if __name__ == "__main__":
    main()
