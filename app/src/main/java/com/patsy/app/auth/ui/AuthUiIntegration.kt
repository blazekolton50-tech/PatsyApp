package com.patsy.app.auth.ui

import com.patsy.app.auth.AuthGateway
import com.patsy.app.auth.LoginSessionRetention
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.SignOutResult

enum class GreetingTimeOfDay {
    MORNING,
    DAY,
    EVENING;

    companion object {
        fun fromHour(hour: Int): GreetingTimeOfDay {
            require(hour in 0..23) { "hour must be 0..23, got $hour" }
            return when (hour) {
                in 5..11 -> MORNING
                in 12..16 -> DAY
                else -> EVENING
            }
        }
    }
}

data class GreetingContext(
    val username: String?,
    val completedVisits: Int,
    val timeOfDay: GreetingTimeOfDay,
    val hasContinuableWork: Boolean,
    val variantSeed: Int,
    val previousGreeting: String? = null,
) {
    init {
        require(completedVisits >= 0) { "completedVisits must never be negative" }
    }
}

object PatsyGreetingResolver {
    private const val FIRST_INTRO = "Hi, I’m Patsy! Your AI Pet Pal!"

    fun resolve(context: GreetingContext): String {
        if (context.completedVisits == 0) return FIRST_INTRO

        val name = context.username?.trim()?.takeIf { it.isNotEmpty() } ?: "there"
        val candidates = when {
            context.hasContinuableWork -> listOf(
                "You’re back, $name! Wanna carry on where we left off?",
                "Welcome back, $name! Should we pick up where we left off?",
            )
            context.timeOfDay == GreetingTimeOfDay.MORNING -> listOf(
                "Morning, $name! What are we getting into?",
                "Heeeyy, $name! You’re back! Need anything?",
                "Morning, $name! What are we making today?",
            )
            context.timeOfDay == GreetingTimeOfDay.DAY -> listOf(
                "Hey, $name! What are we working on today?",
                "You’re back, $name! What should we dive into?",
                "Hey, $name! Ready to create something?",
            )
            else -> listOf(
                "Heyyy, $name — got an idea or are we winging it tonight?",
                "Evening, $name! What are we getting into?",
                "Hey, $name! What are we making tonight?",
            )
        }

        val index = Math.floorMod(context.variantSeed, candidates.size)
        val selected = candidates[index]
        if (selected != context.previousGreeting || candidates.size == 1) return selected

        return candidates.drop(index + 1).plus(candidates.take(index + 1))
            .firstOrNull { it != context.previousGreeting }
            ?: selected
    }
}

interface RememberMePreferenceStore {
    suspend fun isSessionRestoreEnabled(): Boolean
    suspend fun setSessionRestoreEnabled(enabled: Boolean)
}

class RememberMeCoordinator(
    private val preferenceStore: RememberMePreferenceStore,
) {
    fun retentionFor(rememberMeEnabled: Boolean): LoginSessionRetention =
        if (rememberMeEnabled) {
            LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH
        } else {
            LoginSessionRetention.CURRENT_PROCESS_ONLY
        }

    suspend fun restoreSession(gateway: AuthGateway): SessionState {
        if (!preferenceStore.isSessionRestoreEnabled()) return SessionState.Anonymous

        val result = gateway.restoreSession()
        when (result) {
            is SessionState.Authenticated -> Unit
            is SessionState.Unavailable -> Unit
            else -> preferenceStore.setSessionRestoreEnabled(false)
        }
        return result
    }

    suspend fun recordSuccessfulLogin(rememberMeEnabled: Boolean) {
        preferenceStore.setSessionRestoreEnabled(rememberMeEnabled)
    }

    suspend fun signOut(gateway: AuthGateway): SignOutResult {
        try {
            return gateway.signOut()
        } finally {
            preferenceStore.setSessionRestoreEnabled(false)
        }
    }
}

data class PatsyAccountMenuItem(
    val stableId: String,
    val title: String,
    val subtitle: String,
)

object PatsyAccountMenu {
    val items = listOf(
        PatsyAccountMenuItem("my_account", "My Account", "Username, password, info"),
        PatsyAccountMenuItem("security_privacy", "Security & Privacy", "PIN, 2FA, devices, privacy"),
        PatsyAccountMenuItem("patsy_settings", "Patsy Settings", "Patsy’s personality & prefs"),
        PatsyAccountMenuItem("log_out", "Log Out", "Sign out of your account"),
    )

    init {
        require(items.size == 4) { "Patsy account menu is locked to four primary items" }
    }
}
