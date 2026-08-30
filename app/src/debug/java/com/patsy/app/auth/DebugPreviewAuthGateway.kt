package com.patsy.app.auth

/**
 * Debug-only gateway used solely to let test builds restore a local preview session.
 * All credential-bearing and account-changing operations remain unavailable.
 */
internal object DebugPreviewAuthGateway : AuthGateway {
    override suspend fun startRegistration(request: StartRegistrationRequest) =
        RegistrationStartResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun completeRegistration(request: CompleteRegistrationRequest) =
        RegistrationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun confirmEmail(request: ConfirmEmailRequest) =
        EmailConfirmationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun login(request: LoginRequest) =
        LoginResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun requestPasswordReset(request: PasswordResetRequest) =
        PasswordResetResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun restoreSession() =
        SessionState.Authenticated(createDebugPreviewSession())

    override suspend fun signOut() = SignOutResult.SignedOut
}
