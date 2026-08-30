package com.patsy.app.studio

import com.patsy.app.studio.document.CvTemplatePack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentTemplatePackRedTest {
    @Test
    fun cvPackHasExactlyTenUniqueEditableTemplates() {
        assertTrue(CvTemplatePack.editable)
        assertEquals(10, CvTemplatePack.expectedTemplateCount)
        assertEquals(10, CvTemplatePack.templateIds.size)
        assertEquals(10, CvTemplatePack.templateIds.distinct().size)
        assertTrue(CvTemplatePack.templateIds.none { it.isBlank() })
    }

    @Test
    fun cvPackKeepsLockedTemplateIds() {
        assertEquals(
            listOf(
                "minimal",
                "corporate",
                "creative",
                "technical",
                "timeline",
                "swiss",
                "elegant",
                "bold",
                "pop_playful",
                "gradient_energy",
            ),
            CvTemplatePack.templateIds,
        )
    }

    @Test
    fun cvTemplateBlocksExposeRequiredEditableStructure() {
        assertEquals(
            listOf("header", "about", "experience", "education", "skills", "contact"),
            CvTemplatePack.blocks.map { it.stableId },
        )
        assertTrue(CvTemplatePack.blocks.all { it.editable })
    }

    @Test
    fun cvPlaceholdersAreCompleteAndTemplateNameIsMetadataOnly() {
        val placeholders = CvTemplatePack.blocks.flatMap { it.placeholders }.toSet()
        assertEquals(
            setOf("fullName", "jobTitle", "email", "about", "experience", "education", "skills", "contact"),
            placeholders,
        )
        assertFalse("templateName" in placeholders)
        assertTrue(CvTemplatePack.displayName.isNotBlank())
    }

    @Test
    fun cvPackPaletteMatchesApprovedFamilyPalette() {
        assertEquals("#FEF7ED", CvTemplatePack.palette.cream)
        assertEquals("#492F7F", CvTemplatePack.palette.logoPurple)
        assertEquals("#330E61", CvTemplatePack.palette.titlePurple)
        assertEquals("#C5D1BD", CvTemplatePack.palette.sage)
    }
}
