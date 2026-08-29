# Patsy App — Full Backend/API Contract

Status: implementation contract  
Date: 29 August 2026

## General rules
- HTTPS only.
- Android sends a Supabase access token or equivalent authenticated bearer token; privileged secrets never ship in the APK.
- Every response includes a machine-readable status/error code and a request/correlation ID where practical.
- Mutating requests use idempotency keys for retry-safe operations where duplicates would be harmful.
- Age/capability and account-state checks happen server-side on every protected feature, not only in UI.
- OWNER/moderation operations require server authorization; high-impact operations should require recent authentication/MFA.
- Provider-dependent actions return `NOT_CONFIGURED`, `UNAVAILABLE`, `RATE_LIMITED`, `DENIED`, `FAILED`, `QUEUED`, or `COMPLETED`; never synthetic success.

## Auth and account
### POST /auth/register/start
Input: username, email, experience_mode, consent versions.  
Output: registration_attempt_id, normalized_username, username_available, next_step.  
Rules: case-insensitive uniqueness; reserved usernames checked; unknown age becomes Protected Mode.

### POST /auth/register/complete
Input: registration_attempt_id, password.  
Output: account_created, email_confirmation_state.  
Rules: password never logged; confirmation only reported queued/sent when provider confirms.

### POST /auth/login
Input: username_or_email, password, device metadata.  
Output: authenticated session, profile summary, capabilities.  
Rules: rate limit; generic invalid-credential response; audit suspicious attempts.

### POST /auth/recovery/request
Input: username_or_email.  
Output: accepted.  
Rules: non-enumerating response; provider state logged server-side.

### POST /auth/recovery/complete
Input: recovery token/code, new password.  
Output: success/session state.

### POST /auth/logout
Revokes/ends current device session where supported.

### GET /me
Returns profile, verified age tier, account state, server capabilities, email verification state and feature flags.

### DELETE /me / POST /account-requests
Creates deletion request. Export request is separate. Both must be observable by the user.

## Profiles and discovery
### GET /profiles/{username}
Returns only fields permitted by visibility, block, age and safety rules.

### PATCH /me/profile
Editable profile fields only. User cannot set role, verified age, moderation state or capabilities.

### GET /profiles/search?q=
Discovery respects profile visibility, blocks, age compatibility and account state.

### POST /connections / PATCH /connections/{id} / DELETE /connections/{id}
Connection requests and responses with age-safe peer interaction enforcement.

### POST /blocks/{user_id} / DELETE /blocks/{user_id}
Blocking immediately affects Social discovery/interactions and DMs.

## Capabilities / protected mode
### GET /me/capabilities
Canonical server response for `can_use_creator_studio`, `can_use_social`, `can_use_dms`, `can_link_social_accounts`, school tools, view-once requirement and verified age tier.

No client endpoint may self-edit privileged capability fields. Age verification/review is a protected server/OWNER workflow.

## Patsy AI and search
### POST /patsy/chat
Input: conversation_id?, message, optional project/context references.  
Output: answer, citations/tool results where applicable, memory proposals, provider state, request_id.  
Rules: per-user isolation, age-aware safety, input/output limits, rate limiting.

### POST /patsy/search
Input: query, search intent/context.  
Output: summarized results + source metadata.  
Rules: claim search only after provider success.

### POST /patsy/action/prepare
Turns natural language into a proposed action plan. Returns `requires_confirmation=true` for publishing, deletion, external posting, account/security and other consequential actions.

### POST /patsy/action/confirm
Executes a previously prepared action using a short-lived action token; revalidates auth/capabilities before execution.

### GET/POST/PATCH/DELETE /patsy/memories
User-scoped memory operations. Locked/approved memories require deliberate overwrite semantics rather than silent mutation.

## Creation Studio
### POST /creator/jobs
Types: image, video, edit, template. Returns job id and provider state.

### GET /creator/jobs/{id}
Returns draft/queued/running/completed/failed/cancelled plus output-media reference when complete.

### POST /creator/jobs/{id}/cancel
Requests cancellation where supported.

### POST /media/upload-ticket
Returns a short-lived upload target/path for the authenticated user's permitted bucket and media type.

### POST /media/complete
Registers a successfully uploaded object in `media_assets` after server-side ownership/type/size validation.

