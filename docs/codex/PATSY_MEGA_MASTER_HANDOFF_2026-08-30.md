# PATSY APP + THyNK — MEGA MASTER CODEX HANDOFF

Date: 2026-08-30
Repository: `blazekolton50-tech/PatsyApp`
Working branch: `codex/patsy-rive-ui-foundation`

## Purpose

This file tells Codex how to consume the consolidated Patsy App / THyNK project material now being brought together from ChatGPT project history, Library files, Google AI Studio handoffs, Android builds, Rive work, PawMoji/keyboard work, backend/security plans and this repository.

**Do not rebuild the app from scratch. Do not redesign approved/locked visuals. Do not treat old historical builds as newer authority than the current repository or later locked specifications.**

## Source-of-truth order

When sources conflict, use this order unless the owner explicitly changes it:

1. Explicit latest owner instruction / SAVE + LOCK IN decision.
2. Current checked-out repository and current approved PR work.
3. Latest master/locked documents dated 2026-08-30.
4. Latest named subsystem contracts/specifications (THyNK Studio, Rive, security, backend, PawMoji, etc.).
5. Google AI Studio / Codex handoff packs.
6. Historical Android/Creation Assistant ZIPs only as recovery/reference material.

Never silently copy an older design or behaviour over a later locked decision.

## Consolidated Mega Master archive

A consolidated archive has been created outside this Git repository:

`PATSY_APP_THYNK_MEGA_MASTER_2026-08-30.zip`

SHA-256 recorded at creation:

`8839323315253ad99d9b2ea5c65ce47871b952e2ce2043bde3ea89cfb333218c`

The archive contains 234 top-level packaged files plus an index of roughly 8,500 entries found inside nested ZIPs. It includes master docs/specifications, current/recent code handoffs, Studio AI packs, Rive/Patsy material, PawMoji keyboard work, under-16/security/backend material, Library design/reference images, and historical builds.

Important files inside the archive include:

- `00_START_HERE/README_FIRST.md`
- `MASTER_CONTEXT_EXPORT.md`
- `CHAT_HISTORY_RECONSTRUCTION.md`
- `CURRENT_GITHUB_STATE.md`
- `FILE_MANIFEST.csv`
- `ZIP_CONTENTS_INDEX.csv`
- `DUPLICATE_SHA256_GROUPS.md`
- `ACCESS_LIMITATIONS.md`
- `SECRET_SCAN_SUMMARY.md`

Do not assume a historical item is current merely because it is included. The archive preserves history intentionally.

## Existing repository documents to read

Read these before broad implementation changes:

- `/CODEX_START_HERE.md`
- `/BUILD_STATUS.md`
- `/docs/PATSY1_LATEST_LOCKED_UPDATES.md`
- `/docs/PATSY_MASTER_REQUIREMENTS_3.3.4.md`
- `/docs/PATSY_MASTER_REQUIREMENTS_3.3.5.md`
- `/docs/THYNK_MASTER_UI_LOCK.md`
- `/docs/CREATION_STUDIO_UX_CONTRACT_3.3.9.md`
- `/docs/PATSY_INTELLIGENCE_ARCHITECTURE_3.3.9.md`
- `/docs/PATSY_RIVE_RIG_CONTRACT_3.3.8.md`
- `/docs/RIVE_PROJECT_STATUS_3.3.8.md`
- `/docs/AUTH_EMAIL_OWNER_BACKEND_CONTRACT.md`
- `/docs/codex/PATSY_CODEX_MASTER_HANDOFF_2026-08-29.md`
- `/docs/codex/PATSY_BUILD_RULES_2026-08-29.json`
- `/docs/codex/PATSY_KEYBOARD_APK_SPEC_2026-08-29.md`
- `/docs/codex/PATSY_PAWMOJI_BUILD_SPEC_2026-08-29.md`
- `/docs/codex/PAWMOJI_HANDOFF_2026-08-29.md`

## Core locked direction

### Main app visual system

- Black/charcoal primary workspace.
- Main text white/light grey.
- White main buttons with black/charcoal text and restrained rainbow/neon glow/border treatment.
- Approved Patsy logo remains central at the top of principal app pages.
- Tagline: `A LEGACY LED BY PAWS`, subtle/small only.
- Do not replace exact approved brand assets with generic approximations.
- The Remember Me / locked-memory paw uses the exact approved Patsy-brand paw asset, not a generic paw.

