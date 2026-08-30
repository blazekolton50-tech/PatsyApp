package com.patsy.app.studio.catalog

import com.patsy.app.studio.sizing.ReflowStrategy

enum class CatalogueItemType {
    TEMPLATE,
    STICKER,
    ICON,
    ILLUSTRATION,
    PATTERN,
    IMAGE,
    LOGO_ASSET,
    FONT,
    EFFECT,
    FILTER,
    TRANSITION,
    TEXT_ANIMATION,
    OVERLAY,
    MOCKUP,
}

enum class AssetAvailability { BUNDLED, STAGED, REMOTE_AVAILABLE, UNAVAILABLE }
enum class CompletionStatus { COMPLETED, METADATA_ONLY, PREVIEW_ONLY, STAGED, REMOTE_AVAILABLE, UNAVAILABLE }

data class LicenceMetadata(
    val origin: String,
    val licence: String? = null,
    val evidenceReference: String? = null,
    val approvedForBundling: Boolean = false,
)

data class StudioCatalogueItem(
    val stableId: String,
    val category: String,
    val subcategory: String,
    val displayName: String,
    val itemType: CatalogueItemType,
    val previewReference: String? = null,
    val sourceReference: String? = null,
    val tags: Set<String> = emptySet(),
    val supportedSizeIds: Set<String> = emptySet(),
    val supportedAspectRatios: Set<String> = emptySet(),
    val allowCustomSize: Boolean = true,
    val reflowStrategy: ReflowStrategy = ReflowStrategy.RESPONSIVE_LAYOUT,
    val editableCapabilities: Set<String> = emptySet(),
    val licence: LicenceMetadata,
    val availability: AssetAvailability,
    val completionStatus: CompletionStatus,
)

data class CatalogueFamilySpec(
    val category: String,
    val subcategory: String,
    val itemType: CatalogueItemType,
    val plannedCount: Int,
)

object BuiltInCataloguePlan {
    val families = listOf(
        CatalogueFamilySpec("templates", "cvs", CatalogueItemType.TEMPLATE, 10),
        CatalogueFamilySpec("templates", "posters", CatalogueItemType.TEMPLATE, 25),
        CatalogueFamilySpec("templates", "social", CatalogueItemType.TEMPLATE, 50),
        CatalogueFamilySpec("templates", "business_cards", CatalogueItemType.TEMPLATE, 25),
        CatalogueFamilySpec("templates", "slides", CatalogueItemType.TEMPLATE, 50),
        CatalogueFamilySpec("templates", "schedules", CatalogueItemType.TEMPLATE, 25),
        CatalogueFamilySpec("templates", "todos", CatalogueItemType.TEMPLATE, 25),
        CatalogueFamilySpec("elements", "stickers", CatalogueItemType.STICKER, 100),
        CatalogueFamilySpec("elements", "icons", CatalogueItemType.ICON, 100),
        CatalogueFamilySpec("elements", "illustrations", CatalogueItemType.ILLUSTRATION, 100),
        CatalogueFamilySpec("elements", "patterns", CatalogueItemType.PATTERN, 50),
        CatalogueFamilySpec("media", "images", CatalogueItemType.IMAGE, 300),
        CatalogueFamilySpec("media", "logos", CatalogueItemType.LOGO_ASSET, 50),
        CatalogueFamilySpec("style", "fonts", CatalogueItemType.FONT, 50),
        CatalogueFamilySpec("style", "effects", CatalogueItemType.EFFECT, 25),
        CatalogueFamilySpec("style", "filters", CatalogueItemType.FILTER, 25),
        CatalogueFamilySpec("style", "transitions", CatalogueItemType.TRANSITION, 25),
        CatalogueFamilySpec("style", "text_animations", CatalogueItemType.TEXT_ANIMATION, 25),
        CatalogueFamilySpec("style", "overlays", CatalogueItemType.OVERLAY, 25),
        CatalogueFamilySpec("mockups", "mockups", CatalogueItemType.MOCKUP, 25),
    )

    val plannedCount: Int = families.sumOf { it.plannedCount }
}
