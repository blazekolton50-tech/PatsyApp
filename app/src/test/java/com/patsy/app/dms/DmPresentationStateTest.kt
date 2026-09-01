package com.patsy.app.dms

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DmPresentationStateTest {
    private val threads = listOf(
        DmThreadSummary("a", "A", "Hello", unreadCount = 2, isGroup = false, archived = false),
        DmThreadSummary("b", "B Group", "Hi", unreadCount = 0, isGroup = true, archived = false),
        DmThreadSummary("c", "C", "Archived", unreadCount = 1, isGroup = false, archived = true),
    )

    @Test
    fun narrowLayoutUsesStackedInboxAndConversation() {
        assertEquals(DmLayoutMode.STACKED, dmLayoutForWidth(430))
        assertEquals(DmLayoutMode.STACKED, dmLayoutForWidth(719))
    }

    @Test
    fun expandedLayoutUsesSingleSplitView() {
        assertEquals(DmLayoutMode.SPLIT_VIEW, dmLayoutForWidth(720))
        assertEquals(DmLayoutMode.SPLIT_VIEW, dmLayoutForWidth(1024))
    }

    @Test
    fun unreadFriendGroupAndArchivedFiltersStayDeterministic() {
        assertEquals(listOf("a"), filterDmThreads(threads, DmFilter.UNREAD, "").map { it.id })
        assertEquals(listOf("a"), filterDmThreads(threads, DmFilter.FRIENDS, "").map { it.id })
        assertEquals(listOf("b"), filterDmThreads(threads, DmFilter.GROUPS, "").map { it.id })
        assertEquals(listOf("c"), filterDmThreads(threads, DmFilter.ARCHIVED, "").map { it.id })
    }

    @Test
    fun searchMatchesParticipantOrRealPreviewTextOnly() {
        assertEquals(listOf("b"), filterDmThreads(threads, DmFilter.ALL, "group").map { it.id })
        assertEquals(listOf("a"), filterDmThreads(threads, DmFilter.ALL, "hello").map { it.id })
    }

    @Test
    fun callAndVideoStayNotConfiguredByDefault() {
        val state = DmConversationCapabilities()

        assertEquals(DmProviderCapability.NOT_CONFIGURED, state.audioCall)
        assertEquals(DmProviderCapability.NOT_CONFIGURED, state.videoCall)
        assertFalse(state.canStartAudioCall)
        assertFalse(state.canStartVideoCall)
    }

    @Test
    fun selectingThreadDoesNotFabricateReadOrMessageState() {
        val state = DmPresentationState(selectedThreadId = null)
        val selected = state.selectThread("a")

        assertEquals("a", selected.selectedThreadId)
        assertTrue(selected.locallyAcknowledgedThreadIds.isEmpty())
    }
}
