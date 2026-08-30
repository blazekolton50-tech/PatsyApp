package com.patsy.app.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseAuthGatewayTest {
    private class MemoryStore : AuthSessionStore {
        var value: StoredAuthSession? = null
        override fun read(): StoredAuthSession? = value
        override fun write(session: StoredAuthSession) { value = session }
        override fun clear() { value = null }
    }

    private class FakeTransport : SupabaseAuthTransport {
        var loginResult: RemoteAuthResult = RemoteAuthResult.Failure(RemoteAuthFailure.InvalidCredentials)
        var refreshResult: RemoteAuthResult = RemoteAuthResult.Failure(RemoteAuthFailure.InvalidSession)
        var signOutResult: RemoteSignOutResult = RemoteSignOutResult.SignedOut
        var observedPassword: CharArray? = null
        override suspend fun login(identifier: LoginIdentifier, password: CharArray): RemoteAuthResult {
            observedPassword = password
            return loginResult
        }
        override suspend fun refresh(refreshToken: String): RemoteAuthResult = refreshResult
        override suspend fun signOut(accessToken: String): RemoteSignOutResult = signOutResult
    }

    private fun remoteSession() = RemoteAuthSession(
        accessToken = "access",
        refreshToken = "refresh",
        sessionId = "session-1",
        userId = "user-1",
        username = "PatsyUser",
        maskedEmail = "pa***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = 4_000_000_000_000L,
    )

    @Test
    fun successfulLoginStoresTokensAndReturnsOnlyPublicSession() = kotlinx.coroutines.runBlocking {
        val transport = FakeTransport().apply { loginResult = RemoteAuthResult.Authenticated(remoteSession()) }
        val store = MemoryStore()
        val gateway = SupabaseAuthGateway(transport, store)
        val secret = SecretChars.copyOf("ValidPassword123!".toCharArray())
        val result = try {
            gateway.login(LoginRequest(LoginIdentifier.Username("PatsyUser"), secret))
        } finally {
            secret.close()
        }

        val authenticated = assertIs<LoginResult.Authenticated>(result)
        assertEquals("user-1", authenticated.session.userId)
        assertEquals("PatsyUser", authenticated.session.username)
        assertEquals("access", store.value?.accessToken)
        assertEquals("refresh", store.value?.refreshToken)
        assertTrue(transport.observedPassword?.all { it == '\u0000' } == true)
    }

    @Test
    fun invalidCredentialsStayGenericAndDoNotCreateSession() = kotlinx.coroutines.runBlocking {
        val transport = FakeTransport()
        val store = MemoryStore()
        val gateway = SupabaseAuthGateway(transport, store)
        val secret = SecretChars.copyOf("WrongPassword123!".toCharArray())
        val result = try {
            gateway.login(LoginRequest(LoginIdentifier.Email("nobody@example.com"), secret))
        } finally {
            secret.close()
        }

        val rejected = assertIs<LoginResult.Rejected>(result)
        assertEquals(AuthFailure.InvalidCredentials, rejected.failure)
        assertNull(store.value)
    }

    @Test
    fun restoreRefreshesStoredSessionBeforeTrustingIt() = kotlinx.coroutines.runBlocking {
        val transport = FakeTransport().apply { refreshResult = RemoteAuthResult.Authenticated(remoteSession()) }
        val store = MemoryStore().apply {
            value = StoredAuthSession(
                accessToken = "old-access",
                refreshToken = "old-refresh",
                publicSession = PublicSession("old", "user-1", "Old", "hidden", false, 1L),
            )
        }
        val gateway = SupabaseAuthGateway(transport, store)
        val result = gateway.restoreSession()

        val authenticated = assertIs<SessionState.Authenticated>(result)
        assertEquals("session-1", authenticated.session.sessionId)
        assertEquals("access", store.value?.accessToken)
    }

    @Test
    fun invalidRefreshClearsLocalSession() = kotlinx.coroutines.runBlocking {
        val store = MemoryStore().apply {
            value = StoredAuthSession(
                accessToken = "old-access",
                refreshToken = "old-refresh",
                publicSession = PublicSession("old", "user-1", "Old", "hidden", false, 1L),
            )
        }
        val gateway = SupabaseAuthGateway(FakeTransport(), store)
        val result = gateway.restoreSession()

        assertIs<SessionState.Expired>(result)
        assertNull(store.value)
    }

    @Test
    fun signOutClearsLocalTokensEvenWhenServerIsUnavailable() = kotlinx.coroutines.runBlocking {
        val transport = FakeTransport().apply { signOutResult = RemoteSignOutResult.Unavailable(ServiceFailure.Offline) }
        val store = MemoryStore().apply {
            value = StoredAuthSession(
                accessToken = "access",
                refreshToken = "refresh",
                publicSession = PublicSession("session", "user-1", "User", "hidden", true, 2L),
            )
        }
        val gateway = SupabaseAuthGateway(transport, store)
        val result = gateway.signOut()

        assertIs<SignOutResult.Unavailable>(result)
        assertNull(store.value)
    }
}
