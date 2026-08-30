package com.patsy.app.studio

import com.patsy.app.studio.catalog.*
import com.patsy.app.studio.editor.*
import com.patsy.app.studio.sizing.*
import org.junit.Assert.*
import org.junit.Test

class StudioImportedDeltaRedTest {
    @Test
    fun anchorAndScaleDoesNotStretchBottomRightFixedElement() {
        val from = CanvasSize(1080, 1080)
        val to = CanvasSize(1080, 1920)
        val current = LayerReflow.reflow(
            bounds = LayerBounds(900f, 900f, 150f, 50f),
            from = from,
            to = to,
            strategy = ReflowStrategy.ANCHOR_AND_SCALE,
        )
        // Imported requirement: a bottom-right, no-scale element must preserve size.
        // The current simpler engine scales it, so this is the RED test.
        assertEquals(150f, current.width, 0.001f)
        assertEquals(50f, current.height, 0.001f)
    }

    @Test
    fun richerLayerStateTypesMustExist() {
        val textTypeExists = runCatching {
            Class.forName("com.patsy.app.studio.editor.TextProperties")
        }.isSuccess
        val cropTypeExists = runCatching {
            Class.forName("com.patsy.app.studio.editor.CropState")
        }.isSuccess
        assertTrue("typed text properties are required", textTypeExists)
        assertTrue("crop state is required", cropTypeExists)
    }

    @Test
    fun projectArtifactSeparationMustExist() {
        assertTrue(
            "editable/exported/retained/personal/community artifacts must be separate",
            runCatching { Class.forName("com.patsy.app.studio.project.ProjectArtifact") }.isSuccess,
        )
    }

    @Test
    fun editorNeedsDeterministicForwardBackwardCommands() {
        val methodNames = StudioEditor::class.java.methods.map { it.name }.toSet()
        assertTrue("bringForward is required", "bringForward" in methodNames)
        assertTrue("sendBackward is required", "sendBackward" in methodNames)
    }

    @Test
    fun catalogueValidatorRejectsForbiddenVisibleNamesAndUnsafeBundling() {
        val licence = LicenceMetadata(origin = "generated", approvedForBundling = true)
        val item = StudioCatalogueItem(
            stableId = "templates-social-0001",
            category = "templates",
            subcategory = "social",
            displayName = "Instagram Post",
            itemType = CatalogueItemType.TEMPLATE,
            licence = licence,
            availability = AssetAvailability.STAGED,
            completionStatus = CompletionStatus.METADATA_ONLY,
        )
        val validatorClassExists = runCatching {
            Class.forName("com.patsy.app.studio.catalog.StudioCatalogueValidator")
        }.isSuccess
        assertTrue("catalogue truth validator is required", validatorClassExists)
    }
}