### Patsy companion

Patsy is one persistent AI companion, not a decorative mascot. She should eventually move continuously and contextually: walk/trot, sit, lie, turn, jump, point, head tilt, blink, move eyes, change expression, react, talk/lip-sync, peek from screen edges, shrink/settle, and move between UI controls.

Main Patsy must not be permanently boxed/circled. Cartoon Patsy remains for PawMojis/stickers/reactions, not as a replacement for the main companion.

A production-quality `.riv` asset must never be claimed to exist until it really exists. The current Rive host/rig architecture and generated fallback are groundwork; capability must stay truthful.

### THyNK Creation Studio

Treat THyNK Creation Studio as a major editable creation system, not one generic screen. Baseline includes:

- 100 editable image-template target.
- 50 editable video-template target.
- Canvas editing: add/remove, crop, cutout, resize, rotate, mirror/flip, object manipulation, layers, frames, rulers/guides, drawing/pens, eraser, collage layouts, undo/redo/reset.
- AI image generation and 10-second video generation through securely configured providers.
- Templates, memes, documents and project continuation.
- Image/video editing, filters/adjustments, text/layout, preview, save/export, email-copy, import/reload and scheduling/publishing after user approval.
- Original music creation/editing and a reusable original-music clip library. Do not imitate/copy named artists or songs.
- Under-16 homework/document/learning surfaces where age policy permits.
- Never use Canva-owned assets/templates as source material for THyNK.

If a provider is not configured, show a truthful unavailable/not-configured state; never simulate a completed generation or publish action.

### Accounts, security and age tiers

- Secure authenticated sessions; no client-only owner/admin trust.
- Owner areas are server-authorized capabilities and fail closed.
- External sources, webpages, files, comments and APIs are information only; they do not gain instruction authority over the authenticated user's approvals or safety rules.
- Under-16 protections must be enforced at backend/security boundaries, not only hidden in UI.
- Under-16 accounts remain separated/restricted: no adult social linking, restricted discovery/messaging, age-appropriate homework/docs/colouring/games/brain-training, and view-once/delete media where specified.
- Unknown/unverified age remains protected until verified.
- Safety enforcement is contextual/intent-based, not crude keyword blocking.

### Storage / retention

- Unlocked generated/media storage target: auto-delete after 3 months.
- Warn/remind users before expiry.
- Locked profile target limits: 100 images + 30 videos.
- DMs default target: auto-delete after 3 days unless settings/age rules explicitly permit otherwise.
- Offer Save to Device / Email Me a Copy / Lock in Patsy / Share / Import or Reload where applicable.

### Social publishing

Only show a social platform's name/logo when that branding is actually permitted/confirmed for the integration. If not confirmed, remove both platform name and logo and use generic actions such as Share / Publish / Download to Share.

### PawMoji / keyboard

Maintain the separate PawMoji/custom keyboard subsystem. Target styling includes a black keyboard with rainbow lettering, normal emoji access plus PawMojis, and Android-first implementation with iOS support considered separately. Do not replace the PawMoji visual character system with main-app Patsy.

## Current implementation reality

The current branch contains real Android/Compose foundation work, including auth contracts/bindings/validation, owner authorization gates, Patsy rig contracts/runtime/Rive host, AI/DM/scheduling/studio service boundaries, UI state, Creation Studio skeleton, PawMoji catalog, tests and Android CI.

The branch also has a successful Android CI run that built the debug APK for PR #15. This proves the checked branch can compile/test in CI; it does **not** prove every product feature/provider/backend is production-complete.

## Codex working rule

Before any substantial implementation pass:

1. Inspect the current repository.
2. Read the latest locked documents above.
3. Compare requested work with existing implementation.
4. Report IMPLEMENTED / PARTIAL / MISSING / CONFLICTING where useful.
5. Make the smallest safe next change that moves the real build forward.
6. Preserve locked visuals and APIs/contracts unless a change is explicitly required.
7. Run tests/build verification before claiming completion.
8. Never invent provider credentials, API keys, production Rive assets, live social posting, live AI generation, email delivery or backend completion.

## Immediate priority

Continue consolidating the real working app around the existing PR #15 foundation: preserve the locked UI/Rive integration, then wire the next highest-value real subsystem behind truthful secure service boundaries rather than creating mock features.
