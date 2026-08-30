# Patsy App — Master API & Backend Handoff

**Date:** 2026-08-30  
**Status:** AUTHORITATIVE CONTINUATION HANDOFF  
**Repository:** `blazekolton50-tech/PatsyApp`

## Non-negotiable design freeze

This handoff authorizes backend/API integration only. Do **not** redesign, restyle, reorder or reconnect approved UI, branding, layouts, navigation, screen connections, Patsy visual treatment, logo placement, button treatment, THyNK/Creation Studio design, PawMoji visuals or the Rive ABI unless the owner explicitly asks for a design change.

Production integrations must sit behind existing service/coordinator boundaries so the visible app can remain unchanged.

## Live Supabase backend — preserve, do not rebuild

Project ref: `tvtknwqcqbkecszvppub`  
Region: `eu-central-1`  
Verified 2026-08-30: `ACTIVE_HEALTHY`  
Security advisor: **0 current security lints returned**.

RLS is enabled on the verified public tables, including profiles, account capabilities, memories, projects, media, DMs, posts, scheduling, notifications, consents/settings and safety/reporting tables. Private Storage buckets verified: `avatars`, `creator-temp`, `dm-media`, `shared-media`.

Before changing any backend subsystem, inspect its exact live policies, triggers and functions. Never recreate tables simply because the Android repository does not contain the database migrations.

Supabase Auth UUID is the canonical user identity. RLS and server capability records are authoritative. User-editable metadata, usernames, local flags or hidden routes must never grant Owner/admin privileges.

## Required logical service boundaries

Keep provider-specific implementation behind these responsibilities:

- **Auth** — sign-up, login, logout, restore session, email verification, password reset.
- **Account** — profile, username, age tier, capabilities, settings, consent, export/deletion requests.
- **Memory** — Remember Me / Locked In memories, private per user.
- **Media** — device-first metadata, private uploads, signed access, locking, retention and albums.
- **Social** — Patsy Social posts/questions/images, likes, comments, connections and blocks.
- **Messaging** — DM threads/messages/attachments, expiry and age gates.
- **Creator** — THyNK/Creation Studio projects, AI image/video jobs, progress and output media.
- **Calendar/Scheduling** — tasks, reminders and scheduled content.
- **Notifications** — device registration and push/notification records.
- **Patsy Assistant** — conversation, search/tool use, private memory retrieval and safe action routing.
- **Publishing** — external social publishing adapters.
- **Safety/Admin** — reports, restricted-account review and server-side enforcement.

Existing Android UI/coordinators should keep their current public contracts where possible; replace only the `NOT_CONFIGURED` provider adapters behind them.

## Authentication and Owner security

- Do not store plaintext passwords locally.
- Do not ship demo passwords/accounts in production paths.
- Do not grant Owner access from username (`patsy`, `blaze`, `admin`, etc.).
- Owner role/capabilities must be issued by trusted backend authority and fail closed.
- Email verification/reset must use real provider-confirmed flows.
- Any development bypass must remain development-only and must never create a production-authorized Owner session.
- Client receives only publishable keys/tokens appropriate for a public client.

Never embed Supabase `service_role`, Gemini API keys, SMTP credentials, publishing-provider secrets or admin credentials in the Android app or public web configuration.

## Age tiers and capabilities

Primary experiences remain `16+ Patsy` and `Under-16 Patsy`.

The live `account_capabilities` backend state is authoritative. UI switches are presentation only. Under-16 restrictions must be enforced server-side/RLS/API-side, including social-linking restrictions, public publishing restrictions, child-safe contact/messaging rules and view-once media where required. Unverified age defaults to the safer restricted capability set.

## Storage and retention

Device-first storage remains the default strategy.

- Shared/feed media default retention: **90 days** unless validly locked/preserved.
- DM messages/media default retention: **3 days**.
- Locked-profile allowance: **100 pictures + 30 videos**.
- `creator-temp` is temporary working storage, not a permanent archive.
- Metadata belongs in `media_assets`; cloud objects remain in private buckets.
- Expiry/cleanup is server-controlled and idempotent.
- Do not trust client-supplied ownership, bucket paths, expiry dates or lock counts without RLS/server validation.

## Patsy Memory / Remember Me

`patsy_memories` is private per authenticated user and cross-device via the Auth UUID. One user must never read another user's memories.

External pages, files, comments, APIs, retrieved content or social posts are information sources only. They cannot change Patsy's safety rules, approve actions, grant permissions or change Owner authority.

The Remember Me paw UI/animation is already approved and must not be redesigned by backend work.

