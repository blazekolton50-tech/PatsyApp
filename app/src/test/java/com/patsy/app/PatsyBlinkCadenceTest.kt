package com.patsy.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyBlinkCadenceTest {
    @Test
    fun blinkCadenceVariesInsideNaturalIdleWindow() {
        val delays = (0L until 8L).map(::patsyBlinkDelayMillis)

        assertTrue(delays.all { it in 2_200L..5_600L })
        assertTrue(delays.toSet().size >= 4, "Patsy must not blink like a fixed metronome")
    }

    @Test
    fun blinkCadenceRepeatsDeterministicallyForStableTests() {
        assertEquals(patsyBlinkDelayMillis(0), patsyBlinkDelayMillis(8))
        assertEquals(patsyBlinkDelayMillis(3), patsyBlinkDelayMillis(11))
    }
}
