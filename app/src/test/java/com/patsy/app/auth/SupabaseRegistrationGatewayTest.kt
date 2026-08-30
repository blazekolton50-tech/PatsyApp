package com.patsy.app.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SupabaseRegistrationGatewayTest {
    private class NoopAuthTransport : SupabaseAuthTransport {
        override suspend fun login(identifier: LoginIdentifier, password: CharArray) =
            RemoteAuthResult.Failure(RemoteAuthFailure.InvalidCredentials)
        override suspend fun refresh(refreshToken: String) =
            RemoteAuthResult.Failure(RemoteAuthFailure.InvalidSession)
        override suspend fun signOut(accessToken: String) = RemoteSignOutResult.SignedOut
    }

    private class MemoryStore : AuthSessionStore {
        override fun read(): StoredAuthSession? = null
        override fun write(session: StoredAuthSession) = Unit
        override fun clear() = Unit
    }

    private class FakeRegistrationTransport : SupabaseRegistrationTransport {
        var startResult: RemoteRegistrationStartResult = RemoteRegistrationStartResult.Ready(
            attemptId = "attempt-1",
            normalizedUsername = "PatsyUser",
            maskedEmail = "pa***@example.com",
        )
        var completeResult: RemoteRegistrationResult = RemoteRegistrationResult.AccountCreated(
            userId = "user-1",
            username = "PatsyUser",
            maskedEmail = "pa***@example.com",
            deliveryState = EmailDeliveryState.QUEUED,
        )
        override suspend fun start(request: StartRegistrationRequest) = startResult
        override suspend fun complete(attemptId: String, password: CharArray) = completeResult
    }

    private fun gateway(registration: FakeRegistrationTransport) = SupabaseAuthGateway(
        transport = NoopAuthTransport(),
        sessionStore = MemoryStore(),
        registrationTransport = registration,
    )

    @Test
    fun startRegistrationReturnsRealPasswordStepMetadata() = kotlinx.coroutines.runBlocking {
        val result = gateway(FakeRegistrationTransport()).startRegistration(
            StartRegistrationRequest("PatsyUser", "patsy@example.com", "16+ Patsy")
        )
        val ready = assertIs<RegistrationStartResult.ReadyForPassword>(result)
        assertEquals("attempt-1", ready.registrationAttemptId)
        assertEquals("PatsyUser", ready.normalizedUsername)
        assertEquals("pa***@example.com", ready.maskedEmail)
    }

    @Test
    fun reservedOrDuplicateUsernameFailsBeforePasswordStep() = kotlinx.coroutines.runBlocking {
        val registration = FakeRegistrationTransport().apply {
            startResult = RemoteRegistrationStartResult.Failure(RemoteRegistrationFailure.DuplicateUsername)
        }
        val result = gateway(registration).startRegistration(
            StartRegistrationRequest("taken", "new@example.com", "16+ Patsy")
        )
        val rejected = assertIs<RegistrationStartResult.Rejected>(result)
        assertEquals(AuthFailure.DuplicateUsername, rejected.failure)
    }

    @Test
    fun completedRegistrationReportsQueuedEmailWithoutCallingItSent() = kotlinx.coroutines.runBlocking {
        val registration = FakeRegistrationTransport()
        val secret = SecretChars.copyOf("ValidPassword123!".toCharArray())
        val result = try {
            gateway(registration).completeRegistration(CompleteRegistrationRequest("attempt-1", secret))
        } finally {
            secret.close()
        }
        val created = assertIs<RegistrationResult.AccountCreated>(result)
        val confirmation = assertIs<ConfirmationEmailAcknowledgement.Status>(created.confirmationEmail)
        assertEquals(EmailDeliveryState.QUEUED, confirmation.deliveryState)
        assertEquals("pa***@example.com", confirmation.maskedEmail)
    }

    @Test
    fun duplicateEmailMapsToExistingAuthFailureWithoutPretendSuccess() = kotlinx.coroutines.runBlocking {
        val registration = FakeRegistrationTransport().apply {
            completeResult = RemoteRegistrationResult.Failure(RemoteRegistrationFailure.DuplicateEmail)
        }
        val secret = SecretChars.copyOf("ValidPassword123!".toCharArray())
        val result = try {
            gateway(registration).completeRegistration(CompleteRegistrationRequest("attempt-1", secret))
        } finally {
            secret.close()
        }
        val rejected = assertIs<RegistrationResult.Rejected>(result)
        assertEquals(AuthFailure.DuplicateEmail, rejected.failure)
    }
}
