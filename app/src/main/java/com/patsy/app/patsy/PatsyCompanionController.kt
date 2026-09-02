package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** A UI target expressed as normalised coordinates inside the full companion surface. */
data class PatsyCompanionTarget(
    val normalizedX: Float,
    val normalizedY: Float,
) {
    fun normalised(): PatsyCompanionTarget = copy(
        normalizedX = normalizedX.coerceIn(0f, 1f),
        normalizedY = normalizedY.coerceIn(0f, 1f),
    )
}

/** Reactions intentionally use only expressions already present in the locked V1 Rive ABI. */
enum class PatsyCompanionReaction {
    CHEEKY,
    CURIOUS,
    CONCERNED,
    PROUD,
    HAPPY,
}

/**
 * Semantic companion commands layered over the existing travel controller and rig ABI.
 * They describe intent, never frames, sprites or alternate Patsy assets.
 */
sealed interface PatsyCompanionIntent {
    data object Blink : PatsyCompanionIntent
    data class TrackEyes(val horizontal: Float, val vertical: Float) : PatsyCompanionIntent
    data class TiltHead(val amount: Float) : PatsyCompanionIntent
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
}

enum class PatsyCompanionMode {
    IDLE,
    SHRINKING,
    TRAVELLING,
    GUIDING,
    RETURNING,
    EXPANDING,
    TRACKING,
    ATTENTIVE,
    THINKING,
    LISTENING,
    SPEAKING,
    REACTING,
    CELEBRATING,
    JUMPING,
    RESTING,
}

data class PatsyCompanionState(
    val mode: PatsyCompanionMode = PatsyCompanionMode.IDLE,
    val pose: PatsyRigPose = PatsyRigPose(
        motion = PatsyRigMotion.IDLE,
        motionSpeed = IDLE_MOTION_SPEED,
        stageX = REST_STAGE_X,
        stageY = REST_STAGE_Y,
        stageScale = FULL_SCALE,
    ),
)

/**
 * App-owned companion movement and semantic interaction controller.
 *
 * The same Patsy rig is used at every size. Travel is performed by repeatedly updating the
 * existing stage/x, stage/y and stage/scale ABI values; no alternate mini sprite/artboard is used.
 * Semantic interactions are translated only into controls already available in PatsyRigContractV1.
 */
