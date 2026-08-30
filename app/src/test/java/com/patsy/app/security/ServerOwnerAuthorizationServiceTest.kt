package com.patsy.app.security

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ServerOwnerAuthorizationServiceTest {
    private val now = 1_000L
    private fun session(userId: String = "user-1", expiresAt: Long = 2_000L) = PublicSession(
        sessionId = "session-1", userId = userId, username = "local-name",
        maskedEmail = "hidden", emailVerified = true, expiresAtEpochMillis = expiresAt,
    )

    private class Store(var value: StoredAuthSession?) : AuthSessionStore {
        override fun read() = value
        override fun write(session: StoredAuthSession) { value = session }
        override fun clear() { value = null }
    }

    private class Transport(var result: RemoteOwnerAuthorizationDecision) : OwnerAuthorizationTransport {
        override suspend fun authorize(accessToken: String, capability: OwnerCapability) = result
    }

    private fun stored(public: PublicSession = session()) = StoredAuthSession("access", "refresh", public)

    @Test fun ordinaryUserIsDeniedByServerDecision() = runBlocking {
        val service = ServerOwnerAuthorizationService(Store(stored()), Transport(RemoteOwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_OWNER)), { now })
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_OWNER), service.verify(session(), OwnerCapability.VIEW_OWNER_PROFILE))
    }

    @Test fun expiredOrMismatchedSessionFailsClosedBeforeTransportGrant() = runBlocking {
        val grant = RemoteOwnerAuthorizationDecision.Allowed("grant", OwnerCapability.VIEW_OWNER_TOOLS, 2_000L, "audit")
        val service = ServerOwnerAuthorizationService(Store(stored()), Transport(grant), { now })
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_EXPIRED), service.verify(session(expiresAt = 999L), OwnerCapability.VIEW_OWNER_TOOLS))
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED), service.verify(session(userId = "forged"), OwnerCapability.VIEW_OWNER_TOOLS))
    }

    @Test fun oneCapabilityNeverGrantsAnother() = runBlocking {
        val wrong = RemoteOwnerAuthorizationDecision.Allowed("grant", OwnerCapability.VIEW_OWNER_PROFILE, 2_000L, "audit")
        val service = ServerOwnerAuthorizationService(Store(stored()), Transport(wrong), { now })
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.CAPABILITY_NOT_GRANTED), service.verify(session(), OwnerCapability.VIEW_OWNER_TOOLS))
    }

    @Test fun validServerGrantAllowsOnlyRequestedCurrentCapability() = runBlocking {
        val grant = RemoteOwnerAuthorizationDecision.Allowed("grant", OwnerCapability.VIEW_ANALYTICS, 2_000L, "audit")
        val service = ServerOwnerAuthorizationService(Store(stored()), Transport(grant), { now })
        assertIs<OwnerAuthorizationDecision.Allowed>(service.verify(session(), OwnerCapability.VIEW_ANALYTICS))
    }

    @Test fun logoutOrBackendFailureDeniesAccess() = runBlocking {
        val empty = ServerOwnerAuthorizationService(Store(null), Transport(RemoteOwnerAuthorizationDecision.Unavailable), { now })
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_AUTHENTICATED), empty.verify(session(), OwnerCapability.MANAGE_BACKUPS))
        val unavailable = ServerOwnerAuthorizationService(Store(stored()), Transport(RemoteOwnerAuthorizationDecision.Unavailable), { now })
        assertIs<OwnerAuthorizationDecision.Unavailable>(unavailable.verify(session(), OwnerCapability.MANAGE_BACKUPS))
    }
}
