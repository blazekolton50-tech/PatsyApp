package com.patsy.app.ui.finaldesign

import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertNotNull
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
        val image = ImageIO.read(asset)
        assertNotNull(image, "Panel artwork must decode as an image")
        assertTrue(image.width >= 900, "Panel artwork must retain enough source detail for the five logos")
        assertTrue(image.width.toDouble() / image.height >= 4.5, "Panel artwork must retain the locked wide five-logo layout")
    }
}
