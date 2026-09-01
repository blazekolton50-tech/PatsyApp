package com.patsy.app.dms

import com.patsy.app.auth.PublicSession

object DmServiceBindings {
    var dmDataService: DmDataService = UnconfiguredDmDataService
}

private object UnconfiguredDmDataService : DmDataService {
    override suspend fun load(session: PublicSession): DmDataResult = DmDataResult.Unavailable
}