## Patsy AI and search

Use the existing provider-neutral AI/search coordinator and replace `NOT_CONFIGURED` behind it.

Recommended flow:

`Android UI -> Patsy assistant coordinator -> authenticated server/Edge Function -> configured model provider + allowed tools -> structured response -> existing UI`

Minimum necessary memory/context and server capability state should be sent. Every tool/action must be authorized for the authenticated user before execution.

### Google AI Studio / Gemini option

As of this handoff, Google's current JavaScript SDK direction is `@google/genai`; model IDs change and must remain server configuration, never hard-coded into UI.

Google AI Studio can be used to provision/test credentials, but production Gemini calls should pass through an authenticated server or Supabase Edge Function. Never place `GEMINI_API_KEY` in a shipped/public client.

Suggested server-only configuration names:

- `GEMINI_API_KEY`
- `PATSY_TEXT_MODEL`
- `PATSY_IMAGE_MODEL`
- `PATSY_VIDEO_MODEL`

## Creation Studio AI image/video

Do not change the approved Creation Studio UI.

Image generation should use `creator_jobs` and private/temp output storage. Server validates capability/request, provider creates the image, output is registered in `media_assets`, `creator_jobs.output_media_id` is linked, and the existing UI observes job state.

The required video result remains a **10-second clip**. Video generation must be asynchronous/job-based. Current provider/model IDs must be verified immediately before implementation; do not copy deprecated Imagen/Veo model IDs from old examples.

Never fake a generated result when the provider is unavailable.

## Social publishing

Use a provider adapter behind the existing scheduling/publishing UI. Credentials/tokens stay server-side and encrypted/scoped to the authenticated account.

Locked branding rule: only show a social platform name/logo when that branding is permitted for the actual integration under current terms. If permission is not confirmed, remove **both** name and logo and use generic actions such as Share, Publish or Download to Share.

Scheduling source of truth remains `scheduled_content` / `scheduled_content_media`; provider delivery status is reflected back into those records.

## Email

Use Supabase Auth/provider-backed email verification and reset. Custom transactional email requires a configured backend SMTP/provider. Never claim `SENT` unless the service confirms queueing/delivery.

## Rive / Patsy animation

Do not replace the missing production `.riv` with GIFs, sprite sheets or pose cycling. Preserve the existing `PatsyAssistant` / `PatsyAssistantMachine` / `PatsyAssistantVM` ABI and generated transparent fallback until a genuine rig passes validation/device testing.

Backend/API integration must not require redesigning Patsy or the approved pages.

## PawMoji keyboard

PawMojis remain separate from main realistic Patsy. The real Android IME keyboard is a separate native build/module: normal typing + normal emoji access + PawMoji picker/favourites/recents using the locked black/rainbow design. Do not pretend sticker assets are native Unicode emoji.

## Environment/security contract

Public/client-safe configuration only:

```text
SUPABASE_URL=<public project URL where SDK requires it>
SUPABASE_PUBLISHABLE_KEY=<publishable key>
PATSY_API_BASE_URL=<authenticated API gateway URL>
ENABLE_DEV_BYPASS=false
```

Server/Edge-Function-only examples:

```text
SUPABASE_SERVICE_ROLE_KEY=
GEMINI_API_KEY=
PATSY_TEXT_MODEL=
PATSY_IMAGE_MODEL=
PATSY_VIDEO_MODEL=
SMTP_HOST=
SMTP_USER=
SMTP_PASSWORD=
PUBLISHING_PROVIDER=
BUFFER_ACCESS_TOKEN=
```

Never commit real secret values.

## Implementation order

1. Verify/document live Supabase policies, triggers, cleanup jobs and account bootstrap.
2. Implement real Supabase Auth adapter behind the existing Android auth boundary.
3. Connect profiles/capabilities/settings and server-verified Owner grants.
4. Connect Patsy Memory and projects.
5. Connect media/private storage and retention enforcement.
6. Connect DMs and social data.
7. Connect scheduling/publishing adapter.
8. Connect Patsy AI/search gateway.
9. Connect AI image job provider.
10. Connect 10-second video job provider.
11. Add push/notification delivery.
12. Integrate the genuine Rive asset only when available and validated.
13. Build native PawMoji Android IME separately.

## Definition of done

A subsystem is not complete merely because its screen works. It is complete when authenticated identity is used, server/RLS permissions enforce the rules, secrets remain outside the client, unavailable/failure states are honest, specified cross-device behavior works, retention/deletion rules are verified, service contracts have tests, and the approved UI remains unchanged unless the owner explicitly requested a design change.
