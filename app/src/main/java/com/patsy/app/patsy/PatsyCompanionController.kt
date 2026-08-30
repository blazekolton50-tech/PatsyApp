package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme

/**
 * Semantic target supplied by the UI/intelligence layer.
 *
 * Coordinates are normalised to the containing UI surface. The companion layer deliberately
 * knows nothing about pixels, Compose nodes, Rive frames or authored bones.
 */
data class PatsyCompanionTarget(
    val normalizedX: Float,
    val normalizedY: Float,
) {
    fun normalised(): PatsyCompanionTarget = copy(
        normalizedX = normalizedX.coerceIn(0f, 1f),
        normalizedY = normalizedY.coerceIn(0f, 1f),
    )
}

/** High-level interaction state exposed to app code without exposing the final rig. */
enum class PatsyCompanionMode {
    IDLE,
    TRACKING,
    ATTENTIVE,
    GUIDING,
    THINKING,
    LISTENING,
    SPEAKING,
    REACTING,
    CELEBRATING,
    JUMPING,
    RESTING,
    HELPER,
    ENGAGED,
    REPOSITIONED,
}

/** Reactions intentionally map only to expressions already present in the locked V1 rig ABI. */
enum class PatsyCompanionReaction {
    CHEEKY,
    CURIOUS,
    CONCERNED,
    PROUD,
    HAPPY,
}

/**
 * Commands consumed by [PatsyCompanionController].
 *
 * These are semantic intents, not animation frames. The eventual production Rive asset remains
 * free to author continuous breathing, idle secondary motion and transitions behind the existing
 * rig contract without changing calling code here.
 */
sealed interface PatsyCompanionIntent {
    data object Idle : PatsyCompanionIntent
    data object Blink : PatsyCompanionIntent
    data class TrackEyes(val horizontal: Float, val vertical: Float) : PatsyCompanionIntent
    data class TiltHead(val amount: Float) : PatsyCompanionIntent
    data class LookAt(val target: PatsyCompanionTarget) : PatsyCompanionIntent
    data class Notice(val target: PatsyCompanionTarget) : PatsyCompanionIntent
    data class PointAt(val target: PatsyCompanionTarget) : PatsyCompanionIntent
    data class Guide(val target: PatsyCompanionTarget) : PatsyCompanionIntent
    data object Think : PatsyCompanionIntent
    data object Listen : PatsyCompanionIntent
    data class Speak(
        val viseme: PatsyRigViseme = PatsyRigViseme.REST,
        val visemeIntensity: Float = 0.45f,
        val speechEnergy: Float = 0.45f,
    ) : PatsyCompanionIntent
    data class React(val reaction: PatsyCompanionReaction) : PatsyCompanionIntent
    data object Celebrate : PatsyCompanionIntent
    data object Jump : PatsyCompanionIntent
    data object Sleep : PatsyCompanionIntent
    data object ShrinkHelper : PatsyCompanionIntent
    data object ExpandAssistant : PatsyCompanionIntent
    data class Reposition(val normalizedX: Float, val normalizedY: Float) : PatsyCompanionIntent
    data object ReturnRest : PatsyCompanionIntent
    data class SetReducedMotion(val enabled: Boolean) : PatsyCompanionIntent
}

data class PatsyCompanionState(
    val mode: PatsyCompanionMode = PatsyCompanionMode.IDLE,
    val pose: PatsyRigPose = PatsyRigPose(
        motion = PatsyRigMotion.IDLE,
        motionSpeed = 0.12f,
    ),
)

/**
 * Reusable app-owned Patsy state machine layered over [PatsyRigCoordinator].
 *
 * This controller does not load, invent or imply the existence of a production `.riv` file. It
 * only translates semantic app intents into the already-defined rig ABI. One-shot actions are
 * retriggered by the coordinator only when reduced motion is off.
 */
