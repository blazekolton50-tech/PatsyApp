package com.patsy.app.auth

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DebugPreviewAccessTest {
    @Test
    fun debugVariantProvidesAWorkspacePreviewSessionWithoutOwnerAuthority() {
        assertTrue(debugPreviewEnabled)
        val session = createDebugPreviewSession()
        assertNotNull(session)
        assertTrue(session.sessionId.startsWith("debug-preview-"))
        assertFalse(session.emailVerified)
    }

    @Test
    fun releaseVariantHardDisablesPreviewBypass() {
        val releaseSource = File("src/release/java/com/patsy/app/auth/DebugPreviewAccess.kt")
        assertTrue(releaseSource.exists(), "Release source must explicitly disable debug preview access")
        val text = releaseSource.readText()
        assertTrue(text.contains("debugPreviewEnabled = false"))
        assertTrue(text.contains("createDebugPreviewSession(): PublicSession? = null"))
    }

    @Test
    fun debugBuildHasClearlyMarkedPreviewLauncherAndKeepsMainActivityNonLauncher() {
        val activity = File("src/debug/java/com/patsy/app/DebugPreviewActivity.kt")
        val manifest = File("src/debug/AndroidManifest.xml")
        assertTrue(activity.exists(), "Debug preview activity must exist only in debug source set")
        assertTrue(manifest.exists(), "Debug manifest must replace the launcher for test builds")

        val activityText = activity.readText()
        val accessText = File("src/debug/java/com/patsy/app/auth/DebugPreviewAccess.kt").readText()
        val manifestText = manifest.readText()
        assertTrue(activityText.contains("Preview app (DEBUG ONLY)"))
        assertTrue(activityText.contains("PREVIEW_AUTH_REQUESTED_EXTRA"))
        assertTrue(accessText.contains("DebugPreviewAuthGateway"))
        assertFalse(activityText.contains("PatsyServiceBindings.authGateway ="))
        assertTrue(manifestText.contains(".DebugPreviewActivity"))
        assertTrue(manifestText.contains("tools:node=\"replace\""))
    }
}
