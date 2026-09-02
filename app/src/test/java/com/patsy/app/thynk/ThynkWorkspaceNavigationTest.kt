package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkWorkspaceNavigationTest {
    @Test
    fun `THyNK IT and Music panel entries open distinct roots`() {
        assertEquals(ThynkWorkspaceRoute.Hub, ThynkWorkspaceNavigation.initialRoute(ThynkStudioEntry.IT))
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.initialRoute(ThynkStudioEntry.MUSIC),
        )
    }

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
    fun `editing board chrome is back plus overflow home with global homebar hidden`() {
        val chrome = ThynkWorkspaceNavigation.chrome(
            ThynkWorkspaceRoute.Music("mixer"),
        )

        assertTrue(chrome.showBack)
        assertTrue(chrome.showOverflowHome)
        assertFalse(chrome.showGlobalHomebar)
    }

    @Test
    fun `category chrome keeps back and global homebar without board overflow home`() {
        val chrome = ThynkWorkspaceNavigation.chrome(
            ThynkWorkspaceRoute.Category("fashion"),
        )

        assertTrue(chrome.showBack)
        assertFalse(chrome.showOverflowHome)
        assertTrue(chrome.showGlobalHomebar)
    }

    @Test
    fun `THyNK IT and Music roots have no back and keep global homebar`() {
        listOf(
            ThynkWorkspaceRoute.Hub,
            ThynkWorkspaceRoute.Music("music-home"),
        ).forEach { route ->
            val chrome = ThynkWorkspaceNavigation.chrome(route)
            assertFalse(chrome.showBack)
            assertFalse(chrome.showOverflowHome)
            assertTrue(chrome.showGlobalHomebar)
        }
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
    fun `back from music setup returns to music home while music home remains root`() {
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("create-music")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("ai-music-generator")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("music-home")),
        )
    }
}
