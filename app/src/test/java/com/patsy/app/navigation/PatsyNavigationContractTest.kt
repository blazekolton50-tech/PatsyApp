package com.patsy.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PatsyNavigationContractTest {
    @Test
    fun primaryNavigationMatchesLatestLockedFiveDestinationsInOrder() {
        assertEquals(
            listOf("HOME", "THYNK", "CREATE", "PATSY_DMS", "PROFILE"),
            PatsyNavigationContract.primary.map { it.key },
        )
        assertEquals(
            listOf("Home", "THyNK", "Create", "Patsy DMs", "Profile"),
            PatsyNavigationContract.primary.map { it.label },
        )
    }

    @Test
    fun scheduleIsSecondaryNotPrimary() {
        assertFalse(PatsyNavigationContract.primary.any { it.key == "SCHEDULE" })
        assertEquals("SCHEDULE", PatsyNavigationContract.schedule.key)
    }
}
