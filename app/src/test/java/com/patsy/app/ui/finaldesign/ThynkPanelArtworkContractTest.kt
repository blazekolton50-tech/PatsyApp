package com.patsy.app.ui.finaldesign

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThynkPanelArtworkContractTest {
    @Test
    fun `official five-logo THyNK panel artwork is packaged as a real raster asset`() {
        val asset = sequenceOf(
            File("src/main/res/drawable-nodpi/thynk_panel_official.jpg"),
            File("app/src/main/res/drawable-nodpi/thynk_panel_official.jpg"),
        ).firstOrNull(File::isFile)
            ?: error("Missing official five-logo THyNK panel artwork")

        assertTrue(asset.length() > 10_000L, "Panel artwork must be the supplied raster, not a placeholder")

        val bytes = asset.readBytes()
        assertTrue(bytes.size > 10_000, "Panel artwork must contain the real raster bytes")
        assertEquals(0xFF, bytes.first().toInt() and 0xFF, "Panel artwork must start with JPEG SOI")
        assertEquals(0xD8, bytes[1].toInt() and 0xFF, "Panel artwork must start with JPEG SOI")
        assertEquals(0xFF, bytes[bytes.lastIndex - 1].toInt() and 0xFF, "Panel artwork must end with JPEG EOI")
        assertEquals(0xD9, bytes.last().toInt() and 0xFF, "Panel artwork must end with JPEG EOI")
    }
}
