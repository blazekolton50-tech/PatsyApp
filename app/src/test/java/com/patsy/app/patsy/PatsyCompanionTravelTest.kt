package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyCompanionTravelTest {

    @Test
    fun guideToUsesLockedOneThumbShrinkThenWalksAndPoints() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = observed::add,
            frameWait = {},
        )
        val target = PatsyCompanionTarget(normalizedX = 0.90f, normalizedY = 0.28f)

        controller.guideTo(target)

        assertEquals(50, observed.count { it.mode == PatsyCompanionMode.SHRINKING })
        assertTrue(observed.filter { it.mode == PatsyCompanionMode.SHRINKING }.all { it.pose.stageScale >= 0.50f })
        assertTrue(observed.any { it.pose.motion == PatsyRigMotion.WALK && it.pose.stageX > 0.50f })
        assertTrue(observed.count { it.pose.motion == PatsyRigMotion.WALK } >= 3)
        assertEquals(PatsyRigMotion.POINT, controller.state.pose.motion)
        assertEquals(0.50f, controller.state.pose.stageScale)
        assertEquals(target.normalizedX, controller.state.pose.pointX)
        assertEquals(target.normalizedY, controller.state.pose.pointY)
        assertTrue(controller.state.pose.stageX < target.normalizedX)
    }

    @Test
    fun shrinkForMissionShrinksInPlaceToOneThumbThenIdles() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        val waits = mutableListOf<Long>()
        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = observed::add,
            frameWait = waits::add,
        )

        controller.shrinkForMission()

        assertTrue(observed.isNotEmpty())
        assertTrue(observed.any { it.mode == PatsyCompanionMode.SHRINKING })
        assertTrue(observed.none { it.pose.motion == PatsyRigMotion.WALK || it.pose.motion == PatsyRigMotion.POINT })
        assertTrue(observed.all { it.pose.stageX == 0.50f && it.pose.stageY == 0.75f })
        assertEquals(PatsyCompanionMode.IDLE, controller.state.mode)
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(0.50f, controller.state.pose.stageScale)
        assertEquals(50, waits.size)
        assertEquals(800L, waits.sum())
    }

    @Test
    fun returnHomeWalksBackThenExpandsToNormalIdlePosition() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = observed::add,
            frameWait = {},
        )

        controller.guideTo(PatsyCompanionTarget(normalizedX = 0.12f, normalizedY = 0.35f))
        observed.clear()
        controller.returnHome()

        assertTrue(observed.any { it.pose.motion == PatsyRigMotion.WALK && it.pose.stageScale <= 0.50f })
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(0.50f, controller.state.pose.stageX)
        assertEquals(0.75f, controller.state.pose.stageY)
        assertEquals(1.00f, controller.state.pose.stageScale)
        assertEquals(false, controller.state.pose.talking)
    }

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        val batches = mutableListOf<List<PatsyRigMutation>>()

        override fun apply(mutations: List<PatsyRigMutation>) {
            batches += mutations
        }
    }
}
