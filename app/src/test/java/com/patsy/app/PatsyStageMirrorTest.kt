package com.patsy.app

import kotlin.test.Test
import kotlin.test.assertEquals

class PatsyStageMirrorTest {
    @Test
    fun centreOfViewportMapsToCentreOfRiveStage() {
        val snapshot = normalisePatsyStage(
            centreX = 540f,
            centreY = 960f,
            viewportWidth = 1080f,
            viewportHeight = 1920f,
        )

        assertEquals(0.5f, snapshot.x, 0.0001f)
        assertEquals(0.5f, snapshot.y, 0.0001f)
    }

    @Test
    fun stageCoordinatesClampToSafeNormalisedRange() {
        val snapshot = normalisePatsyStage(
            centreX = -100f,
            centreY = 2500f,
            viewportWidth = 1080f,
            viewportHeight = 1920f,
        )

        assertEquals(0f, snapshot.x, 0.0001f)
        assertEquals(1f, snapshot.y, 0.0001f)
    }

    @Test
    fun invalidViewportFallsBackToLockedDefaultStagePosition() {
        val snapshot = normalisePatsyStage(
            centreX = 10f,
            centreY = 10f,
            viewportWidth = 0f,
            viewportHeight = -1f,
        )

        assertEquals(0.5f, snapshot.x, 0.0001f)
        assertEquals(0.75f, snapshot.y, 0.0001f)
    }
}
