# Patsy MAIN APP — Safe Storage + Remember Me Lock

Status: **SAVE MAIN APP / SAVE LOCK IN — 2026-08-30**

This document extends the current locked Patsy product rules. It must not be used to redesign the approved UI, navigation, Rive ABI or THyNK visual system.

## Production authority

- Supabase is the production authentication, PostgreSQL, RLS, realtime and private-object-storage authority.
- GitHub is the source-of-truth for application code, migrations, tests and implementation evidence.
- Device-first storage is preferred when cloud persistence is unnecessary.
- Google Drive / expanded Google storage is an owner backup/archive destination only, not live production user storage or authentication authority.
- Replit is prototype/recovery/support only.

## Storage zones

1. **Device local** — drafts, editing proxies, thumbnails, generated media and cache where cloud persistence is unnecessary.
2. **Temporary cloud** — applicable feed/media with server-side `created_at` and `expires_at`; baseline retention is 90 days.
3. **Remember Me durable semantic memory** — compact, canonical, user-approved memory records. Do not put raw image/video binaries in memory rows.
4. **Durable saved/locked media** — eligible media explicitly retained beyond temporary policy.
5. **App-owned asset library** — THyNK templates/effects/music/assets stored once and referenced by stable ID/version.
6. **Owner archive/backup** — encrypted/versioned backups and app-owned master archives.

## Remember Me

Remember Me and saved/locked media are related but separate concepts.

A durable memory record should be scoped to the authenticated immutable user ID and include the minimum useful structured information, such as:

- `memory_id`
- `owner_user_id`
- category/type
- compact canonical value
- user-visible summary
- status
- `created_at`
- `updated_at`
- revision/version
- provenance/source pointer where required
- optional authorised `media_reference_id`
- privacy/sensitivity handling flags where required

Never store passwords, auth tokens or secrets as Patsy Memory.

Equivalent memories should merge where safe. Contradictory updates require deterministic conflict handling or user confirmation where meaning could change. Cross-device sync must use server-authoritative revisions. User correction/deletion must propagate safely.

## Temporary 90-day content

- The server sets `created_at` and `expires_at`.
- Derived thumbnails/previews inherit the parent lifecycle.
- Cleanup is a scheduled backend responsibility, not a client-only timer.
- Before physical deletion, the backend must re-check durable references.
- Expired unreferenced content is removed from indexes/derivatives/object storage/database references in a safe order.
- Keep only minimal deletion audit metadata; do not retain deleted private content.

## Saved/locked media promotion

Saving/locking eligible temporary media should normally be an **atomic lifecycle/reference change**, not download-copy-reupload.

1. Authenticate and authorise the current user.
2. Lock/read the logical media reference.
3. Enforce account-domain and quota rules.
4. Change lifecycle to durable saved/locked and remove ordinary temporary expiry.
5. Update reference/lifecycle metadata.
6. Commit atomically.
7. Only then show the durable saved state in UI.

If any step fails, roll back. Do not leave half-promoted media.

When removing a save/lock, check all active project/media/memory references before physical deletion. A semantic Remember Me record may remain meaningful without retaining its original media binary; handle that reference separately.

## Current retention/product constraints

- Applicable feed/media temporary retention baseline: **90 days**.
- DM default retention baseline: **3 days**.
- Profile locked-media target: **100 pictures + 30 videos**.
- Generated media should prefer device storage where practical.

## Compression and minimisation

- Exact-content deduplication may use SHA-256 or equivalent strong hashes, but must never leak another user's ownership/existence.
- Separate physical media objects from logical user/project references.
- Images: optimized master, thumbnails, modern compatible formats, strip unnecessary metadata/location, avoid repeated lossy recompression.
- Video: Media3/device-side transcode only when needed; use suitable resolution/bitrate/FPS/codec profiles, posters/previews and proxies.
- Audio: high-quality masters only where required, compressed delivery versions and shared app-owned IDs.
- THyNK projects: structured document/object model, stable asset references, incremental/delta saves, compact snapshots, bounded undo/history and named checkpoints.
- Patsy Memory: candidate → normalize → deduplicate → compact canonical durable record.
- Delete abandoned temporary uploads, renders, derivatives and orphan blobs safely.

## Security requirements

- Deny by default.
- Every private object requires server-side object-level authorization.
- User A cannot access User B's private Patsy Memory, media, DMs, projects, settings or account records by changing usernames, UUIDs, URLs, file paths or API parameters.
- Private storage and signed access must remain authorised and time-bounded.
- Under-16 and 16+ ordinary account domains are mutually isolated at database/storage/realtime/search/API boundaries.
- Unknown/uncertain age stays in the protected child domain until an approved server-side transition.
- Owner capability is a separate server-authorised path; never a username, local boolean or hidden route.
- Logout/account switch clears private local cache.
- No production secrets in the APK.
- Debug/preview authentication must not become a production bypass.

## Required implementation order

1. Audit current schema/migrations/client adapters.
2. Finalise authoritative account-domain state.
3. Add owner-only Remember Me schema/RLS.
4. Separate physical media-object metadata from logical per-user references.
5. Add server-side 90-day lifecycle fields.
6. Implement atomic Save/Lock promotion/demotion and quota checks.
7. Add compression/checksum/dedup pipeline.
8. Add retention/orphan cleanup jobs.
9. Add Android local cache/storage boundaries and WorkManager sync/retry.
10. Add cross-device memory/media sync and conflict handling.
11. Add storage usage/cleanup UI/API.
12. Add account deletion and backup/restore flows.
13. Run Alice/Bob/ChildA/ChildB/Owner direct API/storage/realtime attack tests.
14. Run cross-device and restore tests before calling storage production-ready.

## Truthfulness gate

This document defines the locked architecture; it does **not** prove production implementation. Every item must be marked DONE / PARTIAL / BLOCKED from live repository/backend/test evidence.