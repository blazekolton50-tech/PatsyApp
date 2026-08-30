package com.patsy.app.studio

import com.patsy.app.studio.camera.CameraCapabilities
import com.patsy.app.studio.camera.CameraCapabilityState
import com.patsy.app.studio.export.StudioExportResult
import com.patsy.app.studio.export.StudioExportState
import com.patsy.app.studio.timeline.StudioTimeline
import com.patsy.app.studio.timeline.TimelineClip
import com.patsy.app.studio.timeline.TimelineEditor
import com.patsy.app.studio.timeline.TimelineTrack
import com.patsy.app.studio.timeline.TimelineTrackType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StudioTimelineAndTruthTest {
    @Test
    fun timelineSupportsAddMoveTrimAndVolume() {
        val base = StudioTimeline(
            durationMs = 10_000,
            tracks = listOf(TimelineTrack("video", TimelineTrackType.VIDEO, 0)),
        )
        val clip = TimelineClip("clip-1", timelineStartMs = 0, sourceTrimEndMs = 4_000)
        val added = TimelineEditor.addClip(base, "video", clip)
        val moved = TimelineEditor.moveClip(added, "video", "clip-1", 2_000)
        val trimmed = TimelineEditor.trim(moved, "video", "clip-1", 500, 2_500)
        val volume = TimelineEditor.setVolume(trimmed, "video", "clip-1", 0.5f)

        val result = volume.tracks.single().clips.single()
        assertEquals(2_000, result.timelineStartMs)
        assertEquals(2_000, result.durationMs)
        assertEquals(0.5f, result.volume)
    }

    @Test
    fun invalidTrimRangeFailsClosed() {
        assertThrows(IllegalArgumentException::class.java) {
            TimelineClip("bad", timelineStartMs = 0, sourceTrimStartMs = 1000, sourceTrimEndMs = 500)
        }
    }

    @Test
    fun completeExportCannotExistWithoutRealOutputMetadata() {
        assertThrows(IllegalArgumentException::class.java) {
            StudioExportResult(StudioExportState.COMPLETE)
        }
    }

    @Test
    fun unavailableCameraCannotAdvertiseCapture() {
        assertThrows(IllegalArgumentException::class.java) {
            CameraCapabilities(
                state = CameraCapabilityState.NOT_CONFIGURED,
                photoCapture = true,
            )
        }
    }
}
