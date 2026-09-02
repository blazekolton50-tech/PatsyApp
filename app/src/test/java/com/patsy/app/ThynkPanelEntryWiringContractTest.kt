package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ThynkPanelEntryWiringContractTest {
    @Test
    fun `secure shell preserves distinct THyNK Music and IT panel entries`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
        val studio = source("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

        assertTrue(activity.contains("ThynkPrimaryNavigationBar("))
        assertTrue(activity.contains("var thynkEntry by remember"))
        assertTrue(activity.contains("ThynkPanelDestination.MUSIC ->"))
        assertTrue(activity.contains("thynkEntry = ThynkStudioEntry.MUSIC"))
        assertTrue(activity.contains("ThynkPanelDestination.IT ->"))
        assertTrue(activity.contains("thynkEntry = ThynkStudioEntry.IT"))
        assertTrue(activity.contains("ThynkStudioScreen(\n                                        entry = thynkEntry,"))

        assertTrue(studio.contains("entry: ThynkStudioEntry"))
        assertTrue(studio.contains("ThynkWorkspaceNavigation.initialRoute(entry)"))
        assertTrue(studio.contains("LaunchedEffect(entry)"))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}
