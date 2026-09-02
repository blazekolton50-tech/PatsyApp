package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PatsyCanonicalShrinkTest {

    @Test
    fun canonicalShrinkUsesMiniScalePointFiveForEightHundredMilliseconds() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        var currentMode = PatsyCompanionMode.IDLE
        var shrinkWaitMillis = 0L

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = { state ->
                observed += state
                currentMode = state.mode
            },
            frameWait = { millis ->
                if (currentMode == PatsyCompanionMode.SHRINKING) {
                    shrinkWaitMillis += millis
                }
            },
        )

        controller.guideTo(PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f))

        val finalShrinkState = observed.last { it.mode == PatsyCompanionMode.SHRINKING }
        assertEquals(0.50f, finalShrinkState.pose.stageScale)
        assertEquals(800L, shrinkWaitMillis)
        assertEquals(0.50f, controller.state.pose.stageScale)
    }

    @Test
    fun canonicalShrinkRunsRightToMissionForFourHundredMilliseconds() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        var currentMode = PatsyCompanionMode.IDLE
        var travelWaitMillis = 0L
        val mission = PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f)

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = { state ->
                observed += state
                currentMode = state.mode
            },
            frameWait = { millis ->
                if (currentMode == PatsyCompanionMode.TRAVELLING) {
                    travelWaitMillis += millis
                }
            },
        )

        controller.guideTo(mission)

        val firstTravelState = observed.first { it.mode == PatsyCompanionMode.TRAVELLING }
        assertEquals("run", firstTravelState.pose.motion.riveValue)
        assertEquals(1f, firstTravelState.pose.facing)
        assertEquals(400L, travelWaitMillis)
        assertEquals(0.50f, firstTravelState.pose.stageScale)
    }

    @Test
    fun canonicalShrinkRequestsRainbowGlitterVideoAtEightTimesSpeed() = runTest {
        var requestedEffect: PatsyCompanionEffect? = null

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onEffectRequested = { effect -> requestedEffect = effect },
            frameWait = {},
        )

        controller.guideTo(PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f))

        val effect = assertNotNull(requestedEffect)
        assertEquals("video4635308202773325454.mp4", effect.assetName)
        assertEquals(8f, effect.playbackSpeed)
        assertEquals(PatsyCompanionEffectKind.RISING_RAINBOW_GLITTER, effect.kind)
    }

    @Test
    fun canonicalRiveContractUsesPatsySevenTwentyArtboardAndBodyStates() {
        assertEquals("Patsy", PatsyRigContractV1.ARTBOARD)
        assertEquals(720, PatsyRigContractV1.ARTBOARD_WIDTH)
        assertEquals(720, PatsyRigContractV1.ARTBOARD_HEIGHT)
        assertEquals(true, PatsyRigContractV1.ARTBOARD_TRANSPARENT)
        assertEquals("run", enumValues<PatsyRigMotion>().firstOrNull { it.name == "RUN" }?.riveValue)
        assertEquals("stand", enumValues<PatsyRigMotion>().firstOrNull { it.name == "STAND" }?.riveValue)
    }

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        override fun apply(mutations: List<PatsyRigMutation>) = Unit
    }
}
