package com.patsy.app.ui.finaldesign

/**
 * SAVE MAIN APP / LOCK IN SAVE
 *
 * Source-of-truth constants for the FINAL Login, Set Password and Home page designs.
 * The visible Login/Set Password label remains "Remember Me" to match the approved images,
 * while the underlying authentication meaning is keepSignedIn/session restoration only.
 * The separate Patsy Remember Me paw remains reserved for user-approved saved content.
 *
 * The authenticated app navigation keeps the existing secure semantic destinations underneath,
 * while the visible chrome is now the five-button THyNK Panel supplied by the owner.
 */
object FinalVisualContract {
    const val logoSquareDp = 132
    const val logoSquareArgb = 0xFF000000
    const val charcoalArgb = 0xFF0E0E10
    const val cardArgb = 0xFF151518

    const val introCopy = "Hi, I'm Patsy, Your AI Pet Pal."
    const val footerCopy = "YOUR AI. YOUR WORKSPACE. YOUR CONTROL."

    const val loginPersistenceVisibleLabel = "Remember Me"
    const val loginPersistenceSemanticName = "keepSignedIn"
    const val loginPersistenceIsSavedContentMemory = false

    // Keep the existing semantic destinations stable for route authorization/security.
    val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")

    // Locked visible THyNK Panel order.
    const val navigationVisibleOnAllPages = true
    val primaryNavigationDisplayLabels = listOf(
        "THyNK-ME",
        "THyNK Chats",
        "THyNK-IN!",
        "THyNK Music",
        "THyNK-IT",
    )
    const val showThynkSecondaryLabel = false
    const val showCenterActionSecondaryLabel = false

    const val debugUsername = "patsytest"
    const val debugTemporaryPassword = "PatsyTest!2026"
}