Generated media should be downloaded/saved to device promptly where practical; server storage follows expiry/lock rules.

## Patsy Social
### GET /social/feed
Cursor-paginated feed. Filters: allowed public/community content only; excludes blocks, restricted/suspended users and age-incompatible content/interactions.

### POST /social/posts
Supports status, question, image and template post metadata. Video can remain disabled initially. Server validates capability, visibility and media ownership.

### GET/PATCH/DELETE /social/posts/{id}
Owner edits/deletes; readers see only content allowed by feed rules.

### POST/DELETE /social/posts/{id}/likes
Age/block/account-state safe.

### GET/POST/DELETE /social/posts/{id}/comments
Comment policy must allow eligible community users, not only post owners.

### POST /reports
Creates safety report without exposing internal moderation notes.

## DMs
### POST /dm/threads
Existing deployed Edge Function `create-dm-thread` is the current foundation. It should additionally enforce canonical capability/age checks and rate limits before production.

### GET /dm/threads
Returns only memberships visible to current user.

### GET /dm/threads/{id}/messages
Cursor-paginated, membership checked, expired messages omitted.

### POST /dm/threads/{id}/messages
Validates membership, blocks, DM preferences, capabilities, age rules, body/attachment limits and view-once requirement. Persistence confirmation precedes client `sent` state.

### POST /dm/messages/{id}/view
For view-once media, records first authorized view and makes subsequent access unavailable according to policy.

### DELETE /dm/messages/{id}
Sender/user-permitted deletion; retention cleanup remains server-controlled.

## Calendar, scheduling and publishing
### GET/POST/PATCH/DELETE /calendar/items
User-owned calendar/reminder data with explicit timezone.

### GET/POST/PATCH/DELETE /scheduled-content
Draft/ready/cancelled user states editable by user; processing/published transitions server-controlled.

### POST /scheduled-content/{id}/approve
Final confirmation captures target platforms/accounts and approved body/media revision.

### POST /scheduled-content/{id}/publish-now
Revalidates tokens/account target, approval and capability; returns provider-specific truthful status.

Publishing adapters for Facebook/Instagram/TikTok stay server-side and are not considered complete until configured and tested.

## Notifications
### GET /notifications
User-owned in-app notifications.

### PATCH /notifications/{id}/read
Marks read.

### POST /devices/register
Registers/rotates push token tied to authenticated user/device; push token never exposed in public profile APIs.

Delivery queue worker handles bounded retry/backoff and invalid-token cleanup.

## Storage and retention
Private buckets: avatars, creator-temp, dm-media, shared-media. Object paths must be namespaced to immutable authenticated user IDs, not usernames.

Retention worker performs database expiration and storage deletion queue processing. DM default 3 days; Social media target 90 days unless valid lock/save rules apply; profile saved media target max 100 images and 30 videos.

## OWNER / moderation
### GET /owner/me
Returns role/capability grants only when server-authorized.

### GET /owner/health
Provider/backend health without returning secrets.

### GET /owner/audit
Paginated security/audit events; privilege required.

### GET/PATCH /owner/users/{id}
Restricted fields only; every moderation/account-state change writes audit history.

### GET/PATCH /owner/moderation/cases/{id}
Moderation workflow.

### POST /owner/moderation/actions
Warn/restrict/suspend/restore/age-review using explicit reason and actor identity.

### PATCH /owner/config
Feature/config changes are allowlisted; provider secrets use a secret manager/environment and are never returned to Android.

## Analytics
Analytics is opt-in. Add a privacy-minimized event endpoint/table before claiming analytics dashboards work. For minors, collect only coarse permitted usage categories and no behavioral advertising/profiling.

## Standard errors
`UNAUTHENTICATED`, `SESSION_EXPIRED`, `EMAIL_UNVERIFIED`, `FORBIDDEN`, `AGE_RESTRICTED`, `CAPABILITY_DENIED`, `BLOCKED`, `NOT_FOUND`, `CONFLICT`, `VALIDATION_FAILED`, `RATE_LIMITED`, `OFFLINE` (client), `PROVIDER_NOT_CONFIGURED`, `PROVIDER_UNAVAILABLE`, `TIMEOUT`, `INTERNAL_ERROR`.
