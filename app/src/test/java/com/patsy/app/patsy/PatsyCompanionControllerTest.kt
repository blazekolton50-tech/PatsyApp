package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import com.patsy.app.patsy.rig.PatsyRigValue
import com.patsy.app.patsy.rig.PatsyRigViseme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PatsyCompanionControllerTest {
    private class RecordingRuntime : PatsyRigRuntimePort {
        override var status: PatsyRigStatus = PatsyRigStatus.Ready
        val batches = mutableListOf<List<PatsyRigMutation>>()

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

    private fun controller(): Pair<PatsyCompanionController, RecordingRuntime> {
        val runtime = RecordingRuntime()
        return PatsyCompanionController(PatsyRigCoordinator(runtime)) to runtime
    }

    @Test
    fun idleBlinkEyeTrackingAndHeadTiltAreSemanticTransitions() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.TrackEyes(horizontal = 2f, vertical = -2f))
        assertEquals(PatsyCompanionMode.TRACKING, controller.state.mode)
        assertEquals(1f, controller.state.pose.lookX)
        assertEquals(-1f, controller.state.pose.lookY)

        controller.dispatch(PatsyCompanionIntent.TiltHead(2f))
        assertEquals(PatsyCompanionMode.ATTENTIVE, controller.state.mode)
        assertEquals(1f, controller.state.pose.headTilt)

        controller.dispatch(PatsyCompanionIntent.Blink)
        assertEquals(1, runtime.blinkSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Idle)
        assertEquals(PatsyCompanionMode.IDLE, controller.state.mode)
        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertFalse(controller.state.pose.talking)
        assertEquals(PatsyRigViseme.REST, controller.state.pose.viseme)
    }

    @Test
    fun lookPointNoticeAndGuideUseClampedUiTargets() {
        val (controller, runtime) = controller()
        val target = PatsyCompanionTarget(normalizedX = 1.4f, normalizedY = -0.2f)

        controller.dispatch(PatsyCompanionIntent.LookAt(target))
        assertEquals(PatsyCompanionMode.TRACKING, controller.state.mode)
        assertEquals(1f, controller.state.pose.lookX)
        assertEquals(-1f, controller.state.pose.lookY)
        assertEquals(0, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Notice(target))
        assertEquals(PatsyCompanionMode.ATTENTIVE, controller.state.mode)
        assertEquals(PatsyRigExpression.CURIOUS, controller.state.pose.expression)

        controller.dispatch(PatsyCompanionIntent.PointAt(target))
        assertEquals(PatsyCompanionMode.GUIDING, controller.state.mode)
        assertEquals(1f, controller.state.pose.pointX)
        assertEquals(0f, controller.state.pose.pointY)
        assertEquals(1, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Guide(target))
        assertEquals(PatsyCompanionMode.GUIDING, controller.state.mode)
        assertEquals(PatsyRigExpression.PROUD, controller.state.pose.expression)
        assertEquals(2, runtime.actionSequenceCount())
    }

    @Test
    fun thinkListenAndSpeakMoveThroughNaturalInteractionStates() {
        val (controller, _) = controller()

        controller.dispatch(PatsyCompanionIntent.Think)
        assertEquals(PatsyCompanionMode.THINKING, controller.state.mode)
        assertEquals(PatsyRigExpression.CURIOUS, controller.state.pose.expression)
        assertTrue(controller.state.pose.headTilt != 0f)
        assertFalse(controller.state.pose.talking)

        controller.dispatch(PatsyCompanionIntent.Listen)
        assertEquals(PatsyCompanionMode.LISTENING, controller.state.mode)
        assertEquals(PatsyRigExpression.CURIOUS, controller.state.pose.expression)
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
    fun reactionsAndCelebrateChangeFaceAndEnergyWithoutStaticPoseSwaps() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.CHEEKY))
        assertEquals(PatsyCompanionMode.REACTING, controller.state.mode)
        assertEquals(PatsyRigExpression.CHEEKY, controller.state.pose.expression)

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.CONCERNED))
        assertEquals(PatsyRigExpression.CONCERNED, controller.state.pose.expression)

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.HAPPY))
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertTrue(controller.state.pose.tailEnergy >= 0.8f)

        controller.dispatch(PatsyCompanionIntent.Celebrate)
        assertEquals(PatsyCompanionMode.CELEBRATING, controller.state.mode)
        assertEquals(PatsyRigMotion.WAVE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertEquals(1, runtime.actionSequenceCount())
    }

    @Test
    fun shrinkExpandRepositionAndReturnRestingPositionAreReusablePresentationStates() {
        val (controller, _) = controller()

        controller.dispatch(PatsyCompanionIntent.ShrinkHelper)
        assertEquals(PatsyCompanionMode.HELPER, controller.state.mode)
        assertEquals(0.68f, controller.state.pose.stageScale)

        controller.dispatch(PatsyCompanionIntent.Reposition(normalizedX = 1.5f, normalizedY = -1f))
        assertEquals(PatsyCompanionMode.REPOSITIONED, controller.state.mode)
        assertEquals(1f, controller.state.pose.stageX)
        assertEquals(0f, controller.state.pose.stageY)
        assertEquals(0.68f, controller.state.pose.stageScale)

        controller.dispatch(PatsyCompanionIntent.ExpandAssistant)
        assertEquals(PatsyCompanionMode.ENGAGED, controller.state.mode)
        assertEquals(1f, controller.state.pose.stageScale)

        controller.dispatch(PatsyCompanionIntent.ReturnRest)
        assertEquals(PatsyCompanionMode.IDLE, controller.state.mode)
        assertEquals(0.5f, controller.state.pose.stageX)
        assertEquals(0.75f, controller.state.pose.stageY)
        assertEquals(1f, controller.state.pose.stageScale)
    }

    @Test
    fun reducedMotionKeepsMeaningfulGazeButSuppressesOneShotPointAndCelebrateMotion() {
        val (controller, runtime) = controller()

        controller.dispatch(PatsyCompanionIntent.SetReducedMotion(true))
        assertTrue(controller.state.pose.reducedMotion)

        controller.dispatch(PatsyCompanionIntent.PointAt(PatsyCompanionTarget(0.9f, 0.2f)))
        assertEquals(PatsyCompanionMode.GUIDING, controller.state.mode)
        assertEquals(0.9f, controller.state.pose.pointX)
        assertEquals(0.2f, controller.state.pose.pointY)
        assertTrue(controller.state.pose.lookX > 0f)
        assertEquals(0, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.Celebrate)
        assertEquals(PatsyCompanionMode.CELEBRATING, controller.state.mode)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertEquals(0, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompanionIntent.SetReducedMotion(false))
        controller.dispatch(PatsyCompanionIntent.Celebrate)
        assertEquals(1, runtime.actionSequenceCount())
    }

    @Test
    fun allReactionMappingsRemainWithinTheExistingRigExpressionContract() {
        val (controller, _) = controller()
        val expected = mapOf(
            PatsyCompanionReaction.CHEEKY to PatsyRigExpression.CHEEKY,
            PatsyCompanionReaction.CURIOUS to PatsyRigExpression.CURIOUS,
            PatsyCompanionReaction.CONCERNED to PatsyRigExpression.CONCERNED,
            PatsyCompanionReaction.PROUD to PatsyRigExpression.PROUD,
            PatsyCompanionReaction.HAPPY to PatsyRigExpression.EXCITED,
        )

        expected.forEach { (reaction, expression) ->
            controller.dispatch(PatsyCompanionIntent.React(reaction))
            assertEquals(expression, controller.state.pose.expression)
        }
    }
}
