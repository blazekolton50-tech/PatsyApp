package com.patsy.app.studio.design

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioDesignCanvasStateTest {
    private fun layer(
        id: String,
        x: Float,
        y: Float,
        width: Float = 200f,
        height: Float = 100f,
        locked: Boolean = false,
        hidden: Boolean = false,
    ) = StudioDesignLayer(
        id = id,
        type = StudioDesignLayerType.IMAGE,
        name = id,
        transform = StudioDesignTransform(
            x = x,
            y = y,
            width = width,
            height = height,
        ),
        locked = locked,
        hidden = hidden,
    )

    @Test
    fun selectionMoveResizeRotateAndOpacityAreDeterministic() {
        val initial = StudioDesignCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            layers = listOf(layer("dog", 100f, 200f)),
        )

        val selected = reduceStudioDesignCanvas(initial, StudioDesignAction.Select("dog"))
        val moved = reduceStudioDesignCanvas(selected, StudioDesignAction.MoveSelected(dx = 25f, dy = -50f))
        val resized = reduceStudioDesignCanvas(moved, StudioDesignAction.ResizeSelected(width = 320f, height = 240f))
        val rotated = reduceStudioDesignCanvas(resized, StudioDesignAction.RotateSelected(25f))
        val faded = reduceStudioDesignCanvas(rotated, StudioDesignAction.SetSelectedOpacity(0.55f))

        val dog = faded.layers.single()
        assertEquals("dog", faded.selectedLayerId)
        assertEquals(125f, dog.transform.x)
        assertEquals(150f, dog.transform.y)
        assertEquals(320f, dog.transform.width)
        assertEquals(240f, dog.transform.height)
        assertEquals(25f, dog.transform.rotationDegrees)
        assertEquals(0.55f, dog.opacity)
    }

    @Test
    fun lockedLayerCannotBeTransformedOrDeleted() {
        val initial = StudioDesignCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            layers = listOf(layer("locked", 100f, 100f, locked = true)),
            selectedLayerId = "locked",
        )

        val moved = reduceStudioDesignCanvas(initial, StudioDesignAction.MoveSelected(100f, 100f))
        val deleted = reduceStudioDesignCanvas(moved, StudioDesignAction.DeleteSelected)

        assertEquals(initial.layers, moved.layers)
        assertEquals(initial.layers, deleted.layers)
        assertEquals("locked", deleted.selectedLayerId)
    }

    @Test
    fun hideLockAndReorderPreserveStableLayerIdentity() {
        val initial = StudioDesignCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            layers = listOf(
                layer("background", 0f, 0f),
                layer("text", 100f, 100f),
                layer("photo", 200f, 200f),
            ),
        )

        val hidden = reduceStudioDesignCanvas(initial, StudioDesignAction.SetHidden("text", true))
        val locked = reduceStudioDesignCanvas(hidden, StudioDesignAction.SetLocked("photo", true))
        val raised = reduceStudioDesignCanvas(locked, StudioDesignAction.BringForward("background"))

        assertTrue(raised.layers.first { it.id == "text" }.hidden)
        assertTrue(raised.layers.first { it.id == "photo" }.locked)
        assertEquals(listOf("text", "background", "photo"), raised.layers.map { it.id })
    }

    @Test
    fun canvasRejectsInvalidDimensionsAndClampsOpacity() {
        val initial = StudioDesignCanvasState(
            widthPx = 0,
            heightPx = -1,
            layers = listOf(layer("dog", 0f, 0f)),
            selectedLayerId = "dog",
        )

        assertEquals(1, initial.widthPx)
        assertEquals(1, initial.heightPx)

        val transparent = reduceStudioDesignCanvas(initial, StudioDesignAction.SetSelectedOpacity(-4f))
        val opaque = reduceStudioDesignCanvas(transparent, StudioDesignAction.SetSelectedOpacity(4f))
        assertEquals(1f, opaque.layers.single().opacity)
    }

    @Test
    fun deletingSelectedUnlockedLayerClearsSelection() {
        val initial = StudioDesignCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            layers = listOf(layer("dog", 0f, 0f)),
            selectedLayerId = "dog",
        )

        val deleted = reduceStudioDesignCanvas(initial, StudioDesignAction.DeleteSelected)

        assertTrue(deleted.layers.isEmpty())
        assertEquals(null, deleted.selectedLayerId)
        assertFalse(deleted.hasSelection)
    }
}
