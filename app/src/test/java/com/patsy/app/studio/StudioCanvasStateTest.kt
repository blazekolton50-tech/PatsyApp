package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals

class StudioCanvasStateTest {
    @Test
    fun selectingExistingObjectPreservesItsEditableTransformProperties() {
        val textObject = StudioCanvasObject(
            id = "text-1",
            type = StudioLayerType.TEXT,
            label = "Text - CREATE",
            xPx = 120f,
            yPx = 80f,
            widthPx = 300f,
            heightPx = 90f,
            rotationDegrees = -3f,
            opacity = 0.8f,
        )
        val state = StudioCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            objects = listOf(textObject),
        )

        val selected = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Select("text-1"),
        )

        assertEquals("text-1", selected.selectedObjectId)
        assertEquals(textObject, selected.objects.single())
    }

    @Test
    fun movementAppliesToUnlockedObjectsAndIsRejectedByLockedObjects() {
        val free = StudioCanvasObject(
            id = "free",
            type = StudioLayerType.SHAPE,
            label = "Free shape",
            xPx = 10f,
            yPx = 20f,
            widthPx = 100f,
            heightPx = 80f,
        )
        val locked = StudioCanvasObject(
            id = "locked",
            type = StudioLayerType.IMAGE,
            label = "Locked image",
            xPx = 30f,
            yPx = 40f,
            widthPx = 120f,
            heightPx = 90f,
            locked = true,
        )
        val state = StudioCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            objects = listOf(free, locked),
        )

        val movedFree = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Move("free", deltaXPx = 5f, deltaYPx = 7f),
        )
        val attemptedLockedMove = reduceStudioCanvasState(
            movedFree,
            StudioCanvasAction.Move("locked", deltaXPx = 5f, deltaYPx = 7f),
        )

        assertEquals(15f, attemptedLockedMove.objects.first { it.id == "free" }.xPx)
        assertEquals(27f, attemptedLockedMove.objects.first { it.id == "free" }.yPx)
        assertEquals(30f, attemptedLockedMove.objects.first { it.id == "locked" }.xPx)
        assertEquals(40f, attemptedLockedMove.objects.first { it.id == "locked" }.yPx)
    }
}
