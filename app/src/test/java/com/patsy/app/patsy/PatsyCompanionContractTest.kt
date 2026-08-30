package com.patsy.app.patsy

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PatsyCompanionContractTest {
    @Test
    fun companionIsExtractedFromMainActivityWithoutChangingRigBoundary() {
        val mainActivity = File("src/main/java/com/patsy/app/MainActivity.kt").readText()
        val companion = File("src/main/java/com/patsy/app/patsy/PatsyCompanion.kt")

        assertTrue(companion.exists(), "PatsyCompanion.kt must exist as a dedicated companion integration boundary")
        assertFalse(mainActivity.contains("fun PatsyMotion("), "MainActivity must no longer own the Patsy companion renderer")

        val source = companion.readText()
        assertTrue(source.contains("PatsyRigCoordinator"), "Companion must preserve the rig coordinator boundary")
        assertTrue(source.contains("PatsyRiveHost"), "Companion must preserve the real Rive host boundary")
        assertTrue(source.contains("PatsyRiveRuntimeAdapter"), "Companion must preserve the fail-safe Rive runtime adapter")
    }
}
