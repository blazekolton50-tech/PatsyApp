package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import com.patsy.app.patsy.rig.PatsyRigViseme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PatsyAiCompanionBridgeTest {
    @Test
    fun typedAiPerformanceMapsToSemanticCompanionIntents() {
        assertEquals(
            PatsyCompanionIntent.Think,
            PatsyAiCompanionBridge.toIntent(PatsyAiPerformance.Thinking),
        )
        assertEquals(
            PatsyCompanionIntent.Listen,
            PatsyAiCompanionBridge.toIntent(PatsyAiPerformance.Listening),
        )
        assertEquals(
            PatsyCompanionIntent.React(PatsyCompanionReaction.JUDGY),
            PatsyAiCompanionBridge.toIntent(PatsyAiPerformance.Judgy),
        )
        assertEquals(
            PatsyCompanionIntent.React(PatsyCompanionReaction.CONCERNED),
            PatsyAiCompanionBridge.toIntent(PatsyAiPerformance.Concerned),
        )
        assertEquals(
            PatsyCompanionIntent.Settle,
            PatsyAiCompanionBridge.toIntent(PatsyAiPerformance.Idle),
        )
    }

    @Test
    fun realSpeechAmplitudeIsClampedAndKeptIndependentFromTimelineActions() {
        val performance = PatsyAiCompanionBridge.speakingFromAmplitude(
            level = 2f,
            viseme = PatsyRigViseme.O,
        )

        assertEquals(PatsyRigViseme.O, performance.viseme)
        assertEquals(1f, performance.visemeIntensity)
        assertEquals(1f, performance.speechEnergy)

        val intent = PatsyAiCompanionBridge.toIntent(performance) as PatsyCompanionIntent.Speak
        assertEquals(PatsyRigViseme.O, intent.viseme)
        assertEquals(1f, intent.visemeIntensity)
        assertEquals(1f, intent.speechEnergy)
    }

    @Test
    fun uploadedJudgyAndConcernedBehavioursUseExistingSafeV1RigContract() {
        val (controller, _) = controller()

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.JUDGY))
        assertEquals(PatsyCompanionMode.REACTING, controller.state.mode)
        assertEquals(PatsyRigExpression.CHEEKY, controller.state.pose.expression)
        assertTrue(controller.state.pose.expressionIntensity >= 0.9f)
        assertTrue(controller.state.pose.headTilt != 0f)
        assertTrue(controller.state.pose.leftEarDrive != controller.state.pose.rightEarDrive)
        assertTrue(controller.state.pose.tailEnergy < 0.3f)
        assertFalse(controller.state.pose.talking)

        controller.dispatch(PatsyCompanionIntent.React(PatsyCompanionReaction.CONCERNED))
        assertEquals(PatsyRigExpression.CONCERNED, controller.state.pose.expression)
        assertTrue(controller.state.pose.tailEnergy < 0.3f)
        assertFalse(controller.state.pose.talking)
    }

    @Test
    fun settlingAfterSpeechClosesMouthWithoutMovingPatsyHome() {
        val (controller, runtime) = controller()
        val startX = controller.state.pose.stageX
        val startY = controller.state.pose.stageY
        val startScale = controller.state.pose.stageScale

        controller.dispatch(
            PatsyCompanionIntent.Speak(
                viseme = PatsyRigViseme.A,
                visemeIntensity = 0.8f,
                speechEnergy = 0.7f,
            )
        )
        assertTrue(controller.state.pose.talking)

        controller.dispatch(PatsyCompanionIntent.Settle)
        assertEquals(PatsyCompanionMode.IDLE, controller.state.mode)
        assertFalse(controller.state.pose.talking)
        assertEquals(PatsyRigViseme.REST, controller.state.pose.viseme)
        assertEquals(0f, controller.state.pose.visemeIntensity)
        assertEquals(0f, controller.state.pose.speechEnergy)
        assertEquals(startX, controller.state.pose.stageX)
        assertEquals(startY, controller.state.pose.stageY)
        assertEquals(startScale, controller.state.pose.stageScale)
        assertTrue(runtime.wrote(PatsyRigContractV1.Property.SPEECH_TALKING))
        assertTrue(runtime.wrote(PatsyRigContractV1.Property.SPEECH_ENERGY))
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

        fun wrote(path: String): Boolean = batches
            .flatMap { it }
            .any { it.propertyPath == path }
    }
}
