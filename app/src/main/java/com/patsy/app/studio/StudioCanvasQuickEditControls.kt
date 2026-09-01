package com.patsy.app.studio

enum class StudioCanvasQuickEditGroup {
    POSITION,
    OPACITY,
}

data class StudioCanvasQuickEditControl(
    val label: String,
    val action: StudioCanvasAction,
)

fun studioCanvasQuickEditControls(
    selected: StudioCanvasObject?,
    group: StudioCanvasQuickEditGroup,
): List<StudioCanvasQuickEditControl> {
    if (selected == null || selected.locked) return emptyList()

    return when (group) {
        StudioCanvasQuickEditGroup.POSITION -> listOf(
            StudioCanvasQuickEditControl("X -10", StudioCanvasAction.Move(selected.id, -10f, 0f)),
            StudioCanvasQuickEditControl("X +10", StudioCanvasAction.Move(selected.id, 10f, 0f)),
            StudioCanvasQuickEditControl("Y -10", StudioCanvasAction.Move(selected.id, 0f, -10f)),
            StudioCanvasQuickEditControl("Y +10", StudioCanvasAction.Move(selected.id, 0f, 10f)),
            StudioCanvasQuickEditControl("ROT -15", StudioCanvasAction.Rotate(selected.id, selected.rotationDegrees - 15f)),
            StudioCanvasQuickEditControl("ROT +15", StudioCanvasAction.Rotate(selected.id, selected.rotationDegrees + 15f)),
        )

        StudioCanvasQuickEditGroup.OPACITY -> listOf(
            StudioCanvasQuickEditControl("-10%", StudioCanvasAction.SetOpacity(selected.id, selected.opacity - 0.1f)),
            StudioCanvasQuickEditControl("+10%", StudioCanvasAction.SetOpacity(selected.id, selected.opacity + 0.1f)),
        )
    }
}
