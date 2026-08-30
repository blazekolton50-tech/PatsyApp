package com.patsy.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ConnectedMuted = Color(0xFFAAAAAF)
private val ConnectedWhite = Color(0xFFF7F7F7)
private val ConnectedRaised = Color(0xFF171717)

/**
 * Latest locked HOME destination. Home is the Patsy Social/news-feed entry point, while useful
 * secondary routes remain available without occupying the five locked bottom-navigation slots.
 */
@Composable
fun HomeFeed(profile: Profile?, nav: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PatsyMotion("Hey ${profile?.displayName ?: "you"} —", action = PatsyAction.HAPPY)
        ConnectedAction("Ask your Pet Pal Patsy a question") { nav(Screen.CHAT) }

        Text("HOME", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Text("Patsy Social", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Panel {
            Text("News feed", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "The live community feed is not connected in this build yet. This is the locked Home/feed route, ready for the Social backend rather than fake posts.",
                color = ConnectedMuted,
            )
        }

        Text("Continue Designs", fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ConnectedTile("Continue designs", Modifier.weight(1f)) { nav(Screen.THYNK) }
            ConnectedTile("Camera", Modifier.weight(1f)) { nav(Screen.CAMERA) }
        }
        ConnectedAction("Patsy DMs") { nav(Screen.DMS) }

        Panel {
            Text("Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Tasks, reminders and scheduled work live here as a secondary workflow.", color = ConnectedMuted)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { nav(Screen.SCHEDULE) }, modifier = Modifier.fillMaxWidth()) {
                Text("Open Today / Schedule", color = ConnectedWhite)
            }
        }
    }
}

/** THyNK Studio home follows the approved creation-area hierarchy. */
@Composable
fun ThynkStudioHome(nav: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PatsyMotion("Want to carry on where you left off?", action = PatsyAction.POINTING)
        Text("THyNK", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ConnectedWhite)
        Text("Your creation studio", color = ConnectedMuted)
        ConnectedAction("Create New") { nav(Screen.CREATE) }
        ConnectedAction("Templates") { nav(Screen.THYNK_TEMPLATES) }
        ConnectedAction("Editor") { nav(Screen.THYNK_EDITOR) }
        ConnectedAction("AI Image Generator") { nav(Screen.THYNK_AI_IMAGE) }
        ConnectedAction("AI Video Generator") { nav(Screen.THYNK_AI_VIDEO) }
        ConnectedAction("My Projects") { nav(Screen.THYNK_PROJECTS) }
        ConnectedAction("Brand Kit") { nav(Screen.THYNK_BRAND_KIT) }
        ConnectedAction("Inspiration") { nav(Screen.THYNK_INSPIRATION) }
    }
}

/**
 * Locked middle-plus destination. It is a Camera/Create hub rather than a normal CREATE tab.
 * Camera capture itself stays truthful until the native camera pipeline is wired and verified.
 */
@Composable
fun CameraCreateHome(nav: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PatsyMotion("Camera ready — what are we making?", action = PatsyAction.POINTING)
        Text("CAMERA", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Panel {
            Text("Camera / upload entry", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Preview C connects the Camera hub without pretending native capture is production-ready. Capture and media permissions remain a dedicated implementation step.",
                color = ConnectedMuted,
            )
        }
        ConnectedAction("Open THyNK Editor") { nav(Screen.THYNK_EDITOR) }
        ConnectedAction("Use a Template") { nav(Screen.THYNK_TEMPLATES) }
        ConnectedAction("AI Image") { nav(Screen.THYNK_AI_IMAGE) }
        ConnectedAction("AI Video") { nav(Screen.THYNK_AI_VIDEO) }
        ConnectedAction("Continue a Project") { nav(Screen.THYNK_PROJECTS) }
    }
}

/** Secondary Create flow retained for THyNK Studio. */
@Composable
fun CreateNewHome(nav: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PatsyMotion("Right then — what are we making?", action = PatsyAction.HAPPY)
        Text("CREATE NEW", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        ConnectedAction("Blank design / Editor") { nav(Screen.THYNK_EDITOR) }
        ConnectedAction("Use a Template") { nav(Screen.THYNK_TEMPLATES) }
        ConnectedAction("AI Image") { nav(Screen.THYNK_AI_IMAGE) }
        ConnectedAction("10-second AI Video") { nav(Screen.THYNK_AI_VIDEO) }
        ConnectedAction("Continue a Project") { nav(Screen.THYNK_PROJECTS) }
    }
}

@Composable
fun ThynkSectionPage(
    title: String,
    detail: String,
    back: () -> Unit,
    action: (() -> Unit)? = null,
    actionLabel: String? = null,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Panel {
            Text(detail, color = ConnectedMuted)
            if (action != null && actionLabel != null) {
                Spacer(Modifier.height(12.dp))
                Primary(actionLabel, action)
            }
        }
        OutlinedButton(onClick = back, modifier = Modifier.fillMaxWidth()) {
            Text("Back to THyNK", color = ConnectedWhite)
        }
    }
}

/** Lightweight truthful shells for the locked three-dot workspace menu. */
@Composable
fun PreviewCSecondaryPage(
    title: String,
    detail: String,
    back: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ConnectedWhite)
        Panel { Text(detail, color = ConnectedMuted) }
        OutlinedButton(onClick = back, modifier = Modifier.fillMaxWidth()) {
            Text("Back", color = ConnectedWhite)
        }
    }
}

@Composable
private fun ConnectedAction(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(58.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF111111)),
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConnectedTile(text: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(84.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ConnectedRaised, contentColor = ConnectedWhite),
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}
