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
}
