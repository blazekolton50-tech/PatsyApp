package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import com.patsy.app.patsy.rig.PatsyRigViseme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PatsyCompanionSemanticIntentTest {
    @Test
    fun blinkEyeTrackingAndHeadTiltDriveTheExistingRig() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.TrackEyes(horizontal = 2f, vertical = -2f))
        assertEquals(PatsyCompanionMode.TRACKING, controller.state.mode)
        assertEquals(1f, controller.state.pose.lookX)
        assertEquals(-1f, controller.state.pose.lookY)

        controller.dispatch(PatsyCompanionIntent.TiltHead(amount = 2f))
        assertEquals(PatsyCompanionMode.ATTENTIVE, controller.state.mode)
        assertEquals(1f, controller.state.pose.headTilt)

        controller.dispatch(PatsyCompanionIntent.Blink)
        assertEquals(1, runtime.blinkSequenceCount())
    }

    @Test
    fun thinkListenAndSpeakRemainIndependentSemanticStates() {
        val (controller, _) = controller()

        controller.dispatch(PatsyCompanionIntent.Think)
        assertEquals(PatsyCompanionMode.THINKING, controller.state.mode)
        assertEquals(PatsyRigExpression.CURIOUS, controller.state.pose.expression)
        assertTrue(controller.state.pose.headTilt != 0f)
        assertFalse(controller.state.pose.talking)

        controller.dispatch(PatsyCompanionIntent.Listen)
        assertEquals(PatsyCompanionMode.LISTENING, controller.state.mode)
        assertTrue(controller.state.pose.leftEarDrive > 0f)
        assertTrue(controller.state.pose.rightEarDrive > 0f)
        assertFalse(controller.state.pose.talking)

        controller.dispatch(
            PatsyCompanionIntent.Speak(
                viseme = PatsyRigViseme.O,
                visemeIntensity = 2f,
                speechEnergy = 2f,
            )
        )
        assertEquals(PatsyCompanionMode.SPEAKING, controller.state.mode)
        assertTrue(controller.state.pose.talking)
        assertEquals(PatsyRigViseme.O, controller.state.pose.viseme)
        assertEquals(1f, controller.state.pose.visemeIntensity)
        assertEquals(1f, controller.state.pose.speechEnergy)
    }

    @Test
    fun reactionsCelebrateJumpAndSleepUseOnlySupportedV1States() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.HAPPY))
        assertEquals(PatsyCompanionMode.REACTING, controller.state.mode)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertTrue(controller.state.pose.tailEnergy >= 0.8f)

        controller.dispatch(PatsyCompanionIntent.Celebrate)
        assertEquals(PatsyCompanionMode.CELEBRATING, controller.state.mode)
        assertEquals(PatsyRigMotion.WAVE, controller.state.pose.motion)
        assertEquals(1, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Jump)
        assertEquals(PatsyCompanionMode.JUMPING, controller.state.mode)
        assertEquals(PatsyRigMotion.JUMP, controller.state.pose.motion)
        assertEquals(2, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Sleep)
        assertEquals(PatsyCompanionMode.RESTING, controller.state.mode)
        assertEquals(PatsyRigMotion.LIE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.SLEEPY, controller.state.pose.expression)
        assertFalse(controller.state.pose.talking)
    }

    @Test
    fun reducedMotionSuppressesNewOneShotsButKeepsSleepMeaningful() {
        val (controller, runtime) = controller()

        controller.setReducedMotion(true)
        controller.dispatch(PatsyCompanionIntent.Celebrate)
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertEquals(0, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Jump)
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(0, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Sleep)
        assertEquals(PatsyRigMotion.LIE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.SLEEPY, controller.state.pose.expression)
    }

    private fun controller(): Pair<PatsyCompanionController, RecordingRuntime> {
        val runtime = RecordingRuntime()
        return PatsyCompanionController(PatsyRigCoordinator(runtime)) to runtime
    }

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        private val batches = mutableListOf<List<PatsyRigMutation>>()

        override fun apply(mutations: List<PatsyRigMutation>) {
            batches += mutations
        }

        fun actionSequenceCount(): Int = batches
            .flatMap { it }
            .count { it.propertyPath == PatsyRigContractV1.Property.MOTION_ACTION_SEQUENCE }

        fun blinkSequenceCount(): Int = batches
            .flatMap { it }
            .count { it.propertyPath == PatsyRigContractV1.Property.FACE_BLINK_SEQUENCE }
    }
}
