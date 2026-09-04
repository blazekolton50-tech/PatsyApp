# Patsy App — Supabase Security & Integration Audit

Status: LIVE BACKEND EVIDENCE  
Date: 29 August 2026

## Verified live foundation
The connected Supabase project is healthy and contains RLS-enabled public tables covering profiles, settings, Patsy memories, projects/albums/media, Social post primitives, DMs, creator jobs, calendar, notifications, connections, blocks, safety reports, saved profile media, account export/deletion requests, consents, device registrations, feedback, scheduled content and server-derived account capabilities.

Private/internal tables exist for role/account access, reserved usernames, storage deletion queue, moderation cases/actions, security audit, notification delivery and account processing.

A case-insensitive unique username index exists on `lower(profiles.username)`.

Private Storage buckets exist for `avatars`, `creator-temp`, `dm-media` and `shared-media`; all are non-public and MIME/size constrained.

An active hourly cron invokes `private.cleanup_expired_rows()`.

One deployed Edge Function, `create-dm-thread`, is active with JWT verification. It validates the authenticated user, accepted connection, block state and both users' DM preferences before creating a direct thread/membership using server credentials.

## Private-table RLS advisory — reconciled
The schema inspection reports RLS disabled on eight `private.*` tables. A generic advisory warns that RLS-disabled tables can be dangerous when API roles have access.

Direct privilege verification was performed for `anon` and `authenticated`: both roles currently have **no USAGE on the private schema and no SELECT/INSERT/UPDATE/DELETE privileges on those private tables**. Therefore this audit did not observe direct client exposure of those tables.

RLS should still be considered defense-in-depth for internal tables if future grants/exposed-schema configuration change. Do not enable it blindly: internal triggers/workers must be tested against any new policies. Minimum safe posture is to preserve revoked client privileges, explicitly restrict default privileges, and add RLS only with tested server-role semantics.

## Security-definer review
Internal privileged functions are located in the private schema, and direct EXECUTE checks for `anon` and `authenticated` were false for the inspected functions. This is the intended shape for privileged trigger/worker helpers. Continue to pin `search_path`, validate caller/server context where applicable and avoid exposing privileged wrappers in public schemas.

## Public RLS findings
Public tables have RLS enabled and ownership policies generally use authenticated user identity. This is a strong starting posture.

### Social is not production-functional yet
Current `posts`, `comments`, `likes` and post-media policies are deliberately private/owner-centric. Post insert/update requires private visibility, and comments/likes are effectively limited to the owner/post-owner workflow. This means the database is safe-by-default but does **not** yet implement the locked community Patsy Social feed.

Production Social needs separately designed policies/views/RPCs that permit eligible community reads/interactions while enforcing blocks, account state, profile visibility, age compatibility, capability state and media ownership.

### DM hardening required
The thread-creation function has meaningful checks but should also enforce canonical `account_capabilities.can_use_dms`, verified age/peer compatibility and per-user/IP rate limits at the server boundary. Message insertion must also recheck current capability/account/block state so a thread created earlier cannot bypass a later restriction.

## Missing/unverified service workers
Database queue tables are not evidence that workers are running. This audit did not verify a deployed worker for:
- storage deletion queue processing;
- notification push/email delivery;
- account export/deletion processing;
- scheduled social publishing;
- creator image/video jobs;
- AI/search calls;
- email verification/recovery delivery.

These remain NOT VERIFIED / NOT CONFIGURED until a deployed function/service and successful end-to-end test exists.

## Analytics gap
An analytics opt-in setting exists, but no production analytics event model/pipeline was verified. Do not expose an OWNER analytics dashboard as factual usage analytics until privacy-minimized events and aggregation exist.

## Parental/protected safeguard gap
Verified-age tiers and derived capability fields exist, but a guardian/parental approval/linkage model was not found in the inspected public/private tables. Protected Mode can map `unknown` to fail-safe capabilities, but any requirement for parental approval, guardian linkage or re-verification needs a dedicated server model and policy.

## Recommended hardening order
1. Preserve private schema privilege denial and test default privileges.
2. Add automated RLS/authorization tests using anon, ordinary authenticated, protected/under-16, 16+ and OWNER/moderator cases.
3. Add canonical server capability checks to every Edge Function/write path.
4. Implement Social visibility safely rather than loosening existing owner-only RLS ad hoc.
5. Add rate limits and abuse controls for auth, DMs, Social posting, reports and AI/generation.
6. Deploy queue workers with idempotency/retries/audit and verify each one end-to-end.
7. Add guardian/re-verification model if parental approval is retained as a product requirement.
8. Keep OWNER authorization in private server data/capability grants and require stronger authentication for high-impact controls.
