package com.patsy.app.dms

import com.patsy.app.auth.PublicSession

object DmServiceBindings {
    var dmDataService: DmDataService = UnconfiguredDmDataService
    var conversationService: DmConversationService = UnconfiguredDmConversationService
}

private object UnconfiguredDmDataService : DmDataService {
    override suspend fun load(session: PublicSession): DmDataResult = DmDataResult.Unavailable
}

private object UnconfiguredDmConversationService : DmConversationService {
    override suspend fun load(session: PublicSession, threadId: String): DmConversationResult =
        DmConversationResult.Unavailable

    override suspend fun send(session: PublicSession, threadId: String, body: String): DmSendResult =
        DmSendResult.Unavailable
}
