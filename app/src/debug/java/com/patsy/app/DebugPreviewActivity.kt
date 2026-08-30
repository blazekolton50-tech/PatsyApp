package com.patsy.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.auth.PREVIEW_AUTH_REQUESTED_EXTRA
import com.patsy.app.ui.PatsyHeader
import com.patsy.app.ui.PatsyPrimaryButton

/** Debug-build launcher only. Release builds launch MainActivity directly and never include this file. */
class DebugPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = Color.Black, surface = Color(0xFF101010))) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    DebugPreviewScreen(
                        openNormalLogin = {
                            launchMainApp(previewRequested = false)
                        },
                        openWorkspacePreview = {
                            launchMainApp(previewRequested = true)
                        },
                    )
                }
            }
        }
    }

    private fun launchMainApp(previewRequested: Boolean) {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(PREVIEW_AUTH_REQUESTED_EXTRA, previewRequested),
        )
        finish()
    }
}

@Composable
private fun DebugPreviewScreen(
    openNormalLogin: () -> Unit,
    openWorkspacePreview: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PatsyHeader()
        Spacer(Modifier.height(24.dp))
        Text("Development build", color = Color.White, fontSize = 22.sp)
        Text(
            "Use normal login, or enter a local preview so you can inspect the connected app pages.",
            color = Color(0xFFAAAAAF),
            modifier = Modifier.padding(vertical = 14.dp),
        )
        PatsyPrimaryButton(
            text = "Preview app (DEBUG ONLY)",
            onClick = openWorkspacePreview,
        )
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = openNormalLogin,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue to normal login", color = Color.White)
        }
        Text(
            "Preview does not sign in, verify email, or grant OWNER access.",
            color = Color(0xFFAAAAAF),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
