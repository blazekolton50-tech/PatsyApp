# CODEX START HERE — PATSY MASTER 2026-08-30

Use this order before implementing PatsyApp changes:

1. `docs/master/PATSY_MASTER_EVERYTHING_LOCKED_2026-08-30.md` — highest-authority consolidated product/design/status master.
2. `docs/codex/PATSY_MASTER_API_BACKEND_HANDOFF_2026-08-30.md` — backend/API/provider integration rules.
3. `docs/codex/RIVE_UI_FOUNDATION_STATUS_2026-08-29.md` — Rive/UI foundation status; never fabricate production `.riv`.
4. `docs/codex/PATSY_KEYBOARD_APK_SPEC_2026-08-29.md` + `PATSY_PAWMOJI_BUILD_SPEC_2026-08-29.md` + `PAWMOJI_HANDOFF_2026-08-29.md` — PawMoji/keyboard details.
5. `docs/codex/PATSY_CODEX_MASTER_HANDOFF_2026-08-29.md` and `PATSY_BUILD_RULES_2026-08-29.json` — older handoff context only; where they conflict, the 2026-08-30 master wins.

Current verified build truth:
- Native Android/Kotlin is the production path.
- Android CI has passed unit tests, `assembleDebug`, and APK artifact upload on PR #15.
- MainActivity shared Header/Primary wrappers use the extracted locked visual system.
- Live Supabase infrastructure exists and must be preserved/inspected before schema changes.
- Production Patsy `.riv` is still missing: do not fake animation.
- Auth, DMs, Patsy Social, AI/search, image/video generation, scheduling/publishing, full Studio engine, PawMoji IME and under-16 end-to-end enforcement remain partial/not implemented as described in the master.

Locked requirements that must be carried forward include:
- no silent redesign of Patsy/THyNK/navigation/brand assets
- exact centered Patsy branding/tagline and approved THyNK `y`
- exact branded Remember Me paw
- realistic app Patsy companion; no substitute dog/emoji
- Patsy points/tilts/moves eyes/expresses/reacts/teaches once genuine rig supports it
- 100 editable image templates + 50 editable video templates
- original reusable sound/music clips + original-only music creator
- black/rainbow PawMoji Android keyboard with normal typing, normal emoji and PawMoji picker
- 90-day feed/shared-media retention; 3-day DM retention; 100 image + 30 video saved-profile target
- provider-neutral server-side AI/provider architecture
- authenticated-user authority only for consequential actions
- separate protected under-16 capability model with server-side enforcement
- contextual safety rather than keyword bans
- platform name+logo only where branding permission is confirmed; otherwise generic Share/Publish/Download to Share

Status vocabulary: VERIFIED / PARTIAL / NOT IMPLEMENTED / BLOCKED / LOCKED DESIGN.

Do not mark a feature complete merely because UI/schema/code exists. Verify end-to-end behavior first.
