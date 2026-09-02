package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme

/** Speech side-effect boundary kept separate from the animation/Rive runtime. */
interface PatsySpeechRuntimePort {
    fun start(text: String, audioClipId: String)
    fun stop()
}

data class PatsyCompositeState(
    val pose: PatsyRigPose = neutralPose(),
    val activeAction: PatsyRigMotion? = null,
    val speechText: String = "",
    val audioClipId: String = "",
)

/**
 * Applies composite Patsy commands without repeatedly firing one-shot Rive actions or restarting
 * identical speech. The controller owns transient state while PatsyRigCoordinator remains the ABI
 * writer.
 */
class PatsyCompositeStateController(
    private val rig: PatsyRigCoordinator,
    private val speechRuntime: PatsySpeechRuntimePort,
) {
    var state: PatsyCompositeState = PatsyCompositeState()
        private set

    init {
        rig.render(state.pose)
    }

    fun dispatch(command: PatsyCompositeCommand) {
        val plan = PatsyCompositeCommandMapper.map(command)
        val previous = state

        rig.render(plan.pose)

        if (plan.oneShotAction != null && plan.oneShotAction != previous.activeAction) {
            rig.retriggerAction(plan.oneShotAction)
        }

        val nextSpeech = plan.speechText.trim()
        val nextAudioClipId = plan.audioClipId.trim()
        val speechChanged = nextSpeech != previous.speechText || nextAudioClipId != previous.audioClipId
        when {
            nextSpeech.isNotEmpty() && speechChanged -> speechRuntime.start(nextSpeech, nextAudioClipId)
            nextSpeech.isEmpty() && previous.speechText.isNotEmpty() -> speechRuntime.stop()
        }

        state = PatsyCompositeState(
            pose = plan.pose,
            activeAction = plan.oneShotAction,
            speechText = nextSpeech,
            audioClipId = nextAudioClipId,
        )
    }

    fun resetToNeutral() {
        if (state.speechText.isNotEmpty() || state.pose.talking) {
            speechRuntime.stop()
        }
        val neutral = neutralPose()
        rig.render(neutral)
        state = PatsyCompositeState(pose = neutral)
    }
}

private fun neutralPose(): PatsyRigPose = PatsyRigPose(
    motion = PatsyRigMotion.IDLE,
    motionSpeed = 0.12f,
    expression = PatsyRigExpression.NEUTRAL,
    expressionIntensity = 0f,
    talking = false,
    viseme = PatsyRigViseme.REST,
    visemeIntensity = 0f,
    speechEnergy = 0f,
)
