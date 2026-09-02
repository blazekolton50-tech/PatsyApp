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
        val requestedSpeech = plan.speechText.trim()
        val requestedAudioClipId = plan.audioClipId.trim()
        val carriesActiveSpeech = requestedSpeech.isEmpty() && previous.speechText.isNotEmpty()
        val nextPose = if (carriesActiveSpeech) {
            plan.pose.copy(
                talking = true,
                viseme = previous.pose.viseme,
                visemeIntensity = previous.pose.visemeIntensity,
                speechEnergy = previous.pose.speechEnergy,
            )
        } else {
            plan.pose
        }

        rig.render(nextPose)

        if (plan.oneShotAction != null && plan.oneShotAction != previous.activeAction) {
            rig.retriggerAction(plan.oneShotAction)
        }

        if (requestedSpeech.isNotEmpty()) {
            val speechChanged = requestedSpeech != previous.speechText ||
                requestedAudioClipId != previous.audioClipId
            if (speechChanged) {
                speechRuntime.start(requestedSpeech, requestedAudioClipId)
            }
        }

        state = PatsyCompositeState(
            pose = nextPose,
            activeAction = plan.oneShotAction,
            speechText = if (requestedSpeech.isNotEmpty()) requestedSpeech else previous.speechText,
            audioClipId = if (requestedSpeech.isNotEmpty()) requestedAudioClipId else previous.audioClipId,
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
