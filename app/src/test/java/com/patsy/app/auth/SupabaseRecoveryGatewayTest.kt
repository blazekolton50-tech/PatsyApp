package com.patsy.app.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SupabaseRecoveryGatewayTest {
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

    private class FakeRecoveryTransport(
        var result: RemotePasswordResetResult,
    ) : SupabaseRecoveryTransport {
        override suspend fun request(identifier: String) = result
    }

    @Test
    fun acceptedResetReturnsGenericNonEnumeratingMessage() = kotlinx.coroutines.runBlocking {
        val message = "If an account matches and email delivery is available, you'll receive password reset instructions."
        val gateway = SupabaseAuthGateway(
            transport = NoopAuthTransport(),
            sessionStore = MemoryStore(),
            recoveryTransport = FakeRecoveryTransport(RemotePasswordResetResult.Accepted(message)),
        )

        val result = gateway.requestPasswordReset(PasswordResetRequest("someone@example.com"))
        val accepted = assertIs<PasswordResetResult.RequestAccepted>(result)
        assertEquals(message, accepted.genericMessage)
    }

    @Test
    fun providerTransportFailureNeverBecomesPretendSuccess() = kotlinx.coroutines.runBlocking {
        val gateway = SupabaseAuthGateway(
            transport = NoopAuthTransport(),
            sessionStore = MemoryStore(),
            recoveryTransport = FakeRecoveryTransport(RemotePasswordResetResult.Unavailable(ServiceFailure.Offline)),
        )

        val result = gateway.requestPasswordReset(PasswordResetRequest("some-user"))
        val unavailable = assertIs<PasswordResetResult.Unavailable>(result)
        assertEquals(ServiceFailure.Offline, unavailable.failure)
    }
}