class PatsyCompanionController(
    private val rig: PatsyRigCoordinator,
    private val onStateChanged: (PatsyCompanionState) -> Unit = {},
    private val frameWait: suspend (Long) -> Unit = { delay(it) },
) {
    var state: PatsyCompanionState = PatsyCompanionState()
        private set

    init {
        rig.render(state.pose)
    }

    fun dispatch(intent: PatsyCompanionIntent) {
        when (intent) {
            PatsyCompanionIntent.Blink -> rig.blink()

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

            PatsyCompanionIntent.Think -> render(
                PatsyCompanionMode.THINKING,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = if (state.pose.reducedMotion) 0f else IDLE_MOTION_SPEED,
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
                    motionSpeed = if (state.pose.reducedMotion) 0f else IDLE_MOTION_SPEED,
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
                    motionSpeed = if (state.pose.reducedMotion) 0f else IDLE_MOTION_SPEED,
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
        }
    }

    /**
     * Shrink the existing Patsy in place from normal size to the locked one-thumb mission size.
     * The 50 x 16 ms frames preserve the 0.8 second product contract and use EaseOutCubic.
     */
    suspend fun shrinkForMission() {
        if (state.pose.reducedMotion) {
            render(
                PatsyCompanionMode.IDLE,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = 0f,
                    stageScale = MISSION_SCALE,
                ),
            )
            return
        }

        val startScale = state.pose.stageScale
        repeat(MISSION_SHRINK_FRAMES) { index ->
            val progress = (index + 1).toFloat() / MISSION_SHRINK_FRAMES.toFloat()
            render(
                PatsyCompanionMode.SHRINKING,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = IDLE_MOTION_SPEED,
                    stageScale = lerp(startScale, MISSION_SCALE, easeOutCubic(progress)),
                ),
            )
            frameWait(FRAME_MILLIS)
        }

        render(
            PatsyCompanionMode.IDLE,
            state.pose.copy(
                motion = PatsyRigMotion.IDLE,
                motionSpeed = IDLE_MOTION_SPEED,
                stageScale = MISSION_SCALE,
            ),
        )
    }

    /** Shrink to the locked mission size, travel beside [target], then point while remaining small. */
    suspend fun guideTo(target: PatsyCompanionTarget) {
        val normalisedTarget = target.normalised()
        if (state.pose.reducedMotion) {
            val anchor = guideAnchor(normalisedTarget)
            render(
                PatsyCompanionMode.GUIDING,
                state.pose.copy(
                    motion = PatsyRigMotion.POINT,
                    motionSpeed = 0f,
                    stageX = anchor.normalizedX,
                    stageY = anchor.normalizedY,
                    stageScale = REDUCED_HELPER_SCALE,
                    pointX = normalisedTarget.normalizedX,
                    pointY = normalisedTarget.normalizedY,
                ).lookAt(normalisedTarget),
            )
            return
        }

        shrinkForMission()

        val anchor = guideAnchor(normalisedTarget)
        animatePosition(
            destination = anchor,
            mode = PatsyCompanionMode.TRAVELLING,
            motion = PatsyRigMotion.WALK,
        )

        render(
            PatsyCompanionMode.GUIDING,
            state.pose.copy(
                motion = PatsyRigMotion.POINT,
                motionSpeed = 0.55f,
                stageScale = MISSION_SCALE,
                pointX = normalisedTarget.normalizedX,
                pointY = normalisedTarget.normalizedY,
                expression = PatsyRigExpression.PROUD,
                expressionIntensity = 0.82f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ).lookAt(normalisedTarget),
        )
        rig.retriggerAction(PatsyRigMotion.POINT)
    }

    /** Walk back to Patsy's resting position at mission size, then expand to normal size and idle. */
    suspend fun returnHome() {
        if (state.pose.reducedMotion) {
            render(
                PatsyCompanionMode.IDLE,
                state.pose.copy(
                    motion = PatsyRigMotion.IDLE,
                    motionSpeed = 0f,
                    stageX = REST_STAGE_X,
                    stageY = REST_STAGE_Y,
                    stageScale = FULL_SCALE,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            return
        }

        render(
            PatsyCompanionMode.RETURNING,
            state.pose.copy(
                motion = PatsyRigMotion.WALK,
                motionSpeed = 0.72f,
                stageScale = MISSION_SCALE,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        animatePosition(
            destination = PatsyCompanionTarget(REST_STAGE_X, REST_STAGE_Y),
            mode = PatsyCompanionMode.RETURNING,
            motion = PatsyRigMotion.WALK,
        )
        animateScale(
            destination = FULL_SCALE,
            mode = PatsyCompanionMode.EXPANDING,
            motion = PatsyRigMotion.IDLE,
            frames = EXPAND_FRAMES,
        )
        render(
            PatsyCompanionMode.IDLE,
            state.pose.copy(
                motion = PatsyRigMotion.IDLE,
                motionSpeed = IDLE_MOTION_SPEED,
                stageX = REST_STAGE_X,
                stageY = REST_STAGE_Y,
                stageScale = FULL_SCALE,
                lookX = 0f,
                lookY = 0f,
                headTilt = 0f,
                expression = PatsyRigExpression.CHEEKY,
                expressionIntensity = 0.5f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
    }

    fun setReducedMotion(enabled: Boolean) {
        render(
            state.mode,
            state.pose.copy(
                reducedMotion = enabled,
                motion = if (enabled) PatsyRigMotion.IDLE else state.pose.motion,
                motionSpeed = if (enabled) 0f else state.pose.motionSpeed,
            ),
        )
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
                motionSpeed = if (state.pose.reducedMotion) 0f else IDLE_MOTION_SPEED,
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
        val reduced = state.pose.reducedMotion
        render(
            PatsyCompanionMode.CELEBRATING,
            state.pose.copy(
                motion = if (reduced) PatsyRigMotion.IDLE else PatsyRigMotion.WAVE,
                motionSpeed = if (reduced) 0f else 0.65f,
                leftEarDrive = if (reduced) 0.12f else 0.25f,
                rightEarDrive = if (reduced) 0.10f else 0.18f,
                tailEnergy = if (reduced) 0.35f else 0.95f,
                expression = PatsyRigExpression.EXCITED,
                expressionIntensity = 0.95f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        if (!reduced) rig.retriggerAction(PatsyRigMotion.WAVE)
    }

    private fun jump() {
        val reduced = state.pose.reducedMotion
        render(
            PatsyCompanionMode.JUMPING,
            state.pose.copy(
                motion = if (reduced) PatsyRigMotion.IDLE else PatsyRigMotion.JUMP,
                motionSpeed = if (reduced) 0f else 0.85f,
                leftEarDrive = if (reduced) 0.12f else 0.34f,
                rightEarDrive = if (reduced) 0.10f else 0.28f,
                tailEnergy = if (reduced) 0.35f else 0.85f,
                expression = PatsyRigExpression.EXCITED,
                expressionIntensity = 0.92f,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        if (!reduced) rig.retriggerAction(PatsyRigMotion.JUMP)
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

    private suspend fun animatePosition(
        destination: PatsyCompanionTarget,
        mode: PatsyCompanionMode,
        motion: PatsyRigMotion,
    ) {
        val startX = state.pose.stageX
        val startY = state.pose.stageY
        val distance = kotlin.math.abs(destination.normalizedX - startX) +
            kotlin.math.abs(destination.normalizedY - startY)
        val frames = (MIN_TRAVEL_FRAMES + distance * EXTRA_TRAVEL_FRAMES)
            .roundToInt()
            .coerceAtLeast(MIN_TRAVEL_FRAMES)

        repeat(frames) { index ->
            val progress = (index + 1).toFloat() / frames.toFloat()
            val eased = smoothStep(progress)
            render(
                mode,
                state.pose.copy(
                    motion = motion,
                    motionSpeed = 0.72f,
                    stageX = lerp(startX, destination.normalizedX, eased),
                    stageY = lerp(startY, destination.normalizedY, eased),
                    stageScale = MISSION_SCALE,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            frameWait(FRAME_MILLIS)
        }
    }

    private suspend fun animateScale(
        destination: Float,
        mode: PatsyCompanionMode,
        motion: PatsyRigMotion,
        frames: Int,
    ) {
        val start = state.pose.stageScale
        repeat(frames) { index ->
            val progress = (index + 1).toFloat() / frames.toFloat()
            render(
                mode,
                state.pose.copy(
                    motion = motion,
                    motionSpeed = if (motion == PatsyRigMotion.WALK) 0.72f else IDLE_MOTION_SPEED,
                    stageScale = lerp(start, destination, smoothStep(progress)),
                ),
            )
            frameWait(FRAME_MILLIS)
        }
    }

    private fun guideAnchor(target: PatsyCompanionTarget): PatsyCompanionTarget {
        val targetOnRight = target.normalizedX >= 0.5f
        val x = if (targetOnRight) {
            target.normalizedX - GUIDE_SIDE_OFFSET
        } else {
            target.normalizedX + GUIDE_SIDE_OFFSET
        }
        return PatsyCompanionTarget(
            normalizedX = x.coerceIn(STAGE_EDGE_MARGIN, 1f - STAGE_EDGE_MARGIN),
            normalizedY = target.normalizedY.coerceIn(STAGE_TOP_MARGIN, STAGE_BOTTOM_MARGIN),
        )
    }

    private fun PatsyRigPose.lookAt(target: PatsyCompanionTarget): PatsyRigPose = copy(
        lookX = ((target.normalizedX - stageX) * 3f).coerceIn(-1f, 1f),
        lookY = ((target.normalizedY - stageY) * 3f).coerceIn(-1f, 1f),
        headTilt = (((target.normalizedX - stageX) * -0.35f)).coerceIn(-0.35f, 0.35f),
    )

    private fun render(mode: PatsyCompanionMode, pose: PatsyRigPose) {
        state = PatsyCompanionState(mode = mode, pose = pose.normalised())
        rig.render(state.pose)
        onStateChanged(state)
    }

    private fun smoothStep(value: Float): Float = value * value * (3f - 2f * value)

    private fun easeOutCubic(value: Float): Float {
        val remaining = 1f - value
        return 1f - (remaining * remaining * remaining)
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float =
        start + (end - start) * progress

    private companion object {
        const val REST_STAGE_X = 0.50f
        const val REST_STAGE_Y = 0.75f
        const val FULL_SCALE = 1.00f
        const val MISSION_SCALE = 0.50f
        const val REDUCED_HELPER_SCALE = 0.80f
        const val IDLE_MOTION_SPEED = 0.12f
        const val GUIDE_SIDE_OFFSET = 0.18f
        const val STAGE_EDGE_MARGIN = 0.08f
        const val STAGE_TOP_MARGIN = 0.12f
        const val STAGE_BOTTOM_MARGIN = 0.86f
        const val MISSION_SHRINK_FRAMES = 50
        const val EXPAND_FRAMES = 7
        const val MIN_TRAVEL_FRAMES = 8
        const val EXTRA_TRAVEL_FRAMES = 18f
        const val FRAME_MILLIS = 16L
    }
}

private const val REST_STAGE_X = 0.50f
private const val REST_STAGE_Y = 0.75f
private const val FULL_SCALE = 1.00f
private const val IDLE_MOTION_SPEED = 0.12f
