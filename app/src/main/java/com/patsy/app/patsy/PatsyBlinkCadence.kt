package com.patsy.app

private val PatsyBlinkCadenceMillis = longArrayOf(
    2_700L,
    4_100L,
    3_200L,
    5_300L,
    2_400L,
    3_700L,
    4_800L,
    3_000L,
)

/**
 * Stable varied cadence for natural blinking. Deterministic timing keeps tests reproducible while
 * avoiding the robotic fixed-period stare of a single repeating blink interval.
 */
internal fun patsyBlinkDelayMillis(sequence: Long): Long {
    val index = Math.floorMod(sequence, PatsyBlinkCadenceMillis.size.toLong()).toInt()
    return PatsyBlinkCadenceMillis[index]
}
