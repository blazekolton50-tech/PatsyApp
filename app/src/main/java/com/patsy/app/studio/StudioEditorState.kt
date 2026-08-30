package com.patsy.app.studio

import kotlin.math.roundToInt

enum class StudioMode {
    IMAGE,
    VIDEO,
    DOCUMENT,
    MEME,
    COLLAGE,
    CAMERA,
}

enum class StudioTool {
    SELECT,
    TEMPLATES,
    TEXT,
    MEDIA,
    ELEMENTS,
    AI,
    DRAW,
    CROP,
    CUTOUT,
    LAYERS,
    FRAMES,
    GUIDES,
    FILTERS,
    ADJUST,
    EFFECTS,
    ANIMATE,
    AUDIO,
    EXPORT,
}

enum class StudioLayerType {
    IMAGE,
    VIDEO,
    AUDIO,
    TEXT,
    SHAPE,
    STICKER,
    FRAME,
    GROUP,
    DRAWING,
    BACKGROUND,
}

data class StudioEditorState(
    val mode: StudioMode,
    val projectName: String,
    val canvasWidthPx: Int,
    val canvasHeightPx: Int,
    val durationMs: Int,
    val playheadMs: Int = 0,
    val isPlaying: Boolean = false,
    val isLooping: Boolean = false,
    val isMuted: Boolean = false,
    val playbackSpeed: Float = 1f,
    val selectedTool: StudioTool = StudioTool.SELECT,
    val zoom: Float = 1f,
    val undoDepth: Int = 0,
    val redoDepth: Int = 0,
) {
    companion object {
        fun image(
            widthPx: Int,
            heightPx: Int,
            projectName: String = "Untitled image",
        ) = StudioEditorState(
            mode = StudioMode.IMAGE,
            projectName = projectName,
            canvasWidthPx = widthPx.coerceAtLeast(1),
            canvasHeightPx = heightPx.coerceAtLeast(1),
            durationMs = 0,
        )

        fun video(
            durationMs: Int,
            playheadMs: Int = 0,
            isPlaying: Boolean = false,
            widthPx: Int = 1080,
            heightPx: Int = 1920,
            projectName: String = "Untitled video",
        ): StudioEditorState {
            val safeDuration = durationMs.coerceAtLeast(0)
            return StudioEditorState(
                mode = StudioMode.VIDEO,
                projectName = projectName,
                canvasWidthPx = widthPx.coerceAtLeast(1),
                canvasHeightPx = heightPx.coerceAtLeast(1),
                durationMs = safeDuration,
                playheadMs = playheadMs.coerceIn(0, safeDuration),
                isPlaying = isPlaying && safeDuration > 0,
            )
        }
    }
}

sealed interface StudioAction {
    data object TogglePlayPause : StudioAction
    data object ToggleLoop : StudioAction
    data object ToggleMute : StudioAction
    data class SetPlaying(val playing: Boolean) : StudioAction
    data class SeekTo(val timeMs: Int) : StudioAction
    data class StepBy(val deltaMs: Int) : StudioAction
    data class SetDuration(val durationMs: Int) : StudioAction
    data class SetPlaybackSpeed(val speed: Float) : StudioAction
    data class SelectTool(val tool: StudioTool) : StudioAction
}

private val supportedPlaybackSpeeds = setOf(0.25f, 0.5f, 1f, 1.5f, 2f)

fun reduceStudioState(
    state: StudioEditorState,
    action: StudioAction,
): StudioEditorState = when (action) {
    StudioAction.TogglePlayPause -> {
        if (state.durationMs <= 0) state.copy(isPlaying = false)
        else state.copy(isPlaying = !state.isPlaying)
    }

    StudioAction.ToggleLoop -> state.copy(isLooping = !state.isLooping)
    StudioAction.ToggleMute -> state.copy(isMuted = !state.isMuted)
    is StudioAction.SetPlaying -> state.copy(
        isPlaying = action.playing && state.durationMs > 0,
    )
    is StudioAction.SeekTo -> state.copy(
        playheadMs = action.timeMs.coerceIn(0, state.durationMs.coerceAtLeast(0)),
    )

    is StudioAction.StepBy -> state.copy(
        playheadMs = (state.playheadMs + action.deltaMs)
            .coerceIn(0, state.durationMs.coerceAtLeast(0)),
    )

    is StudioAction.SetDuration -> {
        val safeDuration = action.durationMs.coerceAtLeast(0)
        state.copy(
            durationMs = safeDuration,
            playheadMs = state.playheadMs.coerceIn(0, safeDuration),
            isPlaying = state.isPlaying && safeDuration > 0,
        )
    }

    is StudioAction.SetPlaybackSpeed -> {
        if (action.speed in supportedPlaybackSpeeds) state.copy(playbackSpeed = action.speed)
        else state
    }

    is StudioAction.SelectTool -> state.copy(selectedTool = action.tool)
}

fun timelineFraction(
    playheadMs: Int,
    durationMs: Int,
): Float {
    if (durationMs <= 0) return 0f
    return (playheadMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
}

fun timeFromTimelineFraction(
    fraction: Float,
    durationMs: Int,
): Int {
    if (durationMs <= 0) return 0
    return (durationMs * fraction.coerceIn(0f, 1f)).roundToInt()
}

data class StudioTimelineLayer(
    val id: String,
    val type: StudioLayerType,
    val startMs: Int,
    val endMs: Int,
    val name: String = type.name.lowercase().replaceFirstChar { it.uppercase() },
    val locked: Boolean = false,
    val hidden: Boolean = false,
) {
    fun normalized(projectDurationMs: Int): StudioTimelineLayer {
        val duration = projectDurationMs.coerceAtLeast(0)
        val safeStart = startMs.coerceIn(0, duration)
        val safeEnd = endMs.coerceIn(safeStart, duration)
        return copy(startMs = safeStart, endMs = safeEnd)
    }
}
