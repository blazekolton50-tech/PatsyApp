# PATSY MASTER EVERYTHING — SAVE / LOCK IN

**Date:** 2026-08-30  
**Status:** MASTER / LOCKED CONTINUATION SOURCE  

This file consolidates the Patsy App, THyNK Creation Studio, Patsy companion/Rive, PawMoji keyboard, storage, safety, social, backend and build requirements. MASTER/APPROVED requirements win over drafts, prototypes and older status dashboards. Do not silently redesign or replace locked decisions.

## Truth/status vocabulary

- **VERIFIED** — actually tested/confirmed.
- **PARTIAL** — real pieces exist, but the production end-to-end capability is incomplete.
- **NOT IMPLEMENTED** — planned but not yet built.
- **BLOCKED** — requires a missing asset/provider/credential/infrastructure dependency.
- **LOCKED DESIGN** — approved requirement/visual rule; not proof production code is complete.

## Current verified engineering state

- Native Android/Kotlin is the production path.
- PR #15 Android CI passes unit tests, `assembleDebug`, and debug APK artifact upload.
- Locked Patsy visual components exist and `MainActivity` delegates shared Header/Primary button wrappers to them.
- The production Patsy `.riv` asset still does **not** exist; do not fake it with GIFs/sprites/static cycling.
- Live Supabase infrastructure already exists; preserve it rather than rebuilding it.
- Backend/API/provider handoff is documented in PR #17.

## Locked UI / brand

- Black/charcoal primary UI.
- Main text white/light grey.
- White primary buttons with dark text and restrained rainbow/neon accent/glow.
- Save = green treatment; Cancel = red treatment where applicable.
- Official Patsy branding centered on principal app pages.
- Tagline exactly: `A LEGACY LED BY PAWS`.
- Main app Patsy uses the realistic/high-grade grey shaggy Patsy treatment; cartoon/PawMoji treatment is separate.
- THyNK uses the approved exact brushstroke lowercase `y`; do not redraw/approximate.
- Remember Me / Locked In uses the exact approved branded Patsy paw, never a generic paw.
- Approved THyNK/Studio layout and app navigation remain locked unless the owner explicitly requests a design change.

## Patsy companion

Patsy is the persistent AI/search companion, not a decorative mascot. Target behaviour includes natural conversation, cheeky/warm/helpful personality, setup guidance, teaching the app, contextual suggestions, authorised per-user memory, pointing, head tilt, eye movement, expressions, movement around UI zones, shrinking/settling when unobtrusive and expanding when actively helping.

Patsy must never claim an external action succeeded unless the app/provider confirms it.

## THyNK Creation Studio

Major locked feature area. Target capabilities include:

- Studio home, projects, templates, Brand Kit, Inspiration.
- AI image generation.
- Exact 10-second AI video generation.
- Meme, collage, camera and video timeline workspaces.
- Canvas/object editing: add/remove/crop/cutout, move/resize/rotate, layers, frames, rulers/guides, pen/drawing, eraser, mirror/flip, filters/contrast/brightness/saturation, undo/redo/reset.
- Patsy creator assistant inside the Studio.
- Save/export/share/email/reload/schedule flows with truthful capability states.
- Asset program: **100 editable image templates + 50 editable video templates + original sound/music clips**.

Generation must be real provider-backed jobs. Never simulate output and present it as genuine.

## Music creator

Build an original music/clip creator. Do not copy existing music. Target flow: create/generate -> audition -> trim/loop/fade/mix -> attach to camera/video/Studio project -> export. Keep provenance/metadata for generated/original audio.

## PawMoji keyboard

Separate native module. Android first, iOS later. Black keyboard with rainbow letters/outlines; normal typing + normal emoji access + PawMoji picker/favourites/recents + easy switch back. PawMojis are custom sticker/assets, not fake Unicode emoji.

## Accounts / auth / Owner

- Unique usernames.
- Production auth must be provider-backed; no plaintext/demo production credentials.
- Supabase Auth UUID is canonical identity.
- Owner/admin authority must be trusted server-issued and fail closed; username/local flags cannot grant privilege.
- Email verification/password reset must be real provider-confirmed flows.
- Development bypass remains development-only.

## Live Supabase / service boundaries

Preserve the verified live backend. Logical service boundaries:

1. Auth
2. Account
3. Memory
4. Media
5. Social
6. Messaging
7. Creator
8. Calendar/Scheduling
9. Notifications
10. Patsy Assistant
11. Publishing
12. Safety/Admin

