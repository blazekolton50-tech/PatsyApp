package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        override fun apply(mutations: List<PatsyRigMutation>) = Unit
    }
}
