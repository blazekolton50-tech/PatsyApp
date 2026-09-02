package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkTopLogoContractTest {
    @Test
    fun `THyNK IN home uses its official logo instead of Patsy wordmark`() {
        val home = source("app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt")

        assertTrue(home.contains("R.drawable.thynk_in_logo_official"))
        assertFalse(
            home.substringAfter("private fun FinalHomeTopBar()").substringBefore("private fun ContinueDesignsSection")
                .contains("R.drawable.patsy_logo_official_white"),
        )
        assertRaster("app/src/main/res/drawable-nodpi/thynk_in_logo_official.png")
    }

    @Test
    fun `THyNK studio header selects official Music and IT logos`() {
        val studio = source("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

        assertTrue(studio.contains("R.drawable.thynk_music_logo_official"))
        assertTrue(studio.contains("R.drawable.thynk_it_logo_official"))
        assertFalse(
            studio.substringAfter("private fun ThynkHeader(").substringBefore("private fun ThynkHub")
                .contains("R.drawable.patsy_logo_official_white"),
        )
        assertRaster("app/src/main/res/drawable-nodpi/thynk_music_logo_official.png")
        assertRaster("app/src/main/res/drawable-nodpi/thynk_it_logo_official.png")
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }

    private fun assertRaster(path: String) {
        val candidates = sequenceOf(File(path), File("../$path"))
        val file = candidates.firstOrNull(File::isFile) ?: error("Missing locked raster: $path")
        assertTrue(file.length() > 10_000L, "$path must contain a real raster asset")
    }
}
