package com.patsy.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.R

object PatsyColors {
    val Background = Color.Black
    val Charcoal = Color(0xFF202124)
    val CharcoalRaised = Color(0xFF2A2B2E)
    val White = Color(0xFFF7F7F7)
    val Muted = Color(0xFFAAAAAF)
    val DarkLabel = Color(0xFF111111)
}

val PatsyRainbowBrush = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF6B35),
        Color(0xFFFFD447),
        Color(0xFF4CD964),
        Color(0xFF36A9FF),
        Color(0xFF9B59FF),
        Color(0xFFFF4FA3),
    )
)

const val PATSY_TAGLINE = "A LEGACY LED BY PAWS"

@Composable
fun PatsyHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.patsy_logo_official_white),
            contentDescription = "Patsy",
            modifier = Modifier.width(190.dp).height(82.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = PATSY_TAGLINE,
            color = PatsyColors.Muted,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
fun PatsyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(58.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PatsyColors.White,
            contentColor = PatsyColors.DarkLabel,
        ),
        shape = RoundedCornerShape(30.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
