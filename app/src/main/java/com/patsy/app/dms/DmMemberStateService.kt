package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession

sealed interface DmMemberStateResult {
    data object Updated : DmMemberStateResult
    data object Unauthorized : DmMemberStateResult
    data object Unavailable : DmMemberStateResult
}

interface DmMemberStateTransport {
    suspend fun markRead(accessToken: String, threadId: String): DmMemberStateResult
    suspend fun setArchived(accessToken: String, threadId: String, archived: Boolean): DmMemberStateResult
}

interface DmMemberStateService {
    suspend fun markRead(session: PublicSession, threadId: String): DmMemberStateResult
    suspend fun setArchived(session: PublicSession, threadId: String, archived: Boolean): DmMemberStateResult
}

class ServerDmMemberStateService(
    private val sessionStore: AuthSessionStore,
    private val transport: DmMemberStateTransport,
) : DmMemberStateService {
    override suspend fun markRead(session: PublicSession, threadId: String): DmMemberStateResult {
        if (threadId.isBlank()) return DmMemberStateResult.Unavailable
        val stored = validStoredSession(session) ?: return DmMemberStateResult.Unauthorized
        return transport.markRead(stored.accessToken, threadId)
    }

    override suspend fun setArchived(
        session: PublicSession,
        threadId: String,
        archived: Boolean,
    ): DmMemberStateResult {
        if (threadId.isBlank()) return DmMemberStateResult.Unavailable
        val stored = validStoredSession(session) ?: return DmMemberStateResult.Unauthorized
        return transport.setArchived(stored.accessToken, threadId, archived)
    }

    private fun validStoredSession(session: PublicSession) = sessionStore.read()?.takeIf {
        it.publicSession.sessionId == session.sessionId &&
            it.publicSession.userId == session.userId &&
            it.accessToken.isNotBlank()
    }
}
