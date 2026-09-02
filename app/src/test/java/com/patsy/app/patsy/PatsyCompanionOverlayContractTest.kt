package com.patsy.app.patsy

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class PatsyCompanionOverlayContractTest {
    @Test
    fun `authenticated shell hosts one full-screen Patsy travel overlay`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
        val overlay = source("app/src/main/java/com/patsy/app/patsy/ui/PatsyCompanionOverlay.kt")

        assertTrue(activity.contains("PatsyCompanionOverlay("))
        assertTrue(activity.contains("var patsyCommand by remember"))
        assertTrue(activity.contains("patsyCommand = PatsyCompanionCommand.GuideTo("))
        assertTrue(activity.contains("patsyCommand = PatsyCompanionCommand.ReturnHome"))

        assertTrue(overlay.contains("Modifier.fillMaxSize()"))
        assertTrue(overlay.contains("PatsyRiveHost("))
        assertTrue(overlay.contains("PatsyCompanionController("))
        assertTrue(overlay.contains("controller.guideTo(command.target)"))
        assertTrue(overlay.contains("controller.returnHome()"))
        assertTrue(overlay.contains("R.drawable.patsy_generated_main"))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}
