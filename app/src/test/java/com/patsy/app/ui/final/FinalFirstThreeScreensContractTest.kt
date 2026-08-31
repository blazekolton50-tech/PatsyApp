package com.patsy.app.ui.final

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FinalFirstThreeScreensContractTest {
    @Test
    fun loginScreenMatchesFinalApprovedCopyAndStructure() {
        assertEquals("LOGIN", FinalFirstThreeScreensContract.login.title)
        assertEquals("Hi, I'm Patsy,\nYour AI Pet Pal —\nlet's get you logged in!", FinalFirstThreeScreensContract.login.speech)
        assertEquals(listOf("Username", "Email"), FinalFirstThreeScreensContract.login.fields)
        assertEquals("Owner Profile login setup", FinalFirstThreeScreensContract.login.ownerSetupLabel)
        assertEquals("Blaze profile", FinalFirstThreeScreensContract.login.ownerProfileTitle)
        assertEquals("Owner • Blaze", FinalFirstThreeScreensContract.login.ownerProfileSubtitle)
        assertEquals("Remember Me", FinalFirstThreeScreensContract.login.rememberMeLabel)
        assertEquals("Login", FinalFirstThreeScreensContract.login.primaryAction)
        assertEquals("Forgot Password?", FinalFirstThreeScreensContract.login.forgotPasswordLabel)
    }

    @Test
    fun setPasswordScreenMatchesFinalApprovedCopyAndStructure() {
        assertEquals("SET PASSWORD", FinalFirstThreeScreensContract.setPassword.title)
        assertEquals("Let's set your password\nto keep Blaze safe!", FinalFirstThreeScreensContract.setPassword.speech)
        assertEquals(listOf("Set password", "Confirm password"), FinalFirstThreeScreensContract.setPassword.fields)
        assertEquals("Set Password & Login", FinalFirstThreeScreensContract.setPassword.primaryAction)
        assertEquals("Back to Login", FinalFirstThreeScreensContract.setPassword.secondaryAction)
        assertEquals("Forgot Password?", FinalFirstThreeScreensContract.setPassword.forgotPasswordLabel)
    }

    @Test
    fun homeScreenMatchesFinalApprovedSectionsAndBottomNavigation() {
        assertEquals("Ask Patsy anything...", FinalFirstThreeScreensContract.home.askPlaceholder)
        assertEquals("Hey! What can I help with today? 💜", FinalFirstThreeScreensContract.home.greeting)
        assertEquals(
            listOf("CONTINUE DESIGNS", "TODAY", "CREATE POST"),
            FinalFirstThreeScreensContract.home.sectionTitles,
        )
        assertEquals(
            listOf("For You", "Following", "Questions", "Latest"),
            FinalFirstThreeScreensContract.home.feedTabs,
        )
        assertEquals(
            listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE"),
            FinalFirstThreeScreensContract.home.navigationSemanticLabels,
        )
        assertEquals(
            listOf("Home", "THYNK", "+", "PDMs", "Profile"),
            FinalFirstThreeScreensContract.home.navigationVisibleLabels,
        )
    }

    @Test
    fun finalScreensUseLockedVisualLanguageAndDoNotMakeRememberMePasswordStorage() {
        assertEquals(0xFF08090CL, FinalFirstThreeScreensContract.colors.backgroundArgb)
        assertEquals(0xFF111216L, FinalFirstThreeScreensContract.colors.surfaceArgb)
        assertEquals(1024 to 1536, FinalFirstThreeScreensContract.referencePortraitPx)
        assertFalse(FinalFirstThreeScreensContract.rememberMeStoresPassword)
    }
}
