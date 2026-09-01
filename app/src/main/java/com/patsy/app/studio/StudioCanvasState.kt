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

    is StudioCanvasAction.Move -> state.copy(
        objects = state.objects.map { canvasObject ->
            if (canvasObject.id != action.objectId || canvasObject.locked) {
                canvasObject
            } else {
                canvasObject.copy(
                    xPx = canvasObject.xPx + action.deltaXPx,
                    yPx = canvasObject.yPx + action.deltaYPx,
                )
            }
        },
    )
}
