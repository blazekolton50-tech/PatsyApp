package com.patsy.app.studio.editor

import com.patsy.app.studio.sizing.LayerBounds

enum class StudioLayerType { TEXT, IMAGE, VIDEO, SHAPE, STICKER, PAWMOJI, ILLUSTRATION, PATTERN, AUDIO, OVERLAY }

enum class StudioTextAlignment { START, CENTER, END }

data class TextProperties(
    val fontAssetId: String? = null,
    val fontSize: Float = 16f,
    val lineHeightMultiplier: Float = 1.2f,
    val letterSpacing: Float = 0f,
    val alignment: StudioTextAlignment = StudioTextAlignment.START,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
) {
    init {
        require(fontSize > 0f) { "font size must be positive" }
        require(lineHeightMultiplier > 0f) { "line height multiplier must be positive" }
    }
}

data class CropState(
    val left: Float = 0f,
    val top: Float = 0f,
    val right: Float = 1f,
    val bottom: Float = 1f,
) {
    init {
        require(left in 0f..1f && top in 0f..1f && right in 0f..1f && bottom in 0f..1f) {
            "crop coordinates must be normalized"
        }
        require(left < right && top < bottom) { "crop bounds must have positive area" }
    }
}

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
    val textProperties: TextProperties? = null,
    val cropState: CropState? = null,
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

    fun setTextProperties(id: String, properties: TextProperties) = updateLayer(id) {
        require(it.type == StudioLayerType.TEXT) { "layer is not text" }
        it.copy(textProperties = properties)
    }

    fun setCrop(id: String, cropState: CropState?) = updateLayer(id) {
        require(it.type in setOf(StudioLayerType.IMAGE, StudioLayerType.VIDEO, StudioLayerType.STICKER, StudioLayerType.PAWMOJI)) {
            "layer type does not support crop"
        }
        it.copy(cropState = cropState)
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

    /** Moves a layer exactly one step toward the front using deterministic z-order + id ordering. */
    fun bringForward(id: String) = reorderOneStep(id, direction = 1)

    /** Moves a layer exactly one step toward the back using deterministic z-order + id ordering. */
    fun sendBackward(id: String) = reorderOneStep(id, direction = -1)

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

    private fun reorderOneStep(id: String, direction: Int) = mutate { snapshot ->
        snapshot.requireLayer(id)
        val ordered = snapshot.layers.sortedWith(compareBy<StudioLayer> { it.zOrder }.thenBy { it.id }).toMutableList()
        val fromIndex = ordered.indexOfFirst { it.id == id }
        val toIndex = (fromIndex + direction).coerceIn(0, ordered.lastIndex)
        if (fromIndex == toIndex) return@mutate snapshot

        val moving = ordered.removeAt(fromIndex)
        ordered.add(toIndex, moving)
        val baseZ = ordered.minOfOrNull { it.zOrder } ?: 0
        snapshot.copy(layers = ordered.mapIndexed { index, layer -> layer.copy(zOrder = baseZ + index) })
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
