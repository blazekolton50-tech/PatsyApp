# Patsy Source Intake — 2026-09-01

## Purpose

Record reconciled sources that may influence the Patsy Android app without allowing donor/reference material to overwrite the current native Kotlin/Compose source of truth.

## Source precedence

1. current code on `chatgpt/codex-ready-2026-09-01`
2. checked-in design/navigation/auth/security locks
3. live Supabase backend/security truth
4. latest owner-approved requirements
5. verified owner-supplied files whose bytes/origin are known
6. newly uploaded full THyNK Studio donor implementation for editor behavior/workflow
7. other Drive / AI Studio / Replit / Canva / archive donor material
8. older prototypes only when they do not conflict with 1-7

No donor source may silently replace native architecture.

## Full THyNK Studio upload — newly reconciled

The earlier source intake was too conservative because it primarily recorded Drive/archive wrappers and the older Android recovery ZIP. The Library now contains substantial THyNK Studio implementation material that has been directly inspected.

Relevant uploaded sources include:
- `Thynk-Full-Ecosystem-Studio(1).html`
- `Thynk-Full-Studio-Working(1).html`
- `Thynk-Studio-Pro-Complete` builds
- `FULL-INVENTORY.pdf`
- `THyNK-MUSIC-EVERYTHING.pdf`
- `THYNK-Music-Lab.tsx`
- `Templates.tsx`
- Gemini/React TypeScript source containing Publication/Magazine and Video editor modes
- 1110-item setup/material descriptions and editor-engine snippets

Classification: **SUBSTANTIAL_DONOR_IMPLEMENTATION**.

This classification means the source contains real implemented/demonstrated editor behavior worth porting. It does **not** make React/Tailwind/WebAudio/Canvas, browser persistence, Express/Prisma or WebView the production Patsy architecture.

Binding capability maps:
- `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md`
- `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json`

Approved reconciliation design:
- `docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md`

Execution plan:
- `docs/superpowers/plans/2026-09-01-thynk-full-studio-reconciliation.md`

### Demonstrated donor capability

The inspected material demonstrates/specifies substantial behavior across:
- Design canvas objects with x/y/w/h/rotation/opacity
- select/drag/resize/rotate
- layers with hide/lock/reorder
- Templates, Upload, AI Generate, Text, Elements, Stickers, Draw, Frames, Background, Layers, Rulers & Guides, Align & Distribute
- social/design/poster/document/presentation template catalogues
- Publication/Magazine page and spread workflows
- add-page/page-editor affordances and print/layout concepts
- Video editor/timeline concepts plus filters/effects/transitions/text animations/overlays
- THyNK Music player, visualizer, 10-band EQ, drum machine, piano roll, sampler, tracks/layers, effects and sound-library workflow
- browser-local autosave/version-history patterns that can inform native project persistence UX

### Donor behavior that is not production-real

The following must remain `REPLACE_PLACEHOLDER` rather than being promoted as finished production behavior:
- JavaScript `alert(...)` actions
- fake/static export URLs
- browser `captureStream` / timed `MediaRecorder` demo export
- random/placeholder waveforms or generated-looking media without real source data
- external placeholder-image fallbacks
- browser `localStorage` as production persistence
- donor Express/Prisma or Java Spring persistence as a second backend
- WebAudio oscillators/demo synthesis used as stand-ins for a verified Android production audio path
- a format selector being presented as successful audio/PDF/video export

Provider-backed generation, mastering, vocal processing and other unavailable services remain `NOT_CONFIGURED` until real providers/services are configured and verified.

## Conversation uploads received 2026-09-01

### `PatsyApp-AndroidStudio-THyNK-Music-artifact.zip`

Classification: `REFERENCE_ONLY` / older native recovery snapshot.

Outer SHA-256:
`b7acc3da0c29b4052ca0ded2620aecbae709036f547cb17ca1dfde9b2cdee88f`

Outer size:
`50,253,038` bytes

Nested `PatsyApp-AndroidStudio-THyNK-Music.zip` SHA-256:
`226602593cadcb07aa468745d3f2e11de992c7751d920ae610d12c75f073b076`

Nested size:
`50,252,848` bytes

