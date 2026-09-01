package com.patsy.app.studio

data class StudioCanvasObject(
    val id: String,
    val type: StudioLayerType,
    val label: String,
    val xPx: Float,
    val yPx: Float,
    val widthPx: Float,
    val heightPx: Float,
    val rotationDegrees: Float = 0f,
    val opacity: Float = 1f,
    val visible: Boolean = true,
    val locked: Boolean = false,
)

data class StudioCanvasState(
    val widthPx: Int,
    val heightPx: Int,
    val objects: List<StudioCanvasObject> = emptyList(),
    val selectedObjectId: String? = null,
)

sealed interface StudioCanvasAction {
    data class Select(val objectId: String?) : StudioCanvasAction
    data class Move(
        val objectId: String,
        val deltaXPx: Float,
        val deltaYPx: Float,
    ) : StudioCanvasAction
    data class Resize(
        val objectId: String,
        val widthPx: Float,
        val heightPx: Float,
    ) : StudioCanvasAction
    data class Rotate(
        val objectId: String,
        val rotationDegrees: Float,
    ) : StudioCanvasAction
    data class SetOpacity(
        val objectId: String,
        val opacity: Float,
    ) : StudioCanvasAction
    data class SetVisible(
        val objectId: String,
        val visible: Boolean,
    ) : StudioCanvasAction
    data class SetLocked(
        val objectId: String,
        val locked: Boolean,
    ) : StudioCanvasAction
    data class BringForward(val objectId: String) : StudioCanvasAction
    data class SendBackward(val objectId: String) : StudioCanvasAction
    data class Delete(val objectId: String) : StudioCanvasAction
}

fun reduceStudioCanvasState(
    state: StudioCanvasState,
    action: StudioCanvasAction,
): StudioCanvasState = when (action) {
    is StudioCanvasAction.Select -> state.copy(
        selectedObjectId = action.objectId?.takeIf { candidate ->
            state.objects.any { it.id == candidate }
        },
    )

    is StudioCanvasAction.Move -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(
            xPx = canvasObject.xPx + action.deltaXPx,
            yPx = canvasObject.yPx + action.deltaYPx,
        )
    }

    is StudioCanvasAction.Resize -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(
            widthPx = action.widthPx.coerceAtLeast(1f),
            heightPx = action.heightPx.coerceAtLeast(1f),
        )
    }

    is StudioCanvasAction.Rotate -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(rotationDegrees = action.rotationDegrees)
    }

    is StudioCanvasAction.SetOpacity -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(opacity = action.opacity.coerceIn(0f, 1f))
    }

    is StudioCanvasAction.SetVisible -> state.copy(
        objects = state.objects.map { canvasObject ->
            if (canvasObject.id == action.objectId) canvasObject.copy(visible = action.visible) else canvasObject
        },
    )

    is StudioCanvasAction.SetLocked -> state.copy(
        objects = state.objects.map { canvasObject ->
            if (canvasObject.id == action.objectId) canvasObject.copy(locked = action.locked) else canvasObject
        },
    )

    is StudioCanvasAction.BringForward -> state.reorderOneStep(action.objectId, +1)
    is StudioCanvasAction.SendBackward -> state.reorderOneStep(action.objectId, -1)

    is StudioCanvasAction.Delete -> {
        val target = state.objects.firstOrNull { it.id == action.objectId }
        if (target == null || target.locked) {
            state
        } else {
            state.copy(
                objects = state.objects.filterNot { it.id == action.objectId },
                selectedObjectId = state.selectedObjectId.takeUnless { it == action.objectId },
            )
        }
    }
}

private inline fun StudioCanvasState.updateUnlockedObject(
    objectId: String,
    update: (StudioCanvasObject) -> StudioCanvasObject,
): StudioCanvasState = copy(
    objects = objects.map { canvasObject ->
        if (canvasObject.id != objectId || canvasObject.locked) canvasObject else update(canvasObject)
    },
)

private fun StudioCanvasState.reorderOneStep(
    objectId: String,
    delta: Int,
): StudioCanvasState {
    val fromIndex = objects.indexOfFirst { it.id == objectId }
    if (fromIndex < 0) return this
    val targetIndex = (fromIndex + delta).coerceIn(0, objects.lastIndex)
    if (targetIndex == fromIndex) return this

    val reordered = objects.toMutableList()
    val item = reordered.removeAt(fromIndex)
    reordered.add(targetIndex, item)
    return copy(objects = reordered)
}
