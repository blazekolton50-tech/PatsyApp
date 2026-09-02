package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose

/** External semantic request before it is translated into the locked V1 Rive ABI. */
data class PatsyCompositeCommand(
    val bodyState: String = "idle",
    val bodyTransitionSpeed: Float = 1f,
    val actionName: String = "none",
    val actionDurationSeconds: Float = 0f,
    val expressionPreset: String = "neutral",
    val expressionIntensity: Float = 0f,
    val attentionType: String = "neutral",
    val attentionTargetName: String = "",
    val attentionTarget: PatsyCompanionTarget? = null,
    val speechText: String = "",
    val audioClipId: String = "",
)

/**
 * Truthful V1-compatible plan. Unsupported semantic requests are reported instead of being
 * silently represented as animation states that do not exist in PatsyRigContractV1.
 */
data class PatsyCompositePlan(
    val pose: PatsyRigPose,
    val oneShotAction: PatsyRigMotion? = null,
    val speechText: String = "",
    val audioClipId: String = "",
    val unsupportedSemantics: Set<String> = emptySet(),
)

object PatsyCompositeCommandMapper {
    fun map(command: PatsyCompositeCommand): PatsyCompositePlan {
        val bodyName = command.bodyState.normalized()
        val actionName = command.actionName.normalized()
        val expressionName = command.expressionPreset.normalized()
        val attentionName = command.attentionType.normalized()
        val attentionTarget = command.attentionTarget?.normalised()

        val bodyMotion = when (bodyName) {
            "walking", "walk" -> PatsyRigMotion.WALK
            "sitting", "sit" -> PatsyRigMotion.SIT
            "lying", "lie" -> PatsyRigMotion.LIE
            else -> PatsyRigMotion.IDLE
        }

        val oneShotAction = when (actionName) {
            "wave" -> PatsyRigMotion.WAVE
            "point" -> PatsyRigMotion.POINT
            "jump" -> PatsyRigMotion.JUMP
            else -> null
        }

        val expression = when (expressionName) {
            "happy" -> PatsyRigExpression.EXCITED
            "curious" -> PatsyRigExpression.CURIOUS
            "concerned" -> PatsyRigExpression.CONCERNED
            "neutral" -> PatsyRigExpression.NEUTRAL
            else -> PatsyRigExpression.NEUTRAL
        }

        val knownAttentionTypes = setOf(
            "neutral",
            "user",
            "camera",
            "uicontrol",
            "worldtarget",
            "aiexplicit",
        )
        val targetDependentAttentionTypes = setOf("uicontrol", "worldtarget", "aiexplicit")

        val unsupportedSemantics = buildSet {
            if (actionName == "peek" || actionName == "covereyes") {
                add("action:$actionName")
            }
            if (expressionName == "focused" || expressionName == "shy") {
                add("expression:$expressionName")
            }
            if (attentionName !in knownAttentionTypes) {
                add("attention:$attentionName")
            } else if (attentionName in targetDependentAttentionTypes && attentionTarget == null) {
                add("attention:$attentionName:missing_target")
            }
        }

        val hasUsableAttentionTarget = attentionName in knownAttentionTypes &&
            attentionName != "neutral" && attentionTarget != null
        val lookX = if (hasUsableAttentionTarget) {
            ((attentionTarget!!.normalizedX - 0.5f) * 2f).coerceIn(-1f, 1f)
        } else {
            0f
        }
        val lookY = if (hasUsableAttentionTarget) {
            ((attentionTarget!!.normalizedY - 0.5f) * 2f).coerceIn(-1f, 1f)
        } else {
            0f
        }
        val headTilt = if (hasUsableAttentionTarget) {
            (-lookX * 0.18f).coerceIn(-0.35f, 0.35f)
        } else {
            0f
        }

        val speechText = command.speechText.trim()
        val pose = PatsyRigPose(
            motion = bodyMotion,
            motionSpeed = command.bodyTransitionSpeed.coerceIn(0f, 1f),
            lookX = lookX,
            lookY = lookY,
            headTilt = headTilt,
            expression = expression,
            expressionIntensity = command.expressionIntensity.coerceIn(0f, 1f),
            talking = speechText.isNotEmpty(),
        )

        return PatsyCompositePlan(
            pose = pose,
            oneShotAction = oneShotAction,
            speechText = speechText,
            audioClipId = command.audioClipId.trim(),
            unsupportedSemantics = unsupportedSemantics,
        )
    }

    private fun String.normalized(): String = trim().lowercase()
}
