package com.patsy.app.auth

/** Production builds must never expose the development workspace preview bypass. */
internal const val debugPreviewEnabled = false

internal fun resolveLaunchAuthGateway(
    previewRequested: Boolean,
    productionGateway: AuthGateway,
): AuthGateway = productionGateway

internal fun createDebugPreviewSession(): PublicSession? = null
