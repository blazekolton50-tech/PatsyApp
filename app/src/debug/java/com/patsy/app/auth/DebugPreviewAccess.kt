package com.patsy.app.auth

/**
 * Development-only workspace preview access.
 *
 * This is deliberately compiled only into the debug source set. It does not authenticate with
 * Supabase, does not persist a production session, and carries no OWNER/admin authority.
 */
internal const val debugPreviewEnabled = true

internal fun createDebugPreviewSession(): PublicSession = PublicSession(
    sessionId = "debug-preview-${System.currentTimeMillis()}",
    userId = "debug-preview",
    username = "Preview",
    maskedEmail = "preview@local",
    emailVerified = false,
    expiresAtEpochMillis = System.currentTimeMillis() + 4 * 60 * 60 * 1000L,
)
