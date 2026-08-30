package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LockedNavigationContractTest {
    private val source = File("src/main/java/com/patsy/app/MainActivity.kt").readText()

    @Test
    fun primaryNavigationMatchesLatestLockedProductRules() {
        assertTrue(source.contains("Screen.HOME to \"Home\""))
        assertTrue(source.contains("Screen.THYNK to \"THyNK\""))
        assertTrue(source.contains("Screen.CREATE to \"Create\""))
        assertTrue(source.contains("Screen.DMS to \"Patsy DMs\""))
        assertTrue(source.contains("Screen.PROFILE_HOME to \"Profile\""))
    }

    @Test
    fun chatAndScheduleRemainSecondaryRoutesNotPrimaryTabs() {
        val navFunction = source.substringAfter("@Composable fun AppNavigationBar").substringBefore("\n}")
        assertFalse(navFunction.contains("Screen.CHAT to"))
        assertFalse(navFunction.contains("Screen.SCHEDULE to"))
        assertTrue(source.contains("Screen.CHAT"))
        assertTrue(source.contains("Screen.SCHEDULE"))
    }

    @Test
    fun homeIsFeedAndThynkHasDedicatedStudioHome() {
        assertTrue(source.contains("fun HomeFeed("))
        assertTrue(source.contains("fun ThynkStudioHome("))
        assertTrue(source.contains("fun CreateNewHome("))
        assertTrue(source.contains("Screen.THYNK->ThynkStudioHome"))
        assertTrue(source.contains("Screen.CREATE->CreateNewHome"))
    }
}
