package com.patsy.app.studio

import com.patsy.app.studio.editor.StudioEditor
import com.patsy.app.studio.editor.StudioLayer
import com.patsy.app.studio.editor.StudioLayerType
import com.patsy.app.studio.sizing.LayerBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioEditorTest {
    private fun layer(id: String = "layer-1") = StudioLayer(
        id = id,
        type = StudioLayerType.TEXT,
        bounds = LayerBounds(10f, 20f, 100f, 50f),
        text = "Hello",
    )

    @Test
    fun editorOperationsAreUndoableAndRedoable() {
        val editor = StudioEditor()
        editor.add(layer())
        editor.move("layer-1", 50f, 60f)
        editor.resize("layer-1", 200f, 80f)
        editor.updateText("layer-1", "Updated")

        assertEquals("Updated", editor.state.layers.single().text)
        assertTrue(editor.undo())
        assertEquals("Hello", editor.state.layers.single().text)
        assertTrue(editor.undo())
        assertEquals(100f, editor.state.layers.single().bounds.width)
        assertTrue(editor.redo())
        assertEquals(200f, editor.state.layers.single().bounds.width)
    }

    @Test
    fun duplicateGetsIndependentStableLayerId() {
        val editor = StudioEditor()
        editor.add(layer())
        editor.duplicate("layer-1", "layer-2")
        assertEquals(setOf("layer-1", "layer-2"), editor.state.layers.map { it.id }.toSet())
    }

    @Test
    fun deleteClearsSelectionWhenSelectedLayerIsRemoved() {
        val editor = StudioEditor()
        editor.add(layer())
        assertEquals("layer-1", editor.state.selectedLayerId)
        editor.delete("layer-1")
        assertTrue(editor.state.layers.isEmpty())
        assertEquals(null, editor.state.selectedLayerId)
    }

    @Test
    fun unavailableHistoryDoesNotPretendUndoSucceeded() {
        val editor = StudioEditor()
        assertFalse(editor.undo())
        assertFalse(editor.redo())
    }
}