class PatsyCompanionController(
    private val rig: PatsyRigCoordinator,
) {
    var state: PatsyCompanionState = PatsyCompanionState()
        private set

    init {
        rig.render(state.pose)
    }

    fun dispatch(intent: PatsyCompanionIntent) {
        when (intent) {
            PatsyCompanionIntent.Blink -> rig.blink()
            PatsyCompanionIntent.Idle -> render(
                PatsyCompanionMode.IDLE,
                state.pose.idleInteraction(),
            )
            is PatsyCompanionIntent.TrackEyes -> render(
                PatsyCompanionMode.TRACKING,
                state.pose.copy(
                    lookX = intent.horizontal.coerceIn(-1f, 1f),
                    lookY = intent.vertical.coerceIn(-1f, 1f),
                ),
            )
            is PatsyCompanionIntent.TiltHead -> render(
                PatsyCompanionMode.ATTENTIVE,
                state.pose.copy(headTilt = intent.amount.coerceIn(-1f, 1f)),
            )
            is PatsyCompanionIntent.LookAt -> render(
                PatsyCompanionMode.TRACKING,
                state.pose.lookAt(intent.target),
            )
            is PatsyCompanionIntent.Notice -> render(
                PatsyCompanionMode.ATTENTIVE,
                state.pose.lookAt(intent.target).copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = IDLE_MOTION_SPEED,
                    expression = PatsyRigExpression.CURIOUS,
                    expressionIntensity = 0.72f,
                    headTilt = -lookHorizontal(intent.target) * 0.18f,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            is PatsyCompanionIntent.PointAt -> pointOrGuide(intent.target)
            is PatsyCompanionIntent.Guide -> pointOrGuide(intent.target)
            PatsyCompanionIntent.Think -> render(
                PatsyCompanionMode.THINKING,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = IDLE_MOTION_SPEED,
                    lookX = 0.25f,
                    lookY = -0.10f,
                    headTilt = 0.28f,
                    leftEarDrive = 0.18f,
                    rightEarDrive = 0.08f,
                    tailEnergy = 0.25f,
                    expression = PatsyRigExpression.CURIOUS,
                    expressionIntensity = 0.72f,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            PatsyCompanionIntent.Listen -> render(
                PatsyCompanionMode.LISTENING,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = IDLE_MOTION_SPEED,
                    headTilt = 0.12f,
                    leftEarDrive = 0.65f,
                    rightEarDrive = 0.65f,
                    tailEnergy = 0.22f,
                    expression = PatsyRigExpression.CURIOUS,
                    expressionIntensity = 0.78f,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            is PatsyCompanionIntent.Speak -> render(
                PatsyCompanionMode.SPEAKING,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = IDLE_MOTION_SPEED,
                    expression = PatsyRigExpression.CHEEKY,
                    expressionIntensity = 0.76f,
                    talking = true,
                    viseme = intent.viseme,
                    visemeIntensity = intent.visemeIntensity.coerceIn(0f, 1f),
                    speechEnergy = intent.speechEnergy.coerceIn(0f, 1f),
                ),
            )
            is PatsyCompanionIntent.React -> react(intent.reaction)
            PatsyCompanionIntent.Celebrate -> celebrate()
            PatsyCompanionIntent.Jump -> jump()
            PatsyCompanionIntent.Sleep -> sleep()
            PatsyCompanionIntent.ShrinkHelper -> render(
                PatsyCompanionMode.HELPER,
                state.pose.copy(stageScale = HELPER_SCALE),
            )
            PatsyCompanionIntent.ExpandAssistant -> render(
                PatsyCompanionMode.ENGAGED,
                state.pose.copy(stageScale = ENGAGED_SCALE),
            )
            is PatsyCompanionIntent.Reposition -> render(
                PatsyCompanionMode.REPOSITIONED,
                state.pose.copy(
                    stageX = intent.normalizedX.coerceIn(0f, 1f),
                    stageY = intent.normalizedY.coerceIn(0f, 1f),
                ),
            )
            PatsyCompanionIntent.ReturnRest -> {
                val reducedMotion = state.pose.reducedMotion
                render(
                    PatsyCompanionMode.IDLE,
                    PatsyRigPose(
                        motion = PatsyRigMotion.IDLE,
                        motionSpeed = IDLE_MOTION_SPEED,
                        stageX = REST_STAGE_X,
                        stageY = REST_STAGE_Y,
                        stageScale = ENGAGED_SCALE,
                        reducedMotion = reducedMotion,
                    ),
                )
            }
            is PatsyCompanionIntent.SetReducedMotion -> render(
                state.mode,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = if (intent.enabled) 0f else IDLE_MOTION_SPEED,
                    reducedMotion = intent.enabled,
                ),
            )
        }
    }

    private fun pointOrGuide(target: PatsyCompanionTarget) {
        val normalisedTarget = target.normalised()
        val pose = state.pose.lookAt(normalisedTarget).copy(
            motion = PatsyRigMotion.POINT,
            motionSpeed = 0.45f,
            pointX = normalisedTarget.normalizedX,
            pointY = normalisedTarget.normalizedY,
            headTilt = -lookHorizontal(normalisedTarget) * 0.16f,
            expression = PatsyRigExpression.PROUD,
            expressionIntensity = 0.82f,
            talking = false,
            viseme = PatsyRigViseme.REST,
            visemeIntensity = 0f,
            speechEnergy = 0f,
        )
        render(PatsyCompanionMode.GUIDING, pose)
        retriggerIfMotionAllowed(PatsyRigMotion.POINT)
    }

    private fun react(reaction: PatsyCompanionReaction) {
        val expression = when (reaction) {
            PatsyCompanionReaction.CHEEKY -> PatsyRigExpression.CHEEKY
            PatsyCompanionReaction.CURIOUS -> PatsyRigExpression.CURIOUS
            PatsyCompanionReaction.CONCERNED -> PatsyRigExpression.CONCERNED
            PatsyCompanionReaction.PROUD -> PatsyRigExpression.PROUD
            PatsyCompanionReaction.HAPPY -> PatsyRigExpression.EXCITED
        }
        val tailEnergy = when (reaction) {
            PatsyCompanionReaction.HAPPY -> 0.88f
            PatsyCompanionReaction.CONCERNED -> 0.18f
            PatsyCompanionReaction.CURIOUS -> 0.32f
            PatsyCompanionReaction.CHEEKY,
            PatsyCompanionReaction.PROUD -> 0.48f
        }
        render(
            PatsyCompanionMode.REACTING,
            state.pose.copy(
                motion = PatsyRigMotion.IDLE,
                motionSpeed = IDLE_MOTION_SPEED,
                expression = expression,
                expressionIntensity = 0.82f,
                tailEnergy = tailEnergy,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
    }

    private fun celebrate() {
        render(
            PatsyCompanionMode.CELEBRATING,
            state.pose.copy(
                motion = PatsyRigMotion.WAVE,
                motionSpeed = 0.65f,
                leftEarDrive = 0.25f,
                rightEarDrive = 0.18f,
                tailEnergy = 0.95f,
                expression = PatsyRigExpression.EXCITED,
                expressionIntensity = 0.95f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        retriggerIfMotionAllowed(PatsyRigMotion.WAVE)
    }

    private fun jump() {
        render(
            PatsyCompanionMode.JUMPING,
            state.pose.copy(
                motion = PatsyRigMotion.JUMP,
                motionSpeed = 0.85f,
                leftEarDrive = 0.34f,
                rightEarDrive = 0.28f,
                tailEnergy = 0.85f,
                expression = PatsyRigExpression.EXCITED,
                expressionIntensity = 0.92f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        retriggerIfMotionAllowed(PatsyRigMotion.JUMP)
    }

    private fun sleep() {
        render(
            PatsyCompanionMode.RESTING,
            state.pose.copy(
                motion = PatsyRigMotion.LIE,
                motionSpeed = 0f,
                lookX = 0f,
                lookY = 0f,
                headTilt = 0.08f,
                leftEarDrive = -0.10f,
                rightEarDrive = -0.10f,
                tailDrive = 0f,
                tailEnergy = 0.12f,
                expression = PatsyRigExpression.SLEEPY,
                expressionIntensity = 0.72f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
    }

    private fun retriggerIfMotionAllowed(action: PatsyRigMotion) {
        if (!state.pose.reducedMotion) rig.retriggerAction(action)
    }

    private fun render(mode: PatsyCompanionMode, pose: PatsyRigPose) {
        state = PatsyCompanionState(mode = mode, pose = pose.normalised())
        rig.render(state.pose)
    }

    private fun PatsyRigPose.lookAt(target: PatsyCompanionTarget): PatsyRigPose {
        val normalised = target.normalised()
        return copy(
            lookX = ((normalised.normalizedX - 0.5f) * 2f).coerceIn(-1f, 1f),
            lookY = ((normalised.normalizedY - 0.5f) * 2f).coerceIn(-1f, 1f),
        )
    }

    private fun PatsyRigPose.idleInteraction(): PatsyRigPose = copy(
        motion = PatsyRigMotion.IDLE,
        motionSpeed = IDLE_MOTION_SPEED,
        lookX = 0f,
        lookY = 0f,
        headTilt = 0f,
        leftEarDrive = 0f,
        rightEarDrive = 0f,
        tailDrive = 0f,
        tailEnergy = 0.35f,
        expression = PatsyRigExpression.CHEEKY,
        expressionIntensity = 0.5f,
        talking = false,
        viseme = PatsyRigViseme.REST,
        visemeIntensity = 0f,
        speechEnergy = 0f,
    )

    private fun lookHorizontal(target: PatsyCompanionTarget): Float =
        ((target.normalised().normalizedX - 0.5f) * 2f).coerceIn(-1f, 1f)

    private companion object {
        const val REST_STAGE_X = 0.5f
        const val REST_STAGE_Y = 0.75f
        const val HELPER_SCALE = 0.68f
        const val ENGAGED_SCALE = 1f
        const val IDLE_MOTION_SPEED = 0.12f
    }
}
