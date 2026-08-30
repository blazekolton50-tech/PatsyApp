package com.patsy.app.studio.project

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Page
import com.patsy.app.services.ServiceResult
import com.patsy.app.studio.editor.StudioLayer
import com.patsy.app.studio.media.StudioMediaReference
import com.patsy.app.studio.sizing.CanvasSize
import com.patsy.app.studio.timeline.StudioTimeline

enum class StudioProjectType { IMAGE, VIDEO, DOCUMENT, PRESENTATION, SCHEDULE, TODO, OTHER }
enum class ProjectVisibility { PRIVATE, PERSONAL_TEMPLATE, COMMUNITY_TEMPLATE_PENDING_REVIEW }
enum class ProjectDirtyState { CLEAN, DIRTY }
enum class ProjectSyncState { LOCAL_ONLY, SYNC_PENDING, SYNCED, CONFLICT, UNAVAILABLE }

data class StudioProject(
    val projectId: String,
    val ownerUserId: String,
    val title: String,
    val projectType: StudioProjectType,
    val schemaVersion: Int,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val canvasSize: CanvasSize,
    val layers: List<StudioLayer>,
    val timeline: StudioTimeline? = null,
    val assetReferences: List<StudioMediaReference> = emptyList(),
    val thumbnailReference: StudioMediaReference? = null,
    val visibility: ProjectVisibility = ProjectVisibility.PRIVATE,
    val revisionToken: String? = null,
    val dirtyState: ProjectDirtyState = ProjectDirtyState.DIRTY,
    val syncState: ProjectSyncState = ProjectSyncState.LOCAL_ONLY,
)

/**
 * A project and the artifacts produced from it are intentionally different records.
 * Exporting, retaining or publishing never mutates an editable StudioProject into a different kind of object.
 */
sealed interface ProjectArtifact {
    val artifactId: String
    val projectId: String
    val ownerUserId: String
    val createdAtEpochMs: Long
}

data class EditableProjectArtifact(
    override val artifactId: String,
    override val projectId: String,
    override val ownerUserId: String,
    override val createdAtEpochMs: Long,
    val revisionToken: String? = null,
) : ProjectArtifact

data class ExportedMediaArtifact(
    override val artifactId: String,
    override val projectId: String,
    override val ownerUserId: String,
    override val createdAtEpochMs: Long,
    val mediaReference: StudioMediaReference,
) : ProjectArtifact

data class RetainedMediaArtifact(
    override val artifactId: String,
    override val projectId: String,
    override val ownerUserId: String,
    override val createdAtEpochMs: Long,
    val mediaReference: StudioMediaReference,
    val retainedUntilEpochMs: Long? = null,
) : ProjectArtifact

data class PersonalTemplateArtifact(
    override val artifactId: String,
    override val projectId: String,
    override val ownerUserId: String,
    override val createdAtEpochMs: Long,
) : ProjectArtifact

data class CommunityTemplateArtifact(
    override val artifactId: String,
    override val projectId: String,
    override val ownerUserId: String,
    override val createdAtEpochMs: Long,
    val reviewState: CommunityTemplateReviewState = CommunityTemplateReviewState.PENDING,
) : ProjectArtifact

enum class CommunityTemplateReviewState { PENDING, APPROVED, REJECTED }

interface StudioProjectRepository {
    suspend fun create(context: AuthenticatedContext, project: StudioProject): ServiceResult<StudioProject>
    suspend fun load(context: AuthenticatedContext, projectId: String): ServiceResult<StudioProject>
    suspend fun save(context: AuthenticatedContext, project: StudioProject): ServiceResult<StudioProject>
    suspend fun list(context: AuthenticatedContext, cursor: String? = null): ServiceResult<Page<StudioProject>>
    suspend fun delete(context: AuthenticatedContext, projectId: String): ServiceResult<Unit>
}
