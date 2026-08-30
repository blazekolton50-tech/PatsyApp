package com.patsy.app.auth.ui

/**
 * Copy and policy contracts for the locked Patsy authentication/account UI.
 *
 * This layer deliberately contains no provider or persistence implementation.
 * Authentication remains owned by the existing AuthGateway boundary.
 */
object PatsyGreetingResolver {
    private const val FIRST_INTRODUCTION = "Hi, I’m Patsy! Your AI Pet Pal!"

    private val returningGreetings = listOf(
        "Heeeyy! You’re back! Need anything?",
        "Hi! What we doing today?",
        "Morning! What are we getting into?",
        "You’re back! Wanna carry on where we left off?",
        "Heyyy — got an idea or are we winging it today?",
    )

    fun firstIntroduction(): String = FIRST_INTRODUCTION

    fun returningGreeting(index: Int): String {
        val safeIndex = Math.floorMod(index, returningGreetings.size)
        return returningGreetings[safeIndex]
    }
}

enum class RememberMePersistence {
    SESSION_ONLY,
}

/**
 * Login-screen Remember Me policy.
 *
 * This is intentionally distinct from Patsy's durable per-user memory/"Remember Me" feature:
 * the login control may persist only the authenticated session and must never persist a password.
 */
object RememberMePolicy {
    val persistence: RememberMePersistence = RememberMePersistence.SESSION_ONLY
    const val passwordStorageAllowed: Boolean = false
}

data class PatsyAccountMenuItem(
    val stableId: String,
    val title: String,
    val subtitle: String,
)

object PatsyAccountMenu {
    val items: List<PatsyAccountMenuItem> = listOf(
        PatsyAccountMenuItem(
            stableId = "my_account",
            title = "My Account",
            subtitle = "Username, password, info",
        ),
        PatsyAccountMenuItem(
            stableId = "security_privacy",
            title = "Security & Privacy",
            subtitle = "PIN, 2FA, devices, privacy",
        ),
        PatsyAccountMenuItem(
            stableId = "patsy_settings",
            title = "Patsy Settings",
            subtitle = "Patsy's personality & prefs",
        ),
        PatsyAccountMenuItem(
            stableId = "log_out",
            title = "Log Out",
            subtitle = "Sign out of your account",
        ),
    )
}
