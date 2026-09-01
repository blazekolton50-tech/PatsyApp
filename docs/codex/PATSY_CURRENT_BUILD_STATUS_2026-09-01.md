# Patsy Current Build Status — 2026-09-01

## Purpose
This is the current source-of-truth handoff for Codex after reconciling the live GitHub stack, connected Supabase, Google Drive donor material, Replit, Notion/Canva discovery, and the owner-supplied Android/Gemini uploads received on 2026-09-01.

Do not use the older 2026-08-31 status as the execution starting point when this file is present.

## Authoritative native line
- Repository: `blazekolton50-tech/PatsyApp`
- Native implementation: Kotlin + Jetpack Compose
- Verified parent Draft PR: **#39 — Draft: native Camera capture and THyNK handoff foundation**
- Verified parent branch: `chatgpt/camera-native-foundation-2026-08-31`
- Verified parent head: `78009aa63cdbdb0ae1d33a537ec14392f0b3380b`
- Codex-ready branch created from that exact head: `chatgpt/codex-ready-2026-09-01`
- PR #39 is stacked on Draft PR #38. Keep both Draft. Do not merge either without explicit owner approval and physical-device QA.

## Latest verified CI before this handoff-only branch
Patsy Consolidation CI:
- run number: `206`
- run id: `33444861508`
- exact code head: `78009aa63cdbdb0ae1d33a537ec14392f0b3380b`
- conclusion: **SUCCESS**
- unit tests: PASS
- debug APK build: PASS
- release variant build: PASS
- debug APK upload: PASS
- Android Studio ZIP build/upload: PASS

Any commits made on `chatgpt/codex-ready-2026-09-01` after the SHA above are handoff/documentation changes unless explicitly stated otherwise. Codex must still verify its own final head before completion claims.

## Completed native foundations — do not redo

### FINAL / SAVE MAIN APP screen foundation
Preserve the approved Login, Set Password and Home visual baseline. Do not redesign these screens while adding deeper functionality.

### Authentication / protected shell
Current stack includes:
- Supabase auth/session transport foundation
- encrypted session-token storage
- Remember Me as session policy, not plaintext password storage
- account bootstrap
- centralized route/deep-link authorization
- fail-closed age/protected account handling
- server-backed Owner authorization foundation
- publishable client key only; no service-role/provider secret in Android

Real-user physical-device auth is still unverified because the connected project currently has no Auth users.

### One authenticated primary navigation
Current `FinalMainActivity` owns the shared authenticated primary navigation outside page content. Login and Set Password remain outside authenticated routing.

