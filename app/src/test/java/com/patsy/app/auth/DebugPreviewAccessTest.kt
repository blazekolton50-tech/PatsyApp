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
}