Useful content includes native Android/Kotlin/Compose, FINAL/auth/account/Owner foundations, ten THyNK categories, Music foundation, Media3, Studio state/tool source, Rive host/runtime contracts and Supabase material. It predates the newer native Camera PR line, so never reset the live branch to this ZIP.

### `gemini-code-1788215509627.java`

Classification: `REJECTED` for production native port.

SHA-256:
`71c588b32a2286234ee398e5aa1015c11c4469ee09a426de587eaa5fc3a7901a`

It is an AppCompat/WebView wrapper loading local HTML. Do not merge it as the THyNK Music architecture.

### `gemini-code-1788215511233.xml`

Classification: `REJECTED` for production native port.

SHA-256:
`dcc056e2754f80317dcaa1cae766300914f93a8431b6693f7a613aa8728194a3`

It is a full-screen WebView layout and includes invalid `match_dirname`. Do not merge.

## Google Drive donor/recovery material

Known Drive references include:
- `patsy_full_1110_real.zip`
- `patsy_thynk_stage1_real.zip`
- `PDF_Reader_SETUP_THYNK_STAGE2_REAL.py.txt`
- `Full_App_Backend_Frontend_Editor_1110_Bricks.zip`
- `Icons_100_Illustrations_100_Logos_50_Original.zip`
- `Social_50_BusinessCards_25_Slides_50_Original.zip`
- `Video_Transitions_25_Text_Animations_25_Overlays_25_Original.zip`
- `Upscale_Studio_Canva_Snapchat_Video_Photo_Sound_Architecture.zip`
- `Stickers_100_Patterns_50_Mockups_25_Original_Bricks.zip`
- `Schedule_25_Todo_25_Tailwind_Bricks.zip`
- `Fonts_50_Free_Pack_Codex_Tailwind.zip`
- `Poster_Templates_25_HTML_Original.zip`
- `CV_React_Tailwind_Bricks_10.zip`
- `CV_Templates_10_Original_Editable.zip`

These Drive entries remain `REFERENCE_ONLY` until actual bytes, provenance/license, checksum and duplicate/supersession status are verified. The new full Studio upload does not automatically verify every named asset in older manifests.

Exact requested names `CV_Templates_20.zip`, `Poster_Templates_30.zip`, `Business_Cards_30.zip`, `School_Sheets_20.zip` and `ALL_TEMPLATES_100.zip` were not previously verified in Drive; do not claim them available unless found/re-uploaded.

## Replit

`Patsy Android Companion` remains `REFERENCE_ONLY`. It is an older Expo/React Native prototype and must not override native navigation/auth/storage/Camera/Rive architecture.

## Google AI Studio

AI Studio remains useful donor/reference material for editor requirements and earlier implementation ideas. GitHub/native code and this full-Studio reconciliation are authoritative for production execution.

## Notion / Canva

Notion is a readable mirror/status source only. Canva personal creator-assistant material is not production app UI authority.

## Live Supabase

Supabase is authoritative runtime/security infrastructure, not donor material. Preserve current RLS, auth, account-bootstrap, Owner authorization, DM and storage authority. Never replace it with donor Express/Prisma/Spring persistence.

## Locked app rules carried forward

### App chrome
- charcoal/black base
- white/light-grey text
- white primary controls with restrained rainbow/neon treatment
- exact approved Patsy wordmark asset
- one realistic main Patsy companion per page
- cartoon Patsy only for PawMojis/stickers/reactions

### Primary navigation
Semantic:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible:
`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

### Account menu
`Account · About · Profile · Settings · Remember Me`

### THyNK Music
Keep these route IDs stable:
`music-home`, `create-music`, `ai-music-generator`, `track-editor`, `mixer`, `equalizer`, `effects`, `lyrics-vocals`, `mastering`, `export`.

## Promotion criteria for donor assets

No donor asset becomes production-ready merely because it appears in an HTML build, PDF inventory, TSX catalogue or manifest. Verify:
- stable identity
- actual bytes or durable native resource
- known origin/license
- format/dimensions/Android compatibility
- SHA-256 when bytes exist
- duplicate/supersession decision
- no conflict with locked official logo/branding assets

Fail closed. Unknown files remain reference-only.