Semantic routes remain:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible bar remains locked as:
`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

Rules:
- no duplicate small white THyNK subtitle
- no CAMERA caption under the centre +
- centre + remains Camera semantically
- black/charcoal base + approved restrained rainbow wave/glow
- authenticated Home, THyNK, Camera, PDMs, Profile, Owner Profile and Owner Tools use the shared outer bar
- do not weaken auth/age/Owner gates to keep the bar visible

Physical-device visual QA is still required.

### THyNK editor/video foundation
Already present:
- `StudioEditorState.kt`
- `StudioToolCatalog.kt`
- `StudioVideoPlayerLogic.kt`
- `StudioVideoPlayer.kt`
- Media3 `1.8.1`
- nested `video-editor` routing
- truthful EMPTY / LOADING / READY / FAILED media state
- no fake sample/imported production media

Do not repeat the old Media3 job.

### Native Camera foundation
Already present and green on PR #39:
- Android photo capture
- Android video capture
- Android image import
- Android video import
- private FileProvider capture URIs
- no broad storage permission
- video URI handoff to the shared THyNK Media3 editor
- internal routes for THyNK/Templates/Projects
- AI Image and AI Video remain `NOT_CONFIGURED`

Physical-device camera-app handoff and return-URI behavior still require real-device QA.

## Locked THyNK architecture
Do not create another THyNK home. Preserve these ten main categories:
1. Design & Templates
2. Social & Content
3. Photo & Image
4. Video & Camera
5. Documents & Business
6. Homework & Study
7. Presentations & Planning
8. Collage & Creative
9. THyNK Music
10. AI & My Studio

Current category item catalogue in `ThynkStudioCatalog.kt` is the native routing baseline.

## THyNK Music — current native state
Ten stable Music page IDs already exist and must remain stable:
1. `music-home`
2. `create-music`
3. `ai-music-generator`
4. `track-editor`
5. `mixer`
6. `equalizer`
7. `effects`
8. `lyrics-vocals`
9. `mastering`
10. `export`

Current truth boundaries:
- local UI/editor controls may work locally
- do not fabricate audio data or waveforms
- do not simulate AI generation success/progress
- Create Music / AI generation / lyrics-vocal processing / mastering / audio export stay `NOT_CONFIGURED` until genuine pipelines are wired and verified
- selecting an export format is not export success

## Design & Templates — current direction
Design is a primary THyNK category, not a generic placeholder page.

Locked first production vertical slice remains:
- central canvas presets + custom size validation
- deterministic canvas layer model
- select / move / resize / rotate / flip / duplicate / delete / reorder
- bounded undo/redo
- Android image import with validation
- real PNG export from logical canvas dimensions
- editable project state remains separate from export result
- permanent outer navigation remains visible

After this base slice is green, verified templates/assets can be surfaced through the donor manifest. Reference-only wrapper files must never appear as production templates.

## Profile and top-right account menu — preserve latest owner lock
Do not replace these with an old `More`-first navigation concept.

Primary Profile direction includes:
- profile picture
- profile/bio details
- social links
- gallery/media
- recent projects
- saved projects
- Schedule and Calendar as secondary profile/account tools, not primary bottom tabs
- Remember Me entry
- Owner Profile / Owner Tools only after server authorization

Top-right account menu order/labels remain:
- Account
- About
- Profile
- Settings
- Remember Me

Where legacy code uses older names/layouts, align it to this contract without weakening route gates.

## Patsy / Rive truth gate
Current repository contains Rive host/controller/adapter foundations, but the approved final production `.riv` is still not verified as present and device-tested.

Do not:
- fake production Rive with GIFs, sprites or static pose swaps
- call a static PNG/bob animation production Patsy
- claim lip-sync/walking/target-aware gesture completion without the real authored `.riv` and device evidence

Main app Patsy remains the realistic approved companion; cartoon Patsy is PawMoji/sticker-only.

## Supabase live truth — refreshed 2026-09-01
Connected project is ACTIVE_HEALTHY.

Current verified snapshot:
- 37 public tables, RLS enabled on all 37
- 10 private tables, RLS enabled on all 10
- 119 policies
- 4 private storage buckets
- Security Advisor: 0 current lints
- Auth users: 0
- Studio presets: 48
- other inspected production account/DM/template/project tables remain empty at this pre-user stage

Active relevant functions include:
- `create-dm-thread` v5, JWT verified
- `auth-login` v1
- `auth-register-start` v1
- `auth-register-complete` v1
- `auth-reset-request` v1
- `account-bootstrap` v4, JWT verified
- `owner-authorize` v1, JWT verified

Do not change RLS or backend authority merely to make UI testing easier.

## New 2026-09-01 uploaded-source intake
Full inventory and decision record:
`docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md`

Key decisions:
- owner-uploaded Android Studio THyNK Music artifact = useful older native recovery/reference snapshot; do not replace the newer PR #39 line with it
- Gemini Java WebView wrapper = REJECTED for native production port
- Gemini WebView XML = REJECTED for native production port
- Drive/AI Studio/Replit/archive packs = REFERENCE_ONLY until bytes/license/checksum/duplicate status are verified

## Current execution order for Codex
Use `docs/codex/CODEX_NEXT_INSTRUCTION.txt` as the binding instruction.

High-level order:
1. verified donor-asset intake foundation
2. generic THyNK canvas + Android image import + real PNG export
3. Design & Templates verified-asset/template surfacing and autosave integration
4. THyNK Music ten-screen refinement without fake providers
5. Profile + account-menu visual/routing alignment to latest lock
6. production DMs / storage / Remember Me slices as separately verified work
7. production Patsy Rive only when the actual authored `.riv` is available
8. consolidated CI + physical-device QA

STOP on a failed dependent job. Keep work Draft. Never merge automatically.