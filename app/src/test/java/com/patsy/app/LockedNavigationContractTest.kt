package com.patsy.app

import com.patsy.app.navigation.ShellDestination
import com.patsy.app.navigation.ShellNavigationContract
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class LockedNavigationContractTest {
    @Test
    fun primaryNavigationMatchesLatestLockedProductRules() {
        assertEquals(listOf("Home","THyNK","Create","Patsy DMs","Profile"),ShellNavigationContract.primaryRoutes.map{it.label})
    }

    @Test
    fun chatAndScheduleRemainSecondaryRoutesNotPrimaryTabs() {
        assertFalse(ShellNavigationContract.primaryRoutes.any{it.id=="chat"||it.id=="schedule"||it.id=="more"})
        assertEquals(ShellDestination.SCHEDULE,ShellNavigationContract.route("schedule")?.destination)
    }

    @Test
    fun homeIsFeedAndThynkHasDedicatedStudioHome() {
        assertEquals(ShellDestination.HOME_FEED,ShellNavigationContract.route("home")?.destination)
        assertEquals(ShellDestination.THYNK,ShellNavigationContract.route("thynk")?.destination)
        assertEquals(ShellDestination.CREATE,ShellNavigationContract.route("create")?.destination)
    }
}
