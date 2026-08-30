# CODEX — START HERE: PATSY APP

Repository: `blazekolton50-tech/PatsyApp`

## NEW — Mega Master context

A consolidated project handoff has now been added at:

`docs/codex/PATSY_MEGA_MASTER_HANDOFF_2026-08-30.md`

**Read that file before starting a new substantial task.** It records the 2026-08-30 consolidation of Patsy App / THyNK requirements, Studio AI material, Rive/animation work, PawMoji/keyboard work, security/under-16 plans, backend work, Library/reference assets, historical builds and the current repository state. Historical files are reference/recovery material and must not override newer locked decisions or the current repository.

## NEW — Safe Storage + Remember Me lock

Read:

`docs/codex/PATSY_SAFE_STORAGE_REMEMBER_ME_LOCK_2026-08-30.md`

This is a **SAVE MAIN APP / SAVE LOCK IN** architecture decision. It defines the separation between durable Remember Me semantic memory, 90-day temporary feed/media storage, durable saved/locked media, device-first storage, THyNK app-owned assets and owner backup/archive. It also locks atomic lifecycle/reference promotion instead of unnecessary blob copying, compression/deduplication, server-side retention, cross-device sync requirements and direct cross-user security tests.

The storage lock extends the product rules; it does not prove production implementation. Audit live code/backend evidence and mark each item DONE / PARTIAL / BLOCKED.

Do not redesign or replace this project from scratch.

## First task
Before changing code, inspect the existing repository and return an audit with four sections:

- IMPLEMENTED
- PARTIAL
- MISSING
- CONFLICTING

Check navigation/routes, theme/design tokens, authentication, Patsy rendering/animation, social/feed, Design Studio, profile, messaging, scheduling, storage/media retention, safety/age gating and owner/admin areas.

## Locked visual rules
- Black/charcoal primary UI.
- Main text white/light grey.
- Primary buttons are WHITE with restrained rainbow/neon borders/glow.
- Approved Patsy logo is centred at the top on principal pages.
- Tagline is `A LEGACY LED BY PAWS`, small/subtle only.
- Main Patsy is the realistic grey shaggy dog companion, never cartoon/chibi.
- Main Patsy is not permanently boxed, circled or trapped in a halo. She should move freely on transparent background where possible.
- Main Patsy is always alive in the interface: walk/trot, sit, lie down, turn, head tilt, blink, look around, mouth movement, react, gesture, peek from screen edges, move between UI areas, shrink/settle when appropriate.
- Cartoon Patsy is reserved for PawMojis / custom keyboard / stickers / reactions only.

## Signup copy
Use exactly:

`I'm Patsy. Your personal AI PetPal. Log in and I'll show you what I can do!`

## Main navigation
Home · Design Studio · Create/+ · Patsy DMs · Profile

## Design Studio
Treat this as a major feature area, not one generic page. It includes Ask Patsy while creating, AI image generation, 10-second AI video generation, memes, templates, image/video editing, filters/adjustments, text/layout tools, project continuation, preview, save/export, share, email copy, import/reload and schedule/post after approval.

If any provider/API is unavailable, mark it `NOT_CONFIGURED`. Never fake a working service.

## Media retention
- Unlocked images/videos in Patsy storage are temporary and auto-delete after 3 months.
- Warn users clearly and remind them before expiry.
- Offer: Save to Device · Email Me a Copy · Lock in Patsy · Share · Import/Reload.
- Emailing a copy does not lock the item in Patsy storage.
- Users can reload from device storage and supported cloud/file providers such as Google Drive / OneDrive / Dropbox where available.
- Locked-profile target limits: 100 photos and 30 videos.

## Remember Me
Anything intentionally remembered/locked uses Patsy's paw. Outline by default; when saved it fills with bright neon, gives a short click + soft chime, pulses once, sparkles fade, then remains locked in.

The UI interaction above maps to the durable semantic-memory architecture in `docs/codex/PATSY_SAFE_STORAGE_REMEMBER_ME_LOCK_2026-08-30.md`. Remember Me and saved/locked media are separate concepts and must not be implemented as one raw-media folder.

## Messaging
Default DM retention target: auto-delete after 3 days unless settings/age rules permit otherwise.

## Age-aware rules
Keep under-16 protections and age gating enforced. Do not expose adult-only social/messaging features merely by hiding UI; enforce at the appropriate backend/security layer too.

## Persistent Patsy
Patsy is one persistent AI companion across the app with Brain, Memory, Personality, Awareness and Safety systems. She is not a decorative mascot.

## Priority conflict to fix after the audit
Any current main-Patsy treatment that uses a cartoon/static boxed mascot should be replaced with the realistic unboxed moving Patsy treatment, without changing PawMoji assets.

Preserve approved work. Do not make broad visual changes without explicit approval.
