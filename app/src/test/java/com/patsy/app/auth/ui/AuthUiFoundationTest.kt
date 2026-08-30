package com.patsy.app.auth.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthUiFoundationTest {
    @Test
    fun firstIntroductionUsesApprovedIdentityPhrase() {
        assertEquals(
            "Hi, I’m Patsy! Your AI Pet Pal!",
            PatsyGreetingResolver.firstIntroduction(),
        )
    }

    @Test
    fun returningGreetingVariesWithoutRepeatingFirstIntroduction() {
        val first = PatsyGreetingResolver.returningGreeting(0)
        val second = PatsyGreetingResolver.returningGreeting(1)

        assertTrue(first.isNotBlank())
        assertTrue(second.isNotBlank())
        assertFalse(first == PatsyGreetingResolver.firstIntroduction())
        assertFalse(second == PatsyGreetingResolver.firstIntroduction())
        assertFalse(first == second)
    }

    @Test
    fun rememberMeNeverStoresPassword() {
        assertEquals(RememberMePersistence.SESSION_ONLY, RememberMePolicy.persistence)
        assertFalse(RememberMePolicy.passwordStorageAllowed)
    }

    @Test
    fun accountMenuKeepsLockedFourItemsInOrder() {
        assertEquals(
            listOf("my_account", "security_privacy", "patsy_settings", "log_out"),
            PatsyAccountMenu.items.map { it.stableId },
        )
    }
}
