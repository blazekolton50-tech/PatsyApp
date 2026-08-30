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

data class CatalogueViolation(
    val stableId: String,
    val code: CatalogueViolationCode,
    val safeMessage: String,
)

enum class CatalogueViolationCode {
    FORBIDDEN_VISIBLE_NAME,
    BUNDLED_WITHOUT_APPROVAL,
    BUNDLED_WITHOUT_SOURCE,
    COMPLETED_BUT_UNAVAILABLE,
}

/**
 * Truth/safety gate for catalogue data before it is treated as shippable content.
 *
 * The default forbidden terms cover third-party platform/editor branding that must not appear in
 * the neutral THyNK catalogue. Real integrations can use a separately configured validator only
 * after branding permission has been confirmed for that surface.
 */
class StudioCatalogueValidator(
    forbiddenVisibleTerms: Set<String> = DEFAULT_FORBIDDEN_VISIBLE_TERMS,
) {
    private val normalizedForbiddenTerms = forbiddenVisibleTerms.map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()

    fun validate(item: StudioCatalogueItem): List<CatalogueViolation> {
        val violations = mutableListOf<CatalogueViolation>()
        val visibleText = buildList {
            add(item.displayName)
            add(item.category)
            add(item.subcategory)
            addAll(item.tags)
        }.joinToString(" ").lowercase()

        if (normalizedForbiddenTerms.any { term -> term in visibleText }) {
            violations += CatalogueViolation(
                stableId = item.stableId,
                code = CatalogueViolationCode.FORBIDDEN_VISIBLE_NAME,
                safeMessage = "Catalogue item contains third-party visible branding",
            )
        }

        if (item.availability == AssetAvailability.BUNDLED && !item.licence.approvedForBundling) {
            violations += CatalogueViolation(
                stableId = item.stableId,
                code = CatalogueViolationCode.BUNDLED_WITHOUT_APPROVAL,
                safeMessage = "Bundled asset is not approved for bundling",
            )
        }

        if (item.availability == AssetAvailability.BUNDLED && item.sourceReference.isNullOrBlank()) {
            violations += CatalogueViolation(
                stableId = item.stableId,
                code = CatalogueViolationCode.BUNDLED_WITHOUT_SOURCE,
                safeMessage = "Bundled asset has no source reference",
            )
        }

        if (item.completionStatus == CompletionStatus.COMPLETED && item.availability == AssetAvailability.UNAVAILABLE) {
            violations += CatalogueViolation(
                stableId = item.stableId,
                code = CatalogueViolationCode.COMPLETED_BUT_UNAVAILABLE,
                safeMessage = "Unavailable asset cannot be marked completed",
            )
        }

        return violations
    }

    fun validate(items: Iterable<StudioCatalogueItem>): List<CatalogueViolation> = items.flatMap(::validate)

    companion object {
        val DEFAULT_FORBIDDEN_VISIBLE_TERMS: Set<String> = setOf(
            "instagram",
            "facebook",
            "tiktok",
            "youtube",
            "canva",
            "capcut",
            "photoshop",
            "premiere",
            "adobe",
        )
    }
}

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
