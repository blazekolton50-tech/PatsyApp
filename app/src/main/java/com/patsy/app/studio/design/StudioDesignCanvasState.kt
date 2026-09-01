package com.patsy.app.studio.design

enum class StudioDesignLayerType {
    IMAGE,
    TEXT,
    SHAPE,
    STICKER,
    DRAWING,
    FRAME,
    BACKGROUND,
    GROUP,
}

data class StudioDesignTransform(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotationDegrees: Float = 0f,
) {
    fun normalized(): StudioDesignTransform = copy(
        width = width.coerceAtLeast(1f),
        height = height.coerceAtLeast(1f),
    )
}

data class StudioDesignLayer(
    val id: String,
    val type: StudioDesignLayerType,
    val name: String,
    val transform: StudioDesignTransform,
    val opacity: Float = 1f,
    val locked: Boolean = false,
    val hidden: Boolean = false,
) {
    fun normalized(): StudioDesignLayer = copy(
        transform = transform.normalized(),
        opacity = opacity.coerceIn(0f, 1f),
    )
}

data class StudioDesignCanvasState(
    private val requestedWidthPx: Int,
    private val requestedHeightPx: Int,
    val layers: List<StudioDesignLayer> = emptyList(),
    val selectedLayerId: String? = null,
) {
    val widthPx: Int = requestedWidthPx.coerceAtLeast(1)
    val heightPx: Int = requestedHeightPx.coerceAtLeast(1)
    val hasSelection: Boolean
        get() = selectedLayerId != null && layers.any { it.id == selectedLayerId }

    constructor(
        widthPx: Int,
        heightPx: Int,
        layers: List<StudioDesignLayer> = emptyList(),
        selectedLayerId: String? = null,
        normalizeLayers: Boolean = true,
    ) : this(
        requestedWidthPx = widthPx,
        requestedHeightPx = heightPx,
        layers = if (normalizeLayers) layers.map { it.normalized() } else layers,
        selectedLayerId = selectedLayerId?.takeIf { id -> layers.any { it.id == id } },
    )
}

sealed interface StudioDesignAction {
    data class Select(val layerId: String?) : StudioDesignAction
    data class MoveSelected(val dx: Float, val dy: Float) : StudioDesignAction
    data class ResizeSelected(val width: Float, val height: Float) : StudioDesignAction
    data class RotateSelected(val rotationDegrees: Float) : StudioDesignAction
    data class SetSelectedOpacity(val opacity: Float) : StudioDesignAction
    data class SetHidden(val layerId: String, val hidden: Boolean) : StudioDesignAction
    data class SetLocked(val layerId: String, val locked: Boolean) : StudioDesignAction
    data class BringForward(val layerId: String) : StudioDesignAction
    data class SendBackward(val layerId: String) : StudioDesignAction
    data object DeleteSelected : StudioDesignAction
}

fun reduceStudioDesignCanvas(
    state: StudioDesignCanvasState,
    action: StudioDesignAction,
): StudioDesignCanvasState = when (action) {
    is StudioDesignAction.Select -> state.copy(
        selectedLayerId = action.layerId?.takeIf { id -> state.layers.any { it.id == id } },
    )

    is StudioDesignAction.MoveSelected -> state.updateSelectedUnlocked { layer ->
        layer.copy(
            transform = layer.transform.copy(
                x = layer.transform.x + action.dx,
                y = layer.transform.y + action.dy,
            ),
        )
    }

    is StudioDesignAction.ResizeSelected -> state.updateSelectedUnlocked { layer ->
        layer.copy(
            transform = layer.transform.copy(
                width = action.width.coerceAtLeast(1f),
                height = action.height.coerceAtLeast(1f),
            ),
        )
    }

    is StudioDesignAction.RotateSelected -> state.updateSelectedUnlocked { layer ->
        layer.copy(transform = layer.transform.copy(rotationDegrees = action.rotationDegrees))
    }

    is StudioDesignAction.SetSelectedOpacity -> state.updateSelectedUnlocked { layer ->
        layer.copy(opacity = action.opacity.coerceIn(0f, 1f))
    }

    is StudioDesignAction.SetHidden -> state.copy(
        layers = state.layers.map { layer ->
            if (layer.id == action.layerId) layer.copy(hidden = action.hidden) else layer
        },
    )

    is StudioDesignAction.SetLocked -> state.copy(
        layers = state.layers.map { layer ->
            if (layer.id == action.layerId) layer.copy(locked = action.locked) else layer
        },
    )

    is StudioDesignAction.BringForward -> state.reorderOneStep(action.layerId, +1)
    is StudioDesignAction.SendBackward -> state.reorderOneStep(action.layerId, -1)

    StudioDesignAction.DeleteSelected -> {
        val selected = state.selectedLayerId
        val layer = selected?.let { id -> state.layers.firstOrNull { it.id == id } }
        if (selected == null || layer == null || layer.locked) state
        else state.copy(
            layers = state.layers.filterNot { it.id == selected },
            selectedLayerId = null,
        )
    }
}

private inline fun StudioDesignCanvasState.updateSelectedUnlocked(
    transform: (StudioDesignLayer) -> StudioDesignLayer,
): StudioDesignCanvasState {
    val selected = selectedLayerId ?: return this
    val current = layers.firstOrNull { it.id == selected } ?: return copy(selectedLayerId = null)
    if (current.locked) return this
    return copy(
        layers = layers.map { layer ->
            if (layer.id == selected) transform(layer).normalized() else layer
        },
    )
}

private fun StudioDesignCanvasState.reorderOneStep(
    layerId: String,
    delta: Int,
): StudioDesignCanvasState {
    val fromIndex = layers.indexOfFirst { it.id == layerId }
    if (fromIndex < 0) return this
    val targetIndex = (fromIndex + delta).coerceIn(0, layers.lastIndex)
    if (fromIndex == targetIndex) return this

    val reordered = layers.toMutableList()
    val item = reordered.removeAt(fromIndex)
    reordered.add(targetIndex, item)
    return copy(layers = reordered)
}
