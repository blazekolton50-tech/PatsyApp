package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Locked primary navigation: HOME • THyNK • CREATE • PATSY DMS • PROFILE. */
@Composable
fun FinalPrimaryNavigationBar(
    selected: FinalHomeDestination,
    onNavigate: (FinalHomeDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().background(Color(0xFF0D0D10))) {
        FinalBottomWave(showFooterCopy = false)
        Row(
            Modifier.fillMaxWidth().height(75.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            FinalNavItem("⌂", "Home", selected == FinalHomeDestination.HOME) { onNavigate(FinalHomeDestination.HOME) }
            FinalNavItem("THyNK", "THyNK", selected == FinalHomeDestination.THYNK) { onNavigate(FinalHomeDestination.THYNK) }
            Box(
                Modifier
                    .size(66.dp)
                    .background(FinalRainbow, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onNavigate(FinalHomeDestination.CREATE) },
                contentAlignment = Alignment.Center,
            ) {
                Text("+", color = Color.Black, fontSize = 38.sp, fontWeight = FontWeight.Black)
            }
            FinalNavItem("◌", "PATSY DMS", selected == FinalHomeDestination.PATSY_DMS) { onNavigate(FinalHomeDestination.PATSY_DMS) }
            FinalNavItem("♙", "Profile", selected == FinalHomeDestination.PROFILE) { onNavigate(FinalHomeDestination.PROFILE) }
        }
    }
}

@Composable
private fun FinalNavItem(icon: String, label: String, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(68.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            icon,
            style = if (active || icon == "THyNK") {
                TextStyle(brush = FinalRainbow, fontSize = if (icon == "THyNK") 16.sp else 22.sp, fontWeight = FontWeight.Black)
            } else {
                TextStyle(color = FinalMuted, fontSize = 22.sp)
            },
            maxLines = 1,
        )
        Text(
            label,
            color = if (active) Color(0xFFFF56AB) else FinalMuted,
            fontSize = if (label == "PATSY DMS") 8.sp else 9.sp,
            maxLines = 1,
        )
    }
}
