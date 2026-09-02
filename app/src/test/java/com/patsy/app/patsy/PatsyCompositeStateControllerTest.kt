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
    fun clearingOneShotRearmsTheSameActionWithoutDuplicateRetriggers() {
        val runtime = RecordingRuntime()
        val controller = PatsyCompositeStateController(
            rig = PatsyRigCoordinator(runtime),
            speechRuntime = RecordingSpeechRuntime(),
        )
        val wave = PatsyCompositeCommand(bodyState = "sitting", actionName = "wave")

        controller.dispatch(wave)
        controller.dispatch(wave)
        assertEquals(1, runtime.actionSequenceCount())

        controller.dispatch(PatsyCompositeCommand(bodyState = "sitting", actionName = "none"))
        assertEquals(null, controller.state.activeAction)

        controller.dispatch(wave)
        assertEquals(2, runtime.actionSequenceCount())
        assertEquals(PatsyRigMotion.WAVE, controller.state.activeAction)
    }

    @Test
    fun emptySpeechMeansNoNewSpeechAndDoesNotStopActiveSpeech() {
        val runtime = RecordingRuntime()
        val speech = RecordingSpeechRuntime()
        val controller = PatsyCompositeStateController(
            rig = PatsyRigCoordinator(runtime),
            speechRuntime = speech,
        )

        controller.dispatch(
            PatsyCompositeCommand(
                bodyState = "sitting",
                expressionPreset = "curious",
                speechText = "Still talking",
            )
        )
        controller.dispatch(
            PatsyCompositeCommand(
                bodyState = "walking",
                expressionPreset = "curious",
                speechText = "",
            )
        )

        assertEquals(listOf("Still talking"), speech.startedTexts)
        assertEquals(0, speech.stopCount)
        assertEquals("Still talking", controller.state.speechText)
        assertTrue(controller.state.pose.talking)
        assertEquals(PatsyRigMotion.WALK, controller.state.pose.motion)
        assertEquals(
            PatsyRigValue.Boolean(true),
            runtime.latestValues()[PatsyRigContractV1.Property.SPEECH_TALKING],
        )
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
                attentionType = "worldTarget",
                attentionTargetName = "timeline-scrubber",
                attentionTarget = PatsyCompanionTarget(0.9f, 0.2f),
                speechText = "Testing speech",
            )
        )
        assertTrue(controller.state.pose.lookX > 0f)
        assertTrue(controller.state.pose.lookY < 0f)

        controller.resetToNeutral()

        assertEquals(PatsyRigMotion.IDLE, controller.state.pose.motion)
        assertEquals(PatsyRigExpression.NEUTRAL, controller.state.pose.expression)
        assertEquals(0f, controller.state.pose.expressionIntensity)
        assertEquals(0f, controller.state.pose.lookX)
        assertEquals(0f, controller.state.pose.lookY)
        assertEquals(0f, controller.state.pose.headTilt)
        assertEquals(0.5f, controller.state.pose.pointX)
        assertEquals(0.5f, controller.state.pose.pointY)
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
            PatsyRigValue.Number(0f),
            latest[PatsyRigContractV1.Property.HEAD_LOOK_X],
        )
        assertEquals(
            PatsyRigValue.Number(0f),
            latest[PatsyRigContractV1.Property.HEAD_LOOK_Y],
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
