package com.patsy.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

/** Preview C locked top-menu order. */
enum class PatsyTopMenuAction {
    ACCOUNT,
    ABOUT,
    PROFILE,
    SETTINGS,
    REMEMBER_ME,
}

/**
 * Shared Patsy logo treatment. Preview C reduces the exact approved logo asset to a 32dp image
 * height instead of replacing it with plain text or another logo approximation.
 */
@Composable
fun PatsyHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PatsyLogo32()
        Text(
            text = PATSY_TAGLINE,
            color = PatsyColors.Muted,
            fontSize = 8.sp,
            letterSpacing = 0.8.sp,
        )
    }
}

/** Workspace header: exact 32dp Patsy logo centred, with the locked three-dot menu at top-right. */
@Composable
fun PatsyWorkspaceHeader(
    onAction: (PatsyTopMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = remember { mutableStateOf(false) }
    Box(
        modifier = modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        PatsyLogo32()
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(onClick = { expanded.value = true }) {
                Text(
                    text = "⋯",
                    color = PatsyColors.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                containerColor = Color(0xFF1E1E22),
            ) {
                topMenuItem("Account", PatsyTopMenuAction.ACCOUNT, expanded, onAction)
                topMenuItem("About", PatsyTopMenuAction.ABOUT, expanded, onAction)
                topMenuItem("Profile", PatsyTopMenuAction.PROFILE, expanded, onAction)
                topMenuItem("Settings", PatsyTopMenuAction.SETTINGS, expanded, onAction)
                topMenuItem("Remember Me", PatsyTopMenuAction.REMEMBER_ME, expanded, onAction)
            }
        }
    }
}

@Composable
private fun PatsyLogo32() {
    Image(
        painter = painterResource(R.drawable.patsy_logo_official_white),
        contentDescription = "Patsy",
        modifier = Modifier.width(96.dp).height(32.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun topMenuItem(
    label: String,
    action: PatsyTopMenuAction,
    expanded: androidx.compose.runtime.MutableState<Boolean>,
    onAction: (PatsyTopMenuAction) -> Unit,
) {
    DropdownMenuItem(
        text = { Text(label, color = PatsyColors.White) },
        onClick = {
            expanded.value = false
            onAction(action)
        },
    )
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
