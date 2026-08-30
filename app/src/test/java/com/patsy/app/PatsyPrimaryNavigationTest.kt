package com.patsy.app

import kotlin.test.Test
import kotlin.test.assertEquals

class PatsyPrimaryNavigationTest {
    @Test
    fun primaryNavigationUsesLockedLabelsAndOrder() {
        assertEquals(
            listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE"),
            PatsyPrimaryNavigation.items.map { it.label },
        )
        assertEquals(
            listOf(Screen.HOME, Screen.CHAT, Screen.CREATE, Screen.DMS, Screen.MORE),
            PatsyPrimaryNavigation.items.map { it.screen },
        )
    }
}
