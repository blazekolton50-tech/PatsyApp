package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyCompanionExtendedIntentTest {
    private class RecordingRuntime : PatsyRigRuntimePort {
        override var status: PatsyRigStatus = PatsyRigStatus.Ready
        val batches = mutableListOf<List<PatsyRigMutation>>()

        override fun apply(mutations: List<PatsyRigMutation>) {
            batches += mutations
        }

        fun actionSequenceCount(): Int = batches
            .flatMap { it }
            .count { it.propertyPath == PatsyRigContractV1.Property.MOTION_ACTION_SEQUENCE }
    }

    private fun controller(): Pair<PatsyCompanionController, RecordingRuntime> {
        val runtime = RecordingRuntime()
        return PatsyCompanionController(PatsyRigCoordinator(runtime)) to runtime
    }

    @Test
    fun jumpIsAOneShotSemanticIntentAndReducedMotionSuppressesRetrigger() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.Jump)
        assertEquals(PatsyCompanionMode.JUMPING, controller.state.mode)
        assertEquals(PatsyRigMotion.JUMP, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertEquals(1, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.SetReducedMotion(true))
        controller.dispatch(PatsyCompanionIntent.Jump)
        assertEquals(PatsyCompanionMode.JUMPING, controller.state.mode)
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(1, runtime.actionSequenceCount())
    }

    @Test
    fun sleepUsesTheDurableLieMotionAndRemainsMeaningfulWithReducedMotion() {
        val (controller, _) = controller()

        controller.dispatch(PatsyCompanionIntent.Sleep)
        assertEquals(PatsyCompanionMode.RESTING, controller.state.mode)
        assertEquals(PatsyRigMotion.LIE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.SLEEPY, controller.state.pose.expression)
        assertTrue(controller.state.pose.tailEnergy <= 0.2f)

        controller.dispatch(PatsyCompanionIntent.SetReducedMotion(true))
        controller.dispatch(PatsyCompanionIntent.Sleep)
        assertEquals(PatsyRigMotion.LIE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.SLEEPY, controller.state.pose.expression)
    }
}
