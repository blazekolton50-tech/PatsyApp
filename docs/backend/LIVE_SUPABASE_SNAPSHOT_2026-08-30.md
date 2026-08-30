# Live Supabase Snapshot — 2026-08-30

This is a handoff snapshot, not a migration file. It records the verified live backend state so an implementation agent does not rebuild existing infrastructure.

- Project ref: `tvtknwqcqbkecszvppub`
- Region: `eu-central-1`
- Status: `ACTIVE_HEALTHY`
- Postgres: 17.x
- Supabase security advisors: **no current security lints returned** on 2026-08-30.

## RLS-enabled public tables

`account_capabilities`, `account_requests`, `albums`, `app_feedback`, `calendar_items`, `comments`, `creator_jobs`, `device_registrations`, `dm_attachments`, `dm_members`, `dm_messages`, `dm_threads`, `media_assets`, `notifications`, `patsy_memories`, `post_likes`, `post_media`, `posts`, `profile_saved_media`, `profiles`, `projects`, `safety_reports`, `scheduled_content`, `scheduled_content_media`, `user_blocks`, `user_connections`, `user_consents`, `user_settings`.

## Private Storage buckets

`avatars`, `creator-temp`, `dm-media`, `shared-media`.

## Key schema facts from generated live types

- `account_capabilities`: age/capability booleans including social linking, creator studio, DMs, school tools, social and view-once media.
- `creator_jobs`: async creator job status, prompt/settings, output media link, cancel request and error code.
- `device_registrations`: platform/device identity, push token, notification state and last-seen.
- `dm_messages`: sender/thread/body plus `expires_at`.
- `media_assets`: device/cloud hybrid metadata including bucket/path/device URI, type, lock state and expiry.
- `patsy_memories`: private user memory JSON, memory type, lock state and timestamps.
- `projects`: user project metadata and lock state.
- `scheduled_content`: user scheduling records, target platforms, timezone, status and publish time.
- `user_settings`: DM/feed retention preferences, Remember Me, encrypted backup, analytics and notification controls.

## Safety rule

Do not infer policy correctness only from table names. Before modifying a subsystem, inspect its exact live policies/triggers/functions. Preserve RLS and private storage.
