package com.patsy.app.studio.effects

enum class EffectKind { FILTER, EFFECT, TRANSITION, TEXT_ANIMATION, OVERLAY }
enum class EffectImplementationStatus { IMPLEMENTED_NATIVE, CATALOGUE_ONLY, PROVIDER_REQUIRED, UNAVAILABLE }
enum class SupportedMediaType { IMAGE, VIDEO, TEXT, CANVAS }

data class EffectParameter(
    val name: String,
    val value: Float,
)

data class StudioEffectDefinition(
    val stableId: String,
    val name: String,
    val kind: EffectKind,
    val category: String,
    val parameters: List<EffectParameter> = emptyList(),
    val supportedMediaTypes: Set<SupportedMediaType>,
    val implementationStatus: EffectImplementationStatus,
)

object Stage2EffectCatalogue {
    val filters: List<StudioEffectDefinition> = List(25) { index ->
        val n = index + 1
        val groups = listOf("Warm", "Cool", "Bright", "Soft", "Vivid")
        StudioEffectDefinition(
            stableId = "filter_${n.toString().padStart(2, '0')}",
            name = "${groups[index % groups.size]} $n",
            kind = EffectKind.FILTER,
            category = groups[index % groups.size],
            parameters = listOf(
                EffectParameter("brightness", 1f + (index % 5) * 0.05f),
                EffectParameter("contrast", 1f + (index % 3) * 0.05f),
            ),
            supportedMediaTypes = setOf(SupportedMediaType.IMAGE, SupportedMediaType.VIDEO),
            implementationStatus = EffectImplementationStatus.CATALOGUE_ONLY,
        )
    }

    val effects = generated(EffectKind.EFFECT, "effect", listOf("Tone", "Light", "Soft"))
    val transitions = generated(EffectKind.TRANSITION, "transition", listOf("Dissolve", "Move", "Scale", "Break"))
    val textAnimations = generated(EffectKind.TEXT_ANIMATION, "text_animation", listOf("Appear", "Move", "Pop", "Write"), setOf(SupportedMediaType.TEXT))
    val overlays = generated(EffectKind.OVERLAY, "overlay", listOf("Glow", "Speck", "Round"), setOf(SupportedMediaType.CANVAS, SupportedMediaType.IMAGE, SupportedMediaType.VIDEO))

    private fun generated(
        kind: EffectKind,
        idPrefix: String,
        groups: List<String>,
        media: Set<SupportedMediaType> = setOf(SupportedMediaType.IMAGE, SupportedMediaType.VIDEO),
    ) = List(25) { index ->
        val n = index + 1
        StudioEffectDefinition(
            stableId = "${idPrefix}_${n.toString().padStart(2, '0')}",
            name = "${groups[index % groups.size]} $n",
            kind = kind,
            category = groups[index % groups.size],
            supportedMediaTypes = media,
            implementationStatus = EffectImplementationStatus.CATALOGUE_ONLY,
        )
    }
}
