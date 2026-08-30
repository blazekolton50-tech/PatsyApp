package com.patsy.app.studio.editor

import com.patsy.app.studio.sizing.LayerBounds

enum class StudioLayerType { TEXT, IMAGE, VIDEO, SHAPE, STICKER, PAWMOJI, ILLUSTRATION, PATTERN, AUDIO, OVERLAY }

data class StudioLayer(
    val id: String,
    val sourceAssetId: String? = null,
    val type: StudioLayerType,
    val bounds: LayerBounds,
    val rotationDegrees: Float = 0f,
    val zOrder: Int = 0,
    val opacity: Float = 1f,
    val visible: Boolean = true,
    val locked: Boolean = false,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val text: String? = null,
    val filterIds: List<String> = emptyList(),
    val effectIds: List<String> = emptyList(),
)

data class EditorSnapshot(
    val layers: List<StudioLayer> = emptyList(),
    val selectedLayerId: String? = null,
)

class StudioEditor(initial: EditorSnapshot = EditorSnapshot(), private val historyLimit: Int = 50) {
    var state: EditorSnapshot = initial
        private set

    private val undoStack = mutableListOf<EditorSnapshot>()
    private val redoStack = mutableListOf<EditorSnapshot>()

    fun select(id: String?) {
        require(id == null || state.layers.any { it.id == id }) { "unknown layer" }
        state = state.copy(selectedLayerId = id)
    }

    fun add(layer: StudioLayer) = mutate {
        require(it.layers.none { existing -> existing.id == layer.id }) { "duplicate layer id" }
        it.copy(layers = (it.layers + layer).sortedBy(StudioLayer::zOrder), selectedLayerId = layer.id)
    }

    fun move(id: String, x: Float, y: Float) = updateLayer(id) { layer ->
        layer.copy(bounds = layer.bounds.copy(x = x, y = y))
    }

    fun resize(id: String, width: Float, height: Float) {
        require(width > 0 && height > 0) { "layer dimensions must be positive" }
        updateLayer(id) { layer -> layer.copy(bounds = layer.bounds.copy(width = width, height = height)) }
    }

    fun rotate(id: String, degrees: Float) = updateLayer(id) { it.copy(rotationDegrees = degrees) }
    fun setZ(id: String, zOrder: Int) = updateLayer(id) { it.copy(zOrder = zOrder) }
    fun setOpacity(id: String, opacity: Float) = updateLayer(id) { it.copy(opacity = opacity.coerceIn(0f, 1f)) }
    fun setLocked(id: String, locked: Boolean) = updateLayer(id) { it.copy(locked = locked) }
    fun setVisible(id: String, visible: Boolean) = updateLayer(id) { it.copy(visible = visible) }
    fun flipHorizontal(id: String) = updateLayer(id) { it.copy(flipHorizontal = !it.flipHorizontal) }
    fun flipVertical(id: String) = updateLayer(id) { it.copy(flipVertical = !it.flipVertical) }

    fun updateText(id: String, text: String) = updateLayer(id) {
        require(it.type == StudioLayerType.TEXT) { "layer is not text" }
        it.copy(text = text)
    }

    fun applyFilter(id: String, filterId: String) = updateLayer(id) {
        it.copy(filterIds = (it.filterIds + filterId).distinct())
    }

    fun removeFilter(id: String, filterId: String) = updateLayer(id) {
        it.copy(filterIds = it.filterIds - filterId)
    }

    fun applyEffect(id: String, effectId: String) = updateLayer(id) {
        it.copy(effectIds = (it.effectIds + effectId).distinct())
    }

    fun removeEffect(id: String, effectId: String) = updateLayer(id) {
        it.copy(effectIds = it.effectIds - effectId)
    }

    fun duplicate(id: String, newId: String) = mutate { snapshot ->
        require(snapshot.layers.none { it.id == newId }) { "duplicate layer id" }
        val source = snapshot.requireLayer(id)
        val copy = source.copy(id = newId, zOrder = (snapshot.layers.maxOfOrNull { it.zOrder } ?: -1) + 1)
        snapshot.copy(layers = snapshot.layers + copy, selectedLayerId = newId)
    }

    fun delete(id: String) = mutate { snapshot ->
        snapshot.requireLayer(id)
        snapshot.copy(
            layers = snapshot.layers.filterNot { it.id == id },
            selectedLayerId = snapshot.selectedLayerId.takeUnless { it == id },
        )
    }

    fun undo(): Boolean {
        val previous = undoStack.removeLastOrNull() ?: return false
        redoStack.add(state)
        state = previous
        return true
    }

    fun redo(): Boolean {
        val next = redoStack.removeLastOrNull() ?: return false
        undoStack.add(state)
        state = next
        return true
    }

    private fun updateLayer(id: String, transform: (StudioLayer) -> StudioLayer) = mutate { snapshot ->
        snapshot.requireLayer(id)
        snapshot.copy(layers = snapshot.layers.map { if (it.id == id) transform(it) else it }.sortedBy(StudioLayer::zOrder))
    }

    private fun mutate(block: (EditorSnapshot) -> EditorSnapshot) {
        val before = state
        val after = block(before)
        if (after == before) return
        undoStack.add(before)
        while (undoStack.size > historyLimit) undoStack.removeAt(0)
        redoStack.clear()
        state = after
    }

    private fun EditorSnapshot.requireLayer(id: String): StudioLayer =
        layers.firstOrNull { it.id == id } ?: error("unknown layer: $id")
}
