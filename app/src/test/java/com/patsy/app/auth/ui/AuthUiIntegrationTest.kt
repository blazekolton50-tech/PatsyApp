package com.patsy.app.auth.ui

import com.patsy.app.auth.AuthFailure
import com.patsy.app.auth.AuthGateway
import com.patsy.app.auth.CompleteRegistrationRequest
import com.patsy.app.auth.ConfirmEmailRequest
import com.patsy.app.auth.EmailConfirmationResult
import com.patsy.app.auth.LoginIdentifier
import com.patsy.app.auth.LoginRequest
import com.patsy.app.auth.LoginResult
import com.patsy.app.auth.LoginSessionRetention
import com.patsy.app.auth.PasswordResetRequest
import com.patsy.app.auth.PasswordResetResult
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.RegistrationResult
import com.patsy.app.auth.RegistrationStartResult
import com.patsy.app.auth.SecretChars
import com.patsy.app.auth.ServiceFailure
import com.patsy.app.auth.SessionEndReason
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.SignOutResult
import com.patsy.app.auth.StartRegistrationRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthUiIntegrationTest {
    @Test
    fun firstVisitUsesExactApprovedIntroduction() {
        val greeting = PatsyGreetingResolver.resolve(
            GreetingContext(
                username = null,
                completedVisits = 0,
                timeOfDay = GreetingTimeOfDay.DAY,
                hasContinuableWork = false,
                variantSeed = 0,
            ),
        )

        assertEquals("Hi, I’m Patsy! Your AI Pet Pal!", greeting)
    }

    @Test
    fun returningMorningGreetingUsesRelationshipNameAndTime() {
        val greeting = PatsyGreetingResolver.resolve(
            GreetingContext(
                username = "Blaze",
                completedVisits = 2,
                timeOfDay = GreetingTimeOfDay.MORNING,
                hasContinuableWork = false,
                variantSeed = 0,
            ),
        )

        assertEquals("Morning, Blaze! What are we getting into?", greeting)
    }

    @Test
    fun continuableWorkTakesPriorityForReturningUser() {
        val greeting = PatsyGreetingResolver.resolve(
            GreetingContext(
                username = "Blaze",
                completedVisits = 3,
                timeOfDay = GreetingTimeOfDay.DAY,
                hasContinuableWork = true,
                variantSeed = 0,
            ),
        )

        assertEquals("You’re back, Blaze! Wanna carry on where we left off?", greeting)
    }

    @Test
    fun previousGreetingIsNotRepeatedWhenAnotherCandidateExists() {
        val greeting = PatsyGreetingResolver.resolve(
            GreetingContext(
                username = "Blaze",
                completedVisits = 4,
                timeOfDay = GreetingTimeOfDay.MORNING,
                hasContinuableWork = false,
                variantSeed = 0,
                previousGreeting = "Morning, Blaze! What are we getting into?",
            ),
        )

        assertEquals("Heeeyy, Blaze! You’re back! Need anything?", greeting)
    }

    @Test
    fun eveningContextCannotProduceMorningCopy() {
        val greeting = PatsyGreetingResolver.resolve(
            GreetingContext(
                username = "Blaze",
                completedVisits = 2,
                timeOfDay = GreetingTimeOfDay.EVENING,
                hasContinuableWork = false,
                variantSeed = 0,
            ),
        )

        assertFalse(greeting.contains("Morning"))
        assertEquals("Heyyy, Blaze — got an idea or are we winging it tonight?", greeting)
    }

    @Test
    fun disabledRememberMeSkipsGatewayRestoration() = runTest {
        val store = FakeRememberMePreferenceStore(initiallyEnabled = false)
        val gateway = CountingAuthGateway(SessionState.Authenticated(session()))
        val coordinator = RememberMeCoordinator(store)

        val result = coordinator.restoreSession(gateway)

        assertIs<SessionState.Anonymous>(result)
        assertEquals(0, gateway.restoreCalls)
    }

    @Test
    fun enabledRememberMeRestoresThroughGatewayOnce() = runTest {
        val store = FakeRememberMePreferenceStore(initiallyEnabled = true)
        val gateway = CountingAuthGateway(SessionState.Authenticated(session()))
        val coordinator = RememberMeCoordinator(store)

        val result = coordinator.restoreSession(gateway)

        assertIs<SessionState.Authenticated>(result)
        assertEquals(1, gateway.restoreCalls)
        assertTrue(store.enabled)
    }

    @Test
    fun expiredRestorationClearsRememberMeOptIn() = runTest {
        val store = FakeRememberMePreferenceStore(initiallyEnabled = true)
        val gateway = CountingAuthGateway(SessionState.Expired(SessionEndReason.EXPIRED))
        val coordinator = RememberMeCoordinator(store)

        coordinator.restoreSession(gateway)

        assertFalse(store.enabled)
    }

    @Test
    fun temporaryRestorationFailureKeepsRememberMeOptIn() = runTest {
        val store = FakeRememberMePreferenceStore(initiallyEnabled = true)
        val gateway = CountingAuthGateway(SessionState.Unavailable(ServiceFailure.Offline))
        val coordinator = RememberMeCoordinator(store)

        coordinator.restoreSession(gateway)

        assertTrue(store.enabled)
    }

    @Test
    fun signOutAlwaysClearsRememberMeOptIn() = runTest {
        val store = FakeRememberMePreferenceStore(initiallyEnabled = true)
        val gateway = CountingAuthGateway(
            restoreResult = SessionState.Anonymous,
            signOutResult = SignOutResult.Unavailable(ServiceFailure.Offline),
        )
        val coordinator = RememberMeCoordinator(store)

        val result = coordinator.signOut(gateway)

        assertIs<SignOutResult.Unavailable>(result)
        assertFalse(store.enabled)
    }

    @Test
    fun loginRetentionIsCurrentProcessUnlessRememberMeIsChecked() {
        val coordinator = RememberMeCoordinator(FakeRememberMePreferenceStore(false))

        assertEquals(
            LoginSessionRetention.CURRENT_PROCESS_ONLY,
            coordinator.retentionFor(rememberMeEnabled = false),
        )
        assertEquals(
            LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH,
            coordinator.retentionFor(rememberMeEnabled = true),
        )
    }

    @Test
    fun loginRequestNeverPrintsPassword() {
        val password = SecretChars.copyOf("NeverPrintThis!9".toCharArray())
        try {
            val request = LoginRequest(
                identifier = LoginIdentifier.Username("blaze"),
                password = password,
                sessionRetention = LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH,
            )

            assertFalse(request.toString().contains("NeverPrintThis!9"))
            assertTrue(request.toString().contains("[REDACTED]"))
        } finally {
            password.close()
        }
    }

    @Test
    fun accountMenuExposesOnlyTheLockedFourPrimaryActionsInOrder() {
        assertEquals(
            listOf("my_account", "security_privacy", "patsy_settings", "log_out"),
            PatsyAccountMenu.items.map { it.stableId },
        )
        assertEquals(
            listOf("My Account", "Security & Privacy", "Patsy Settings", "Log Out"),
            PatsyAccountMenu.items.map { it.title },
        )
    }

    private fun session() = PublicSession(
        sessionId = "session-1",
        userId = "user-1",
        username = "blaze",
        maskedEmail = "b***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )
}

private class FakeRememberMePreferenceStore(
    initiallyEnabled: Boolean,
) : RememberMePreferenceStore {
    var enabled: Boolean = initiallyEnabled
        private set

    override suspend fun isSessionRestoreEnabled(): Boolean = enabled

    override suspend fun setSessionRestoreEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}

private class CountingAuthGateway(
    private val restoreResult: SessionState,
    private val signOutResult: SignOutResult = SignOutResult.SignedOut,
) : AuthGateway {
    var restoreCalls: Int = 0
        private set

    override suspend fun startRegistration(request: StartRegistrationRequest) =
        RegistrationStartResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun completeRegistration(request: CompleteRegistrationRequest) =
        RegistrationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun confirmEmail(request: ConfirmEmailRequest) =
        EmailConfirmationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun login(request: LoginRequest) =
        LoginResult.Rejected(AuthFailure.InvalidCredentials)

    override suspend fun requestPasswordReset(request: PasswordResetRequest) =
        PasswordResetResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun restoreSession(): SessionState {
        restoreCalls += 1
        return restoreResult
    }

    override suspend fun signOut(): SignOutResult = signOutResult
}
