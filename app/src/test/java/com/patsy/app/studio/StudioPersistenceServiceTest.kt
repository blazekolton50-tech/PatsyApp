package com.patsy.app.studio

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class StudioPersistenceServiceTest {
    private val session = PublicSession(
        sessionId = "studio-session",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun loadReturnsOnlyServerBackedProjectStateLayersAndRevision() = runTest {
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session),
            transport = FakeStudioTransport(
                RemoteStudioPersistenceResult.Loaded(
                    state = StudioProjectPersistenceState(
                        projectId = "p1",
                        editorMode = "design",
                        canvasWidthPx = 1080,
                        canvasHeightPx = 1080,
                        durationMs = null,
                        fps = null,
                        autosaveRevision = 4,
                        lastAutosavedAtEpochMillis = 500L,
                    ),
                    layers = listOf(
                        StudioLayerPersistenceRecord(
                            id = "l1",
                            projectId = "p1",
                            parentLayerId = null,
                            layerType = "text",
                            name = "Title",
                            zIndex = 2,
                            startMs = null,
                            endMs = null,
                            isLocked = false,
                            isHidden = false,
                            opacity = 1.0,
                            transformJson = "{}",
                            cropJson = null,
                            styleJson = "{}",
                            effectsJson = "[]",
                            contentJson = "{\"text\":\"Hello\"}",
                        ),
                    ),
                    latestRevision = StudioRevisionRecord("r4", "p1", 4, "autosave", "{}", 500L),
                ),
            ),
        )

        val loaded = assertIs<StudioPersistenceResult.Loaded>(service.load(session, "p1"))
        assertEquals(4, loaded.state.autosaveRevision)
        assertEquals("Title", loaded.layers.single().name)
        assertEquals(4, loaded.latestRevision?.revisionNo)
    }

    @Test
    fun sessionMismatchFailsClosed() = runTest {
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session.copy(sessionId = "wrong")),
            transport = FakeStudioTransport(RemoteStudioPersistenceResult.Unavailable),
        )
        assertIs<StudioPersistenceResult.Unauthorized>(service.load(session, "p1"))
    }
}

private class FakeStudioSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access", "refresh", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeStudioTransport(
    private val result: RemoteStudioPersistenceResult,
) : StudioPersistenceTransport {
    override suspend fun load(accessToken: String, userId: String, projectId: String): RemoteStudioPersistenceResult = result
}
