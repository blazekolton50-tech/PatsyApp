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

class PatsyCompositeStateControllerTest {
    @Test
    fun duplicateCompositeDoesNotRetriggerOneShotActionOrRestartSpeech() {
        val runtime = RecordingRuntime()
        val speech = RecordingSpeechRuntime()
        val controller = PatsyCompositeStateController(
            rig = PatsyRigCoordinator(runtime),
            speechRuntime = speech,
        )
        val greeting = PatsyCompositeCommand(
            bodyState = "sitting",
            actionName = "wave",
            actionDurationSeconds = 1.8f,
            expressionPreset = "happy",
            expressionIntensity = 0.75f,
            speechText = "Heeeyy, you're back!",
        )

        controller.dispatch(greeting)
        controller.dispatch(greeting)

        assertEquals(1, runtime.actionSequenceCount())
        assertEquals(listOf("Heeeyy, you're back!"), speech.startedTexts)
        assertEquals(PatsyRigMotion.SIT, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.EXCITED, controller.state.pose.expression)
        assertTrue(controller.state.pose.talking)
        assertEquals(PatsyRigMotion.WAVE, controller.state.activeAction)
    }

    @Test
    fun fullNeutralResetStopsTransientLayersAndRestoresNeutralIdlePose() {
        val runtime = RecordingRuntime()
        val speech = RecordingSpeechRuntime()
        val controller = PatsyCompositeStateController(
            rig = PatsyRigCoordinator(runtime),
            speechRuntime = speech,
        )

        controller.dispatch(
            PatsyCompositeCommand(
                bodyState = "sitting",
                actionName = "wave",
                expressionPreset = "happy",
                expressionIntensity = 0.8f,
                speechText = "Testing speech",
            )
        )
        controller.resetToNeutral()

        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.NEUTRAL, controller.state.pose.expression)
        assertEquals(0f, controller.state.pose.expressionIntensity)
        assertFalse(controller.state.pose.talking)
        assertEquals(PatsyRigViseme.REST, controller.state.pose.viseme)
        assertEquals(null, controller.state.activeAction)
        assertEquals("", controller.state.speechText)
        assertEquals(1, speech.stopCount)

        val latest = runtime.latestValues()
        assertEquals(
            PatsyRigValue.Enum("idle"),
            latest[PatsyRigContractV1.Property.MOTION_MODE],
        )
        assertEquals(
            PatsyRigValue.Enum("neutral"),
            latest[PatsyRigContractV1.Property.FACE_EXPRESSION],
        )
        assertEquals(
            PatsyRigValue.Boolean(false),
            latest[PatsyRigContractV1.Property.SPEECH_TALKING],
        )
    }

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        val batches = mutableListOf<List<PatsyRigMutation>>()

        override fun apply(mutations: List<PatsyRigMutation>) {
            batches += mutations
        }

        fun actionSequenceCount(): Int = batches
            .flatMap { it }
            .count { it.propertyPath == PatsyRigContractV1.Property.MOTION_ACTION_SEQUENCE }

        fun latestValues(): Map<String, PatsyRigValue> = batches
            .flatMap { it }
            .associate { it.propertyPath to it.value }
    }

    private class RecordingSpeechRuntime : PatsySpeechRuntimePort {
        val startedTexts = mutableListOf<String>()
        var stopCount = 0

        override fun start(text: String, audioClipId: String) {
            startedTexts += text
        }

        override fun stop() {
            stopCount += 1
        }
    }
}
