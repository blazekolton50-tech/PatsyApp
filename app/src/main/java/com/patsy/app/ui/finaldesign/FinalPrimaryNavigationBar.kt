package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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

/**
 * SAVE MAIN APP / LOCK IN SAVE.
 *
 * Exact approved primary navigation visual:
 * Home • THyNK mark only • large centre + Camera • PDMs • Profile.
 *
 * Geometry lock:
 * - rainbow line is straight on the left and right
 * - only the centre section rises in a smooth arch around/over the Camera button
 * - the centre + is the Camera destination
 * - this is the single app-wide homebar and must be retained on every authenticated page
 *
 * The semantic routes remain HOME • THyNK • CAMERA • PATSY DMS • PROFILE underneath.
 * NOTE: FinalHomeDestination.CREATE is a legacy internal enum name only; it maps to the real Camera hub.
 */
@Composable
fun FinalPrimaryNavigationBar(
    selected: FinalHomeDestination?,
    onNavigate: (FinalHomeDestination) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth().background(Color(0xFF0D0D10))) {
        FinalHomebarRainbowLine()
        Row(
            Modifier.fillMaxWidth().height(75.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            FinalNavItem("⌂", "Home", selected == FinalHomeDestination.HOME, enabled) {
                onNavigate(FinalHomeDestination.HOME)
            }

            // Temporary text fallback only until the official THyNK PNG is copied into drawable-nodpi.
            // Do not add a second THyNK caption below it.
            FinalNavItem("THyNK", "", selected == FinalHomeDestination.THYNK, enabled) {
                onNavigate(FinalHomeDestination.THYNK)
            }

            // The large centre + is Camera. It sits into the raised centre arch of the rainbow line.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-12).dp),
            ) {
                Box(
                    Modifier
                        .size(60.dp)
                        .background(FinalRainbow, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(enabled = enabled) { onNavigate(FinalHomeDestination.CREATE) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", color = Color.Black, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
            }

            FinalNavItem("◌", "PDMs", selected == FinalHomeDestination.PATSY_DMS, enabled) {
                onNavigate(FinalHomeDestination.PATSY_DMS)
            }
            FinalNavItem("♙", "Profile", selected == FinalHomeDestination.PROFILE, enabled) {
                onNavigate(FinalHomeDestination.PROFILE)
            }
        }
    }
}

@Composable
private fun FinalNavItem(
    icon: String,
    label: String,
    active: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(68.dp).clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            icon,
            style = if (active || icon == "THyNK") {
                TextStyle(
                    brush = FinalRainbow,
                    fontSize = if (icon == "THyNK") 16.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                )
            } else {
                TextStyle(color = FinalMuted, fontSize = 22.sp)
            },
            maxLines = 1,
        )
        if (label.isNotEmpty()) {
            Text(
                label,
                color = if (active) Color(0xFFFF56AB) else FinalMuted,
                fontSize = 9.sp,
                maxLines = 1,
            )
        }
    }
}
