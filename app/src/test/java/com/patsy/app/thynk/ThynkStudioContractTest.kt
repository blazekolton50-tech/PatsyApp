package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThynkStudioContractTest {
    @Test
    fun `THyNK exposes the locked ten category hub`() {
        assertEquals(
            listOf(
                "DESIGN & TEMPLATES",
                "SOCIAL & CONTENT",
                "PHOTO & IMAGE",
                "VIDEO & CAMERA",
                "DOCUMENTS & BUSINESS",
                "HOMEWORK & STUDY",
                "PRESENTATIONS & PLANNING",
                "COLLAGE & CREATIVE",
                "THyNK MUSIC",
                "AI & MY STUDIO",
            ),
            ThynkStudioCatalog.categories.map { it.label },
        )
    }

    @Test
    fun `THyNK Music exposes the locked creator workflow`() {
        val pages = ThynkMusicCatalog.pages.map { it.id }
        assertEquals(
            listOf(
                "music-home",
                "create-music",
                "ai-music-generator",
                "track-editor",
                "mixer",
                "equalizer",
                "effects",
                "lyrics-vocals",
                "mastering",
                "export",
            ),
            pages,
        )
        assertTrue(ThynkMusicCatalog.pages.all { it.providerState != "FAKE_COMPLETE" })
    }

    @Test
    fun `video editor items map to the shared video editor route`() {
        assertEquals("video-editor", editorPageForThynkItem("VIDEO EDITOR"))
        assertEquals("video-editor", editorPageForThynkItem("TRIM & CUT"))
        assertEquals(null, editorPageForThynkItem("POSTERS"))
    }
}
