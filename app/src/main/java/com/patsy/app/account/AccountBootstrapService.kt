package com.patsy.app.account

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession

sealed interface RemoteAccountBootstrapResult {
    data class Available(val account: AccountBootstrap) : RemoteAccountBootstrapResult
    data object Unavailable : RemoteAccountBootstrapResult
}

interface AccountBootstrapTransport {
    suspend fun fetch(accessToken: String): RemoteAccountBootstrapResult
}

interface AccountBootstrapService {
    suspend fun fetch(session: PublicSession): AccountBootstrapResult
}

class ServerAccountBootstrapService(
    private val sessionStore: AuthSessionStore,
    private val transport: AccountBootstrapTransport,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : AccountBootstrapService {
    override suspend fun fetch(session: PublicSession): AccountBootstrapResult {
        fun failed(reason: BootstrapFailure) = AccountBootstrapResult.FailedClosed(reason, protectedBootstrap(session.userId))
        val now = nowEpochMillis()
        if (session.expiresAtEpochMillis <= now) return failed(BootstrapFailure.SESSION_EXPIRED)
        val stored = sessionStore.read() ?: return failed(BootstrapFailure.NOT_AUTHENTICATED)
        if (stored.publicSession.userId != session.userId || stored.publicSession.sessionId != session.sessionId) {
            return failed(BootstrapFailure.SESSION_MISMATCH)
        }
        return when (val remote = transport.fetch(stored.accessToken)) {
            RemoteAccountBootstrapResult.Unavailable -> failed(BootstrapFailure.BACKEND_UNAVAILABLE)
            is RemoteAccountBootstrapResult.Available -> {
                val account = remote.account
                when {
                    account.canonicalUserId != session.userId -> failed(BootstrapFailure.MALFORMED)
                    account.validUntilEpochMillis <= now -> failed(BootstrapFailure.EXPIRED)
                    account.canonicalUserId.isBlank() -> failed(BootstrapFailure.MALFORMED)
                    else -> AccountBootstrapResult.Available(account)
                }
            }
        }
    }
}
