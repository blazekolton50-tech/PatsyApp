package com.patsy.app

import com.patsy.app.navigation.ShellDestination
import com.patsy.app.navigation.ShellNavigationContract
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LockedNavigationContractTest {
    @Test
    fun primaryNavigationMatchesLatestLockedProductRules() {
        assertEquals(
            listOf("Home", "THyNK", "Camera", "PATSY DMs", "Profile"),
            ShellNavigationContract.primaryRoutes.map { it.label },
        )
    }

    @Test
    fun chatScheduleMoreAndCreateRemainSecondaryRoutesNotPrimaryTabs() {
        assertFalse(
            ShellNavigationContract.primaryRoutes.any {
                it.id == "chat" || it.id == "schedule" || it.id == "more" || it.id == "create"
            },
        )
        assertEquals(ShellDestination.SCHEDULE, ShellNavigationContract.route("schedule")?.destination)
    }

    @Test
    fun homeThynkAndCameraHaveDedicatedPrimaryDestinations() {
        assertEquals("HOME_FEED", ShellNavigationContract.route("home")?.destination?.name)
        assertEquals("THYNK", ShellNavigationContract.route("thynk")?.destination?.name)
        assertEquals("CAMERA", ShellNavigationContract.route("camera")?.destination?.name)
    }
}
