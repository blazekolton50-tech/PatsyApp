package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Exact HOME BAR rainbow separator lock.
 *
 * Straight on both outer sides. Only the centre section rises into a smooth arch
 * around/over the large Camera + control. Do not replace this with a full-width wave.
 */
@Composable
fun FinalHomebarRainbowLine(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(28.dp)) {
        val baseline = size.height * 0.78f
        val path = Path().apply {
            moveTo(0f, baseline)
            lineTo(size.width * 0.38f, baseline)
            cubicTo(
                size.width * 0.425f,
                baseline,
                size.width * 0.425f,
                size.height * 0.08f,
                size.width * 0.50f,
                size.height * 0.08f,
            )
            cubicTo(
                size.width * 0.575f,
                size.height * 0.08f,
                size.width * 0.575f,
                baseline,
                size.width * 0.62f,
                baseline,
            )
            lineTo(size.width, baseline)
        }
        drawPath(
            path = path,
            brush = FinalRainbow,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
        )
    }
}
