package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkWorkspaceNavigationTest {
    @Test
    fun `design and video editor routes are full screen editing boards`() {
        assertTrue(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Editor(pageId = "design-editor", categoryId = "design"),
            ),
        )
        assertTrue(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Editor(pageId = "video-editor", categoryId = "video"),
            ),
        )
    }

    @Test
    fun `specialist music workspaces hide the global homebar`() {
        listOf(
            "track-editor",
            "mixer",
            "equalizer",
            "effects",
            "lyrics-vocals",
            "mastering",
            "export",
        ).forEach { pageId ->
            assertTrue(
                ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Music(pageId)),
                "$pageId must be treated as an editing board",
            )
        }
    }

    @Test
    fun `hubs category screens and music setup pages retain the global homebar`() {
        assertFalse(ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Hub))
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Category("design"),
            ),
        )
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Music("music-home"),
            ),
        )
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Music("create-music"),
            ),
        )
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Music("ai-music-generator"),
            ),
        )
    }

    @Test
    fun `back from non music editor returns to exact originating category`() {
        assertEquals(
            ThynkWorkspaceRoute.Category("fashion"),
            ThynkWorkspaceNavigation.back(
                ThynkWorkspaceRoute.Editor(pageId = "design-editor", categoryId = "fashion"),
            ),
        )
    }

    @Test
    fun `back from specialist music board returns to music home`() {
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("mixer")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("mastering")),
        )
    }

    @Test
    fun `back continues outward from music home and category screens`() {
        assertEquals(
            ThynkWorkspaceRoute.Category("music"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("music-home")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Hub,
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Category("music")),
        )
    }
}
