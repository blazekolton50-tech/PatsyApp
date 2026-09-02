package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class PatsyCompanionTarget(
    val normalizedX: Float,
    val normalizedY: Float,
) {
    fun normalised(): PatsyCompanionTarget = copy(
        normalizedX = normalizedX.coerceIn(0f, 1f),
        normalizedY = normalizedY.coerceIn(0f, 1f),
    )
}

enum class PatsyCompanionMode {
    IDLE,
    SHRINKING,
    TRAVELLING,
    GUIDING,
    RETURNING,
    EXPANDING,
}

enum class PatsyCompanionEffectKind {
    RISING_RAINBOW_GLITTER,
}

data class PatsyCompanionEffect(
    val kind: PatsyCompanionEffectKind,
    val assetName: String,
    val playbackSpeed: Float,
)

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
 * App-owned companion movement controller.
 *
 * Big = 300 visual units / scale 1.0. Mini = 150 visual units / scale 0.5.
 * Shrink lasts exactly 0.8 s and the mission run lasts exactly 0.4 s.
 * The same Patsy rig is used throughout; no second mini sprite/artboard is introduced.
 */
class PatsyCompanionController(
    private val rig: PatsyRigCoordinator,
    private val onStateChanged: (PatsyCompanionState) -> Unit = {},
    private val onEffectRequested: (PatsyCompanionEffect) -> Unit = {},
    private val frameWait: suspend (Long) -> Unit = { delay(it) },
) {
    var state: PatsyCompanionState = PatsyCompanionState()
        private set

    init {
        rig.render(state.pose)
    }

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

        onEffectRequested(CANONICAL_SHRINK_EFFECT)
        animateScale(
            destination = HELPER_SCALE,
            mode = PatsyCompanionMode.SHRINKING,
            motion = PatsyRigMotion.IDLE,
            frames = SHRINK_FRAMES,
            frameMillis = SHRINK_FRAME_MILLIS,
        )

        val anchor = guideAnchor(normalisedTarget)
        animatePosition(
            destination = anchor,
            mode = PatsyCompanionMode.TRAVELLING,
            motion = PatsyRigMotion.RUN,
            frames = MISSION_RUN_FRAMES,
            frameMillis = MISSION_RUN_FRAME_MILLIS,
        )

        render(
            PatsyCompanionMode.GUIDING,
            state.pose.copy(
                motion = PatsyRigMotion.POINT,
                motionSpeed = 0.55f,
                stageScale = HELPER_SCALE,
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

    suspend fun returnHome() {
        if (state.pose.reducedMotion) {
            render(PatsyCompanionMode.IDLE, neutralHomePose(reducedMotion = true))
            return
        }

        render(
            PatsyCompanionMode.RETURNING,
            state.pose.copy(
                motion = PatsyRigMotion.WALK,
                motionSpeed = 0.72f,
                stageScale = HELPER_SCALE,
                talking = false,
                viseme = PatsyRigViseme.REST,
                visemeIntensity = 0f,
                speechEnergy = 0f,
            ),
        )
        val home = PatsyCompanionTarget(REST_STAGE_X, REST_STAGE_Y)
        val distance = kotlin.math.abs(home.normalizedX - state.pose.stageX) +
            kotlin.math.abs(home.normalizedY - state.pose.stageY)
        val returnFrames = (MIN_RETURN_FRAMES + distance * EXTRA_RETURN_FRAMES)
            .roundToInt()
            .coerceAtLeast(MIN_RETURN_FRAMES)
        animatePosition(
            destination = home,
            mode = PatsyCompanionMode.RETURNING,
            motion = PatsyRigMotion.WALK,
            frames = returnFrames,
            frameMillis = RETURN_FRAME_MILLIS,
        )
        animateScale(
            destination = FULL_SCALE,
            mode = PatsyCompanionMode.EXPANDING,
            motion = PatsyRigMotion.IDLE,
            frames = EXPAND_FRAMES,
            frameMillis = EXPAND_FRAME_MILLIS,
        )
        render(PatsyCompanionMode.IDLE, neutralHomePose(reducedMotion = false))
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

    private fun neutralHomePose(reducedMotion: Boolean): PatsyRigPose = state.pose.copy(
        motion = PatsyRigMotion.IDLE,
        motionSpeed = if (reducedMotion) 0f else IDLE_MOTION_SPEED,
        stageX = REST_STAGE_X,
        stageY = REST_STAGE_Y,
        stageScale = FULL_SCALE,
        pointX = 0.5f,
        pointY = 0.5f,
        lookX = 0f,
        lookY = 0f,
        headTilt = 0f,
        expression = PatsyRigExpression.NEUTRAL,
        expressionIntensity = 0f,
        talking = false,
        viseme = PatsyRigViseme.REST,
        visemeIntensity = 0f,
        speechEnergy = 0f,
        reducedMotion = reducedMotion,
    )

    private suspend fun animatePosition(
        destination: PatsyCompanionTarget,
        mode: PatsyCompanionMode,
        motion: PatsyRigMotion,
        frames: Int,
        frameMillis: Long,
    ) {
        val startX = state.pose.stageX
        val startY = state.pose.stageY
        val facing = if (destination.normalizedX < startX) -1f else 1f

        repeat(frames) { index ->
            val progress = (index + 1).toFloat() / frames.toFloat()
            val eased = smoothStep(progress)
            render(
                mode,
                state.pose.copy(
                    motion = motion,
                    motionSpeed = if (motion == PatsyRigMotion.RUN) 1f else 0.72f,
                    facing = facing,
                    stageX = lerp(startX, destination.normalizedX, eased),
                    stageY = lerp(startY, destination.normalizedY, eased),
                    stageScale = HELPER_SCALE,
                    talking = false,
                    viseme = PatsyRigViseme.REST,
                    visemeIntensity = 0f,
                    speechEnergy = 0f,
                ),
            )
            frameWait(frameMillis)
        }
    }

    private suspend fun animateScale(
        destination: Float,
        mode: PatsyCompanionMode,
        motion: PatsyRigMotion,
        frames: Int,
        frameMillis: Long,
    ) {
        val start = state.pose.stageScale
        repeat(frames) { index ->
            val progress = (index + 1).toFloat() / frames.toFloat()
            val eased = if (mode == PatsyCompanionMode.SHRINKING) easeOutCubic(progress) else smoothStep(progress)
            render(
                mode,
                state.pose.copy(
                    motion = motion,
                    motionSpeed = if (motion == PatsyRigMotion.WALK) 0.72f else IDLE_MOTION_SPEED,
                    stageScale = lerp(start, destination, eased),
                ),
            )
            frameWait(frameMillis)
        }
    }

    private fun guideAnchor(target: PatsyCompanionTarget): PatsyCompanionTarget {
        val targetOnRight = target.normalizedX >= 0.5f
        val x = if (targetOnRight) target.normalizedX - GUIDE_SIDE_OFFSET else target.normalizedX + GUIDE_SIDE_OFFSET
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
        val inverse = 1f - value
        return 1f - (inverse * inverse * inverse)
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float = start + (end - start) * progress

    private companion object {
        const val REST_STAGE_X = 0.50f
        const val REST_STAGE_Y = 0.75f
        const val FULL_SCALE = 1.00f
        const val HELPER_SCALE = 0.50f
        const val REDUCED_HELPER_SCALE = 0.80f
        const val IDLE_MOTION_SPEED = 0.12f
        const val GUIDE_SIDE_OFFSET = 0.18f
        const val STAGE_EDGE_MARGIN = 0.08f
        const val STAGE_TOP_MARGIN = 0.12f
        const val STAGE_BOTTOM_MARGIN = 0.86f
        const val SHRINK_FRAMES = 10
        const val SHRINK_FRAME_MILLIS = 80L
        const val MISSION_RUN_FRAMES = 10
        const val MISSION_RUN_FRAME_MILLIS = 40L
        const val EXPAND_FRAMES = 10
        const val EXPAND_FRAME_MILLIS = 60L
        const val MIN_RETURN_FRAMES = 8
        const val EXTRA_RETURN_FRAMES = 18f
        const val RETURN_FRAME_MILLIS = 16L
        val CANONICAL_SHRINK_EFFECT = PatsyCompanionEffect(
            kind = PatsyCompanionEffectKind.RISING_RAINBOW_GLITTER,
            assetName = "video4635308202773325454.mp4",
            playbackSpeed = 8f,
        )
    }
}

private const val REST_STAGE_X = 0.50f
private const val REST_STAGE_Y = 0.75f
private const val FULL_SCALE = 1.00f
private const val IDLE_MOTION_SPEED = 0.12f
