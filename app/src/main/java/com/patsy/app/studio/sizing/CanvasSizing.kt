package com.patsy.app.studio.sizing

enum class CanvasShapeKind { SQUARE, PORTRAIT, FULL_VERTICAL, LANDSCAPE, WIDE, CUSTOM }

enum class ReflowStrategy {
    SCALE_UNIFORM,
    ANCHOR_AND_SCALE,
    RESPONSIVE_LAYOUT,
    CROP_TO_FILL,
    FIT_WITH_PADDING,
}

data class CanvasSizePreset(
    val id: String,
    val displayName: String,
    val widthPx: Int,
    val heightPx: Int,
    val aspectRatioLabel: String,
    val shapeKind: CanvasShapeKind,
    val isCustom: Boolean = false,
)

data class CanvasSize(
    val widthPx: Int,
    val heightPx: Int,
) {
    init {
        require(widthPx > 0) { "widthPx must be positive" }
        require(heightPx > 0) { "heightPx must be positive" }
    }
}

data class CustomSizeLimits(
    val minWidthPx: Int = 200,
    val maxWidthPx: Int = 4000,
    val minHeightPx: Int = 200,
    val maxHeightPx: Int = 4000,
) {
    fun validate(size: CanvasSize): List<String> = buildList {
        if (size.widthPx !in minWidthPx..maxWidthPx) add("width_out_of_range")
        if (size.heightPx !in minHeightPx..maxHeightPx) add("height_out_of_range")
    }
}

object NeutralCanvasPresets {
    val square = CanvasSizePreset("square_1_1", "Square", 1080, 1080, "1:1", CanvasShapeKind.SQUARE)
    val portrait = CanvasSizePreset("portrait_4_5", "Portrait", 1080, 1350, "4:5", CanvasShapeKind.PORTRAIT)
    val fullVertical = CanvasSizePreset("full_vertical_9_16", "Full Vertical", 1080, 1920, "9:16", CanvasShapeKind.FULL_VERTICAL)
    val landscape = CanvasSizePreset("landscape_16_9", "Landscape", 1920, 1080, "16:9", CanvasShapeKind.LANDSCAPE)
    val wide = CanvasSizePreset("wide_1_91_1", "Wide", 1200, 628, "1.91:1", CanvasShapeKind.WIDE)
    val custom = CanvasSizePreset("custom", "Custom Size", 1, 1, "Custom", CanvasShapeKind.CUSTOM, isCustom = true)

    val all: List<CanvasSizePreset> = listOf(square, portrait, fullVertical, landscape, wide, custom)
}

data class LayerBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

object LayerReflow {
    fun reflow(
        bounds: LayerBounds,
        from: CanvasSize,
        to: CanvasSize,
        strategy: ReflowStrategy,
    ): LayerBounds {
        val sx = to.widthPx.toFloat() / from.widthPx
        val sy = to.heightPx.toFloat() / from.heightPx
        return when (strategy) {
            ReflowStrategy.RESPONSIVE_LAYOUT -> LayerBounds(
                x = bounds.x * sx,
                y = bounds.y * sy,
                width = bounds.width * sx,
                height = bounds.height * sy,
            )
            ReflowStrategy.SCALE_UNIFORM,
            ReflowStrategy.FIT_WITH_PADDING -> uniform(bounds, from, to, minOf(sx, sy))
            ReflowStrategy.CROP_TO_FILL -> uniform(bounds, from, to, maxOf(sx, sy))
            ReflowStrategy.ANCHOR_AND_SCALE -> {
                val scale = minOf(sx, sy)
                val centreXRatio = (bounds.x + bounds.width / 2f) / from.widthPx
                val centreYRatio = (bounds.y + bounds.height / 2f) / from.heightPx
                val newWidth = bounds.width * scale
                val newHeight = bounds.height * scale
                LayerBounds(
                    x = centreXRatio * to.widthPx - newWidth / 2f,
                    y = centreYRatio * to.heightPx - newHeight / 2f,
                    width = newWidth,
                    height = newHeight,
                )
            }
        }
    }

    private fun uniform(bounds: LayerBounds, from: CanvasSize, to: CanvasSize, scale: Float): LayerBounds {
        val contentWidth = from.widthPx * scale
        val contentHeight = from.heightPx * scale
        val offsetX = (to.widthPx - contentWidth) / 2f
        val offsetY = (to.heightPx - contentHeight) / 2f
        return LayerBounds(
            x = bounds.x * scale + offsetX,
            y = bounds.y * scale + offsetY,
            width = bounds.width * scale,
            height = bounds.height * scale,
        )
    }
}
