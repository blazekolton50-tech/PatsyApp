package com.patsy.app.studio

import com.patsy.app.studio.catalog.BuiltInCataloguePlan
import com.patsy.app.studio.effects.EffectImplementationStatus
import com.patsy.app.studio.effects.Stage2EffectCatalogue
import com.patsy.app.studio.sizing.CanvasSize
import com.patsy.app.studio.sizing.CustomSizeLimits
import com.patsy.app.studio.sizing.NeutralCanvasPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioCoreModelsTest {
    @Test
    fun neutralPresetsMatchLockedSizes() {
        assertEquals(1080, NeutralCanvasPresets.square.widthPx)
        assertEquals(1350, NeutralCanvasPresets.portrait.heightPx)
        assertEquals("9:16", NeutralCanvasPresets.fullVertical.aspectRatioLabel)
        assertEquals(1200, NeutralCanvasPresets.wide.widthPx)
        assertEquals(628, NeutralCanvasPresets.wide.heightPx)
    }

    @Test
    fun visiblePresetLabelsStayPlatformNeutral() {
        val forbidden = listOf("instagram", "tiktok", "reel", "story", "facebook", "youtube")
        assertTrue(NeutralCanvasPresets.all.none { preset ->
            forbidden.any { preset.displayName.lowercase().contains(it) }
        })
    }

    @Test
    fun customSizeLimitsRejectOutOfRangeValues() {
        val limits = CustomSizeLimits()
        assertTrue(limits.validate(CanvasSize(1080, 1920)).isEmpty())
        assertEquals(listOf("width_out_of_range"), limits.validate(CanvasSize(100, 1000)))
    }

    @Test
    fun cataloguePlanPreserves1110StructureWithoutClaimingCompletion() {
        assertEquals(1110, BuiltInCataloguePlan.plannedCount)
    }

    @Test
    fun importedEffectNamesRemainCatalogueOnlyUntilNativeRendererExists() {
        assertEquals(25, Stage2EffectCatalogue.filters.size)
        assertEquals(25, Stage2EffectCatalogue.effects.size)
        assertTrue(Stage2EffectCatalogue.filters.all {
            it.implementationStatus == EffectImplementationStatus.CATALOGUE_ONLY
        })
    }
}
