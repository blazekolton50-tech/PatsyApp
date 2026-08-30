package com.patsy.app.studio.timeline

enum class TimelineTrackType { VIDEO, AUDIO, TEXT, STICKER, PAWMOJI, EFFECT, FILTER, TRANSITION, OVERLAY }

data class TimelineClip(
    val id: String,
    val sourceReference: String? = null,
    val timelineStartMs: Long,
    val sourceTrimStartMs: Long = 0,
    val sourceTrimEndMs: Long,
    val volume: Float = 1f,
    val fadeInMs: Long = 0,
    val fadeOutMs: Long = 0,
    val attachedEffectIds: List<String> = emptyList(),
) {
    init {
        require(timelineStartMs >= 0) { "timelineStartMs must be non-negative" }
        require(sourceTrimStartMs >= 0) { "sourceTrimStartMs must be non-negative" }
        require(sourceTrimEndMs > sourceTrimStartMs) { "trim end must be after trim start" }
        require(volume in 0f..1f) { "volume must be between 0 and 1" }
        require(fadeInMs >= 0 && fadeOutMs >= 0) { "fades must be non-negative" }
    }

    val durationMs: Long get() = sourceTrimEndMs - sourceTrimStartMs
}

data class TimelineTrack(
    val id: String,
    val type: TimelineTrackType,
    val order: Int,
    val clips: List<TimelineClip> = emptyList(),
)

data class StudioTimeline(
    val durationMs: Long,
    val playheadMs: Long = 0,
    val tracks: List<TimelineTrack> = emptyList(),
) {
    init {
        require(durationMs > 0) { "durationMs must be positive" }
        require(playheadMs in 0..durationMs) { "playhead outside timeline" }
    }
}

object TimelineEditor {
    fun addClip(timeline: StudioTimeline, trackId: String, clip: TimelineClip): StudioTimeline =
        updateTrack(timeline, trackId) { track ->
            require(track.clips.none { it.id == clip.id }) { "duplicate clip id" }
            require(clip.timelineStartMs + clip.durationMs <= timeline.durationMs) { "clip exceeds timeline" }
            track.copy(clips = (track.clips + clip).sortedBy(TimelineClip::timelineStartMs))
        }

    fun deleteClip(timeline: StudioTimeline, trackId: String, clipId: String): StudioTimeline =
        updateTrack(timeline, trackId) { track ->
            require(track.clips.any { it.id == clipId }) { "unknown clip" }
            track.copy(clips = track.clips.filterNot { it.id == clipId })
        }

    fun moveClip(timeline: StudioTimeline, trackId: String, clipId: String, newStartMs: Long): StudioTimeline =
        updateClip(timeline, trackId, clipId) { clip ->
            require(newStartMs >= 0 && newStartMs + clip.durationMs <= timeline.durationMs) { "clip exceeds timeline" }
            clip.copy(timelineStartMs = newStartMs)
        }

    fun trim(timeline: StudioTimeline, trackId: String, clipId: String, sourceStartMs: Long, sourceEndMs: Long): StudioTimeline =
        updateClip(timeline, trackId, clipId) { clip ->
            val trimmed = clip.copy(sourceTrimStartMs = sourceStartMs, sourceTrimEndMs = sourceEndMs)
            require(trimmed.timelineStartMs + trimmed.durationMs <= timeline.durationMs) { "clip exceeds timeline" }
            trimmed
        }

    fun setVolume(timeline: StudioTimeline, trackId: String, clipId: String, volume: Float): StudioTimeline =
        updateClip(timeline, trackId, clipId) { it.copy(volume = volume) }

    fun attachEffect(timeline: StudioTimeline, trackId: String, clipId: String, effectId: String): StudioTimeline =
        updateClip(timeline, trackId, clipId) { it.copy(attachedEffectIds = (it.attachedEffectIds + effectId).distinct()) }

    fun removeEffect(timeline: StudioTimeline, trackId: String, clipId: String, effectId: String): StudioTimeline =
        updateClip(timeline, trackId, clipId) { it.copy(attachedEffectIds = it.attachedEffectIds - effectId) }

    fun reorderTrack(timeline: StudioTimeline, trackId: String, newOrder: Int): StudioTimeline {
        val updated = updateTrack(timeline, trackId) { it.copy(order = newOrder) }
        return updated.copy(tracks = updated.tracks.sortedBy(TimelineTrack::order))
    }

    private fun updateClip(
        timeline: StudioTimeline,
        trackId: String,
        clipId: String,
        transform: (TimelineClip) -> TimelineClip,
    ): StudioTimeline = updateTrack(timeline, trackId) { track ->
        require(track.clips.any { it.id == clipId }) { "unknown clip" }
        track.copy(clips = track.clips.map { if (it.id == clipId) transform(it) else it }.sortedBy(TimelineClip::timelineStartMs))
    }

    private fun updateTrack(
        timeline: StudioTimeline,
        trackId: String,
        transform: (TimelineTrack) -> TimelineTrack,
    ): StudioTimeline {
        require(timeline.tracks.any { it.id == trackId }) { "unknown track" }
        return timeline.copy(tracks = timeline.tracks.map { if (it.id == trackId) transform(it) else it })
    }
}