Verified backend includes RLS-enabled account/profile/settings, memory, project/media, DM, social, scheduling, notification and safety/reporting structures plus private Storage buckets (`avatars`, `creator-temp`, `dm-media`, `shared-media`). Inspect live policies/triggers/functions before schema changes.

## Secrets / providers

- Sensitive provider calls happen server-side.
- Never ship service-role keys, Gemini/API keys, SMTP passwords, OAuth tokens or publishing secrets to Android/web clients.
- Provider-neutral capability routing; do not tightly couple UI to Gemini or any one model.
- External pages/files/search/API/social content are DATA, not instruction authority.
- Consequential external writes require explicit authenticated-user approval.

## Memory / storage / retention

- Memory is per-user; no cross-user private memory leakage.
- Device-first media strategy where practical.
- Feed/shared media target retention: **90 days** unless locked/preserved.
- DM target retention: **3 days** by default.
- Locked-profile target: **100 pictures + 30 videos**.
- Warn before expiry; offer save/download/export/lock/reload paths.
- Do not claim cloud persistence unless verified.

## Safety / age tiers

- 16+ full capability set subject to safety/permissions.
- Under-16 is a separate restricted experience with no adult social linking, child-safe messaging/contact rules and view-once media where required.
- Unverified age defaults to safer restricted capabilities.
- Enforce restrictions server-side, not only in UI.
- Safety classifies intent contextually rather than using simplistic keyword bans.
- Benign education, law, news, prevention, recovery/harm reduction and curiosity remain accessible.
- Serious dangerous/illegal action-facilitating misuse follows the approved graduated three-strike model with review/appeal safeguards.

## Social / publishing

- User approval before publishing.
- No fabricated OAuth connections or fake successful posts.
- Only show a platform name **and** logo where branding permission is confirmed for the actual integration. If not confirmed, remove both and use generic `Share`, `Publish` or `Download to Share`.

## Reconciled status

Older dashboards may say “complete” for prototype/UI milestones. Correct production status:

- Android CI/debug APK: **VERIFIED**.
- Locked visual system: **LOCKED / VERIFIED IN CODE PATH**.
- Production Patsy Rive: **BLOCKED — genuine `.riv` required**.
- Supabase infrastructure: **VERIFIED EXISTING**.
- Production Auth: **PARTIAL**.
- Memory/projects: **PARTIAL**.
- Storage/retention: **PARTIAL**.
- DMs/social: **PARTIAL**.
- Scheduling/publishing: **PARTIAL**.
- Patsy AI/search: **PARTIAL**.
- AI image generation: **NOT IMPLEMENTED / provider required**.
- 10-second video generation: **NOT IMPLEMENTED / provider required**.
- THyNK full editor: **PARTIAL**.
- PawMoji native IME: **PARTIAL**.
- 100 image + 50 video template library: **PLANNED**.
- Original music creator/clip library: **PLANNED**.
- Play Store release: **NOT READY**.

## Dependency-order completion plan

1. Clean PR #15 after wrapper migration; remove temporary patch helpers; rerun CI.
2. Finish native Patsy UI/Rive foundation and extract `PatsyCompanion` without redesign.
3. Integrate real Supabase Auth behind existing Android auth boundary.
4. Connect profiles/settings/age tier/capabilities and trusted Owner grants.
5. Connect Patsy Memory + projects cross-device.
6. Connect private media, device-first metadata, retention and lock limits.
7. Connect DMs + Patsy Social persistence and age enforcement.
8. Connect calendar/scheduling/publishing provider adapter.
9. Build authenticated Patsy AI/search server gateway.
10. Connect real AI image creator jobs.
11. Connect real async 10-second video jobs.
12. Complete THyNK editing engine.
13. Build/import 100 editable image + 50 editable video templates and original audio clips.
14. Build original music creator.
15. Build PawMoji Android IME; iOS later.
16. Integrate genuine production Patsy Rive asset only after validation.
17. Notifications, export/deletion, backup/recovery, restricted-account review.
18. Full under-16/safety enforcement and review/appeal flows.
19. Full device/offline/provider/RLS/retention/security regression testing.
20. Signed release, privacy/legal/store checks, Play Store staged rollout.

## Definition of done

A feature is complete only when its identity/permissions are enforced, secrets remain outside clients, failures are truthful and safe, required cross-device/retention behavior is verified, tests pass, provider actions are confirmed and the locked UI remains unchanged unless explicitly authorised.

## Change control

If a new approved instruction conflicts with an older locked rule, mark the older rule **SUPERSEDED** and retain history. Do not silently delete or reinterpret approved decisions. MASTER/APPROVED always wins over drafts, prototypes and older screenshots.
