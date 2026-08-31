package com.patsy.app.ui.final

data class FinalAuthVisualContract(
    val title: String,
    val speech: String,
    val fields: List<String>,
    val ownerSetupLabel: String,
    val ownerProfileTitle: String,
    val ownerProfileSubtitle: String,
    val rememberMeLabel: String,
    val primaryAction: String,
    val secondaryAction: String? = null,
    val forgotPasswordLabel: String,
)

data class FinalHomeVisualContract(
    val askPlaceholder: String,
    val greeting: String,
    val sectionTitles: List<String>,
    val feedTabs: List<String>,
    val navigationSemanticLabels: List<String>,
    val navigationVisibleLabels: List<String>,
)

data class FinalVisualColors(
    val backgroundArgb: Long,
    val surfaceArgb: Long,
)

/**
 * SAVE MAIN APP / LOCK IN SAVE — 2026-08-31.
 *
 * This contract mirrors the owner's three FINAL portrait references for Login,
 * Set Password and Home. Existing code must adapt to this contract; this contract
 * must not be silently changed to match older screenshots or preview branches.
 */
object FinalFirstThreeScreensContract {
    val login = FinalAuthVisualContract(
        title = "LOGIN",
        speech = "Hi, I'm Patsy,\nYour AI Pet Pal —\nlet's get you logged in!",
        fields = listOf("Username", "Email"),
        ownerSetupLabel = "Owner Profile login setup",
        ownerProfileTitle = "Blaze profile",
        ownerProfileSubtitle = "Owner • Blaze",
        rememberMeLabel = "Remember Me",
        primaryAction = "Login",
        forgotPasswordLabel = "Forgot Password?",
    )

    val setPassword = FinalAuthVisualContract(
        title = "SET PASSWORD",
        speech = "Let's set your password\nto keep Blaze safe!",
        fields = listOf("Set password", "Confirm password"),
        ownerSetupLabel = "Owner Profile login setup",
        ownerProfileTitle = "Blaze profile",
        ownerProfileSubtitle = "Owner • Blaze",
        rememberMeLabel = "Remember Me",
        primaryAction = "Set Password & Login",
        secondaryAction = "Back to Login",
        forgotPasswordLabel = "Forgot Password?",
    )

    val home = FinalHomeVisualContract(
        askPlaceholder = "Ask Patsy anything...",
        greeting = "Hey! What can I help with today? 💜",
        sectionTitles = listOf("CONTINUE DESIGNS", "TODAY", "CREATE POST"),
        feedTabs = listOf("For You", "Following", "Questions", "Latest"),
        navigationSemanticLabels = listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE"),
        navigationVisibleLabels = listOf("Home", "THYNK", "+", "PDMs", "Profile"),
    )

    val colors = FinalVisualColors(
        backgroundArgb = 0xFF08090CL,
        surfaceArgb = 0xFF111216L,
    )

    val referencePortraitPx: Pair<Int, Int> = 1024 to 1536

    // The FINAL screenshot visibly says "Remember Me". It remains a saved-content/memory
    // feature and must never become plaintext password storage or a password persistence switch.
    const val rememberMeStoresPassword: Boolean = false
}
