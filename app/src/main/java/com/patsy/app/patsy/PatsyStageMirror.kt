package com.patsy.app

internal data class PatsyStageSnapshot(
    val x: Float = 0.5f,
    val y: Float = 0.75f,
)

/**
 * Converts the centre of Patsy's Compose host into the 0..1 stage coordinates defined by the
 * locked Rive ABI. Invalid/unmeasured viewports keep the contract default instead of emitting
 * NaN/Infinity or making the rig jump to an unsafe edge.
 */
internal fun normalisePatsyStage(
    centreX: Float,
    centreY: Float,
    viewportWidth: Float,
    viewportHeight: Float,
): PatsyStageSnapshot {
    if (viewportWidth <= 0f || viewportHeight <= 0f) return PatsyStageSnapshot()

    return PatsyStageSnapshot(
        x = (centreX / viewportWidth).coerceIn(0f, 1f),
        y = (centreY / viewportHeight).coerceIn(0f, 1f),
    )
}
