# CODEX — START HERE: THyNK-IN — 2026-09-02

Repository: `blazekolton50-tech/PatsyApp` (legacy repository slug; product name is THyNK-IN)

Current working lines:
- device/THyNK Panel: `chatgpt/device-launcher-thynk-panel-2026-09-02` (PR #43)
- Patsy animation/composite integration: `chatgpt/patsy-animation-composite-2026-09-02` (stacked from PR #43 head)

PR #41 is a historical closed draft. Its exact head was merged through PR #42 into the current working native line. Do not treat the old “PR #41 unmerged” wording as current state.

Keep current integration PRs Draft unless the owner explicitly authorizes merge. Physical-device QA remains required for production claims.

## Canonical naming — binding

- App/global product brand: **THyNK-IN**.
- Do not call the product **Patsy App** in current UI, docs, handoffs, PR wording or new code-facing copy.
- **Patsy** is the assistant/character name. Keep Patsy-specific animation, Rive, personality, PawMoji, companion and reference-asset names as Patsy.
- Older filenames containing `PATSY`, `PatsyApp` or `patsy1` are legacy filenames only; do not interpret them as the current product brand.
- Do not rename the existing `com.patsy.app` namespace/application identity as part of ordinary branding work. Treat that as a separate migration requiring install/auth/deep-link/backend verification.

## Read these first — binding order

1. `docs/codex/THYNK_IN_AUTHORITATIVE_SOURCE_2026-09-02.md`
2. `docs/codex/PATSY_COMPOSITE_ANIMATION_HANDOFF_2026-09-02.md` when working on Patsy animation/control
3. `docs/codex/CODEX_RIVE_NEXT_SLICE_CANONICAL_2026-09-01.md` when working on Rive
4. `docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md`
5. `docs/superpowers/plans/2026-09-01-thynk-full-studio-reconciliation.md`
6. `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md`
7. `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json`
8. `docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md`
9. `docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md`
10. `docs/codex/CODEX_NEXT_INSTRUCTION.txt`
11. `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`
12. `docs/NAVIGATION_LOCK_2026-08-31.md`

The older 2026-08-31 status/handoff files are historical checkpoints. Newer dated authoritative files supersede conflicting older wording.

## Source truth

The uploaded Library contains substantial donor/editor implementations, including full THyNK Studio builds, Design/editor tools, Publication/Document workflows, Video editor donor behavior, THyNK Music Lab, templates and setup/material descriptions.

This does **not** mean the React/Tailwind/WebAudio runtime becomes production architecture. Production THyNK-IN remains native Kotlin/Jetpack Compose with the existing Supabase, auth/security, Camera, Media3, navigation and provider truth boundaries.

Every donor capability must follow the four-status matrix:
- `ALREADY_NATIVE`
- `PORT_FROM_UPLOAD`
- `REPLACE_PLACEHOLDER`
- `GENUINELY_MISSING`

Do not rebuild `ALREADY_NATIVE` work. Do not copy `REPLACE_PLACEHOLDER` demo behavior as production. Do not claim `GENUINELY_MISSING` work is complete.

## Already native — do not redo

- Kotlin/Jetpack Compose production shell
- FINAL Login / Set Password / Home baseline
- Supabase auth/session/account-bootstrap/Owner foundations
- age/protected route gates
- one authenticated outer primary navigation bar
- ten THyNK categories
- ten stable THyNK Music page IDs
- `StudioEditorState.kt`
- `StudioToolCatalog.kt`
- Media3 1.8.1 single-clip video foundation
- native Camera photo/video capture
- native image/video import
- private FileProvider capture URIs
- video handoff to THyNK Media3 editor
- Rive host/runtime contracts, but **not** the final authored production Patsy `.riv`

Do not repeat Media3, global-navigation, Camera-foundation, category-routing or auth/security jobs.

## Patsy animation continuation — current

Production Patsy remains Kotlin/Compose/Rive. The Unity/C# subsystem prototype from 2026-09-02 is behavioral reference only.

Preserve the current native ABI and runtime files:
- `PatsyRigContractV1.kt`
- `PatsyRigRuntimePort.kt`
- `PatsyRiveHost.kt`
- `PatsyRiveRuntimeAdapter.kt`

Reconcile the fuller companion/controller regression work from `codex/patsy-rive-ui-foundation` / PR #15 selectively into the latest working line. Do not roll back newer THyNK-IN work.

The five semantic layers to preserve are Body, transient Action, Emotion + intensity, Attention + target, and Voice. Composite commands must coexist rather than replace each other. Duplicate dispatch must not endlessly retrigger one-shot actions or restart speech.

Do not fake unsupported V1 actions/expressions. Current V1 motion is idle/walk/sit/lie/jump/wave/point. Current V1 expressions are neutral/cheeky/excited/curious/confused/concerned/proud/sleepy. A genuine authored `app/src/main/res/raw/patsy_assistant.riv` matching this ABI remains the production visual blocker.

Active implementation tracker: GitHub issue #45.

## First coding slice after this documentation reconciliation

For Patsy work, follow issue #45 and `PATSY_COMPOSITE_ANIMATION_HANDOFF_2026-09-02.md` using RED/GREEN TDD.

For THyNK editor work, continue Design & Templates native port on the existing shared editor core using the capability matrix.

Do not create a second editor or embed the donor React runtime.

Asset/template files still fail closed until real bytes, origin/license, checksum, format and supersession status are verified. Asset verification is a gate inside the slice that uses the asset; it must not block porting non-asset editor behavior.

## Locked visual rules

- black/charcoal primary UI
- white/light-grey main text
- white primary buttons/controls with restrained rainbow/neon treatment
- THyNK-IN is the app/global brand; use the approved current THyNK-IN/global brand asset where global app branding is required
- use matching approved THyNK destination logos on their destination pages
- one realistic main Patsy companion per page
- cartoon Patsy only for PawMojis/stickers/reactions
- never fake production `.riv`

## Locked main navigation

Semantic routes:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible bar:
`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

No second white THyNK subtitle. No CAMERA caption under the centre +. Schedule/Calendar stay secondary.

## Top-right account menu

`Account · About · Profile · Settings · Remember Me`

## Production truth boundaries

Do not introduce:
- WebView/React/Vite/Next as the production THyNK runtime
- a second Express/Prisma/Spring backend replacing Supabase
- browser `localStorage` as production persistence
- fake export URLs or alert-only production actions
- random/placeholder waveforms presented as real audio
- simulated AI generation/mastering/provider success
- weakened auth/RLS/Owner/age gates
- fake production Rive using GIFs, sprites, PNG pose swaps or bobbing static images

Provider-backed work stays `NOT_CONFIGURED` until genuinely configured and verified.

## Verification

Every coding slice must show RED-before-GREEN evidence, targeted/full tests, debug/release build results, exact final GitHub Actions run/head SHA and remaining physical-device/provider/Rive limitations.

Stop on failure. Keep integration PRs Draft unless explicitly authorized to merge.
