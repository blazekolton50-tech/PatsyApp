package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsy.app.R

/**
 * Official five-destination THyNK homebar.
 *
 * The supplied raster owns the logos and their exact left-to-right visual order. Five equal overlay
 * hit regions provide real navigation without redrawing or approximating any logo with text.
 */
@Composable
fun FinalPrimaryNavigationBar(
    selected: ThynkPanelDestination?,
    onNavigate: (ThynkPanelDestination) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rainbowEnabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(Color.Black),
    ) {
        Image(
            painter = painterResource(R.drawable.thynk_panel_official),
            contentDescription = "THyNK navigation: THyNK-ME, THyNK Chats, THyNK-IN!, THyNK Music, THyNK-IT",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize(),
        )

        Row(Modifier.fillMaxSize().padding(vertical = 5.dp)) {
            ThynkPanelDestination.entries.forEach { destination ->
                val active = shouldLightThynkPanelItem(
                    item = destination,
                    selected = selected,
                    rainbowEnabled = rainbowEnabled,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .then(
                            if (active) {
                                Modifier.border(
                                    width = 2.dp,
                                    brush = FinalRainbow,
                                    shape = RoundedCornerShape(14.dp),
                                )
                            } else {
                                Modifier
                            },
                        )
                        .clickable(enabled = enabled) { onNavigate(destination) },
                )
            }
        }
    }
}
