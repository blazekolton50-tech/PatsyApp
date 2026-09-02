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

enum class PatsyCompanionMode {
    IDLE,
    SHRINKING,
    TRAVELLING,
    GUIDING,
    RETURNING,
    EXPANDING,
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
 * App-owned companion movement controller.
 *
 * The same Patsy rig is used at every size. Travel is performed by repeatedly updating the
 * existing stage/x, stage/y and stage/scale ABI values; no alternate mini sprite/artboard is used.
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

    /** Shrink, travel beside [target], then point at the target while remaining small. */
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

        animateScale(
            destination = HELPER_SCALE,
            mode = PatsyCompanionMode.SHRINKING,
            motion = PatsyRigMotion.IDLE,
            frames = SHRINK_FRAMES,
        )

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

    /** Walk back to Patsy's resting position while small, then expand to normal size and idle. */
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
                stageScale = HELPER_SCALE,
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
                    stageScale = HELPER_SCALE,
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
        const val HELPER_SCALE = 0.45f
        const val REDUCED_HELPER_SCALE = 0.80f
        const val IDLE_MOTION_SPEED = 0.12f
        const val GUIDE_SIDE_OFFSET = 0.18f
        const val STAGE_EDGE_MARGIN = 0.08f
        const val STAGE_TOP_MARGIN = 0.12f
        const val STAGE_BOTTOM_MARGIN = 0.86f
        const val MISSION_SHRINK_FRAMES = 50
        const val SHRINK_FRAMES = 6
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
