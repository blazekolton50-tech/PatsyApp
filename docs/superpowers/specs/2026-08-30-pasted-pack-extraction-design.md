# Pasted Pack Extraction Design — 2026-08-30

## Purpose

Translate the useful parts of the newly supplied Patsy/THyNK Kotlin pack into the existing native Android/Kotlin app without creating a second package tree, duplicating existing Studio models, weakening safety boundaries, changing the locked visual system, or claiming unfinished capabilities are complete.

This design is extraction-first. The pasted pack is treated as source material, not as a drop-in replacement.

## Source-of-truth precedence

1. Existing locked Patsy/THyNK requirements and approved current reference screens.
2. Existing native Android/Kotlin architecture in `com.patsy.app`.
3. Current isolated GitHub branches and tests.
4. Useful implementation ideas from the newly supplied pasted pack.
5. Replit remains prototype/reference only and is not production architecture.

When the pasted pack conflicts with an existing locked requirement or current native contract, the current native/locked requirement wins.

## Branch isolation

Implementation work stays isolated from `main`.

- Auth/account work remains on `integration/auth-ui-rememberme-2026-08-30` / PR #25 until verified GREEN.
- THyNK Studio work remains on `integration/thynk-local-catalog-drive-dev-2026-08-30` / PR #24 until its RED requirements are satisfied.
- Real Patsy/Rive work remains aligned with `codex/patsy-rive-ui-foundation` / PR #15 and its existing rig/runtime contract.
- This spec branch is documentation-only and must not be merged as implementation by itself.

No merge to `main` is allowed without explicit user approval.

## Package and module boundaries

Do not introduce the pasted `com.patsy.thynk.*` tree as a parallel app architecture.

Translate relevant code into existing packages under `com.patsy.app`, reusing the current contracts:

- shared visual system under the existing UI/design package
- account/settings UI under the existing app feature structure
- Studio editor/project/export code under `com.patsy.app.studio.*`
- Patsy animation under the existing Patsy/Rive controller and rig packages

Do not create duplicate `StudioProject`, `StudioLayer`, export, media, auth, or animation contracts where current equivalents already exist.

## Visual design extraction

Keep the useful palette direction from the pasted pack:

- background `#121212`
- charcoal surfaces around `#1A1A1A`, `#242424`, `#2E2E2E`
- border `#3A3A3A`
- white primary text
- restrained rainbow using pink/orange/yellow/green/cyan/purple accents
- success green and error/logout red

However, there must be one shared Patsy color system rather than local duplicate `PatsyColors` objects in individual features.

The exact Patsy wordmark remains artwork. It is not recreated from a font.

The current verified app resource `patsy_logo_official_white.png` remains the source for the main app header where applicable.

THyNK branding appears only on THyNK Studio surfaces, not as a replacement for the main Patsy app header.

## Typography

The pasted pack records intended typography hierarchy, but custom font availability must remain truthful.

- Montserrat/Poppins/Inter may only be used as real named fonts when bundled or otherwise verified available.
- Silent substitution that makes an unavailable custom font look successfully loaded is not acceptable.
- The Patsy logo itself remains an image asset rather than a typeface recreation.

## Master paw contract

The new menu and Remember Me UI must use one exact approved master paw asset.

Canonical Android resource target:

`R.drawable.patsy_master_paw`

Important constraints:

- This asset is the exact approved branded paw from the Patsy logo/Remember Me system.
- Do not generate or substitute a generic paw.
- Do not treat the pasted descriptions such as “cream fluffy Maltipoo paw” as the visual source of truth.
- The resource is currently not verified as present in the native branch, so code depending on it remains blocked until the real drawable is added and verified.

## Account dropdown

The approved account dropdown is the compact four-item menu:

1. `my_account` — My Account — Username, password, info
2. `security_privacy` — Security & Privacy — PIN, 2FA, devices, privacy
3. `patsy_settings` — Patsy Settings — Patsy's personality & prefs
4. `log_out` — Log Out — Sign out of your account

The larger 13-item menu in the pasted pack is not used to replace this dropdown. Those broader destinations may belong in the separate More/settings information architecture later, but they are outside this component.

Visual rules:

- charcoal card around `#1E1E1E`
- 16dp rounded corners
- subtle dividers
- white primary text and muted secondary text
- Security & Privacy may use the approved restrained purple accent
- Log Out remains red
- Patsy Settings uses the exact master paw

## Patsy Settings paw micro-interaction

Keep the approved micro-motion idea, but use restrained motion:

- small scale pulse around 1.18–1.22x
- slight tilt around 7–8 degrees
- restrained rainbow glow
- approximately 140 ms acknowledgement before navigation
- prevent repeated taps during the pending navigation window
- reduced-motion path removes bounce/rotation and navigates without the animated delay
- no cream “fluff puffs”
- no mandatory woof sound
- optional sound/haptic support may later be driven by shared Patsy preferences

The component must expose meaningful accessibility semantics.

## Remember Me extraction

Keep the underlying interaction concept from the pasted pack:

- unsaved state is visually quiet
- save acknowledgement uses the exact master paw
- saved state may use a restrained rainbow fill/glow and scale acknowledgement
- optional sound/haptic may be controlled by user preferences
- reduced-motion equivalent required

Do not use a generic Unicode paw such as `Text("🐾")` in production.

Do not render decorative fake audio wave icons merely to imply a sound occurred.

Remember Me persistence remains session/account memory behavior under existing privacy controls; the UI animation must not imply data was persisted unless the real persistence operation succeeded.

## Profile-page extraction

The pasted profile notes are useful only as layout/reference ideas. Production implementation must preserve the current locked Patsy app design and policies.

Useful concepts to retain:

- centered Patsy brand header where appropriate
- dark profile page
- user avatar with restrained rainbow treatment
- stats and recent/saved project sections
- Quick Post treatment using the shared rainbow accent
- locked-media status and quotas when backed by real state
- exact locked bottom navigation contract

Do not hardcode fabricated counts such as posts/friends/following or media usage.

Do not show third-party social platform names/logos unless their use is permitted for the integration. If permission is not confirmed, remove both the name and logo and use generic share/link language.

## THyNK Studio model reuse

The pasted renderer/export pack must target the existing Studio models on PR #24 instead of introducing alternate versions.

Existing native concepts to reuse include:

- `StudioLayer`
- `StudioLayerType`
- `EditorSnapshot`
- `StudioEditor`
- `StudioProject`
- `StudioMediaReference`
- `StudioExportRequest`
- `StudioExportResult`
- `StudioExportService`

The existing editor already provides basic add/move/resize/rotate/z-order/opacity/lock/visibility/flip/text/filter/effect/duplicate/delete/undo/redo behavior. Renderer work should consume that state rather than recreate it.

## Shared image renderer extraction

Useful renderer concepts from the pasted pack:

- deterministic layer ordering by z-order
- visibility checks
- opacity
- rotation around layer centre
- horizontal/vertical flip
- text, shape, image, sticker and PawMoji render branches
- crop validation/clamping
- render diagnostics including rendered/skipped counts, warnings and errors
- explicit placeholder/unresolved-resource reporting

Required corrections before implementation:

- use existing `StudioLayer` and `StudioProject` contracts
- do not assume cache paths such as `test-assets/<id>.png` are production asset resolution
- asset resolution must go through the existing media/asset abstraction
- unresolved assets must not be silently counted as successful output
- add explicit memory/bitmap-size protection
- validate requested output dimensions before allocating a bitmap
- avoid double-applying scale factors
- text rendering must use verified font availability and must report unavailable fonts rather than silently pretending the requested font loaded
- renderer background comes from project/export request semantics, not a forced charcoal default for every project
- transparent PNG output must remain possible
- JPEG may require an explicit opaque background
- failure to render required layers must be surfaced clearly

## Thumbnail generation

Keep these concepts from the pasted pack:

- thumbnails use the same renderer as final image output
- controlled thumbnail dimensions
- compression success and non-zero output checks
- safe path handling
- diagnostics passed through from the renderer

Corrections:

- thumbnail naming must use sanitized project identifiers and app-managed storage abstractions
- thumbnail generation must not imply cloud sync
- project/user isolation must be maintained

## Export behavior

Preserve the current native truthfulness contract: a `COMPLETE` export requires real output metadata.

Useful pasted concepts:

- explicit RUNNING/FAILED/COMPLETE state changes
- image format selection
- real compression
- zero-byte detection
- path traversal protection

Required production behavior:

- a generic export call that lacks resolved project content must fail safely
- image export may only become COMPLETE after a real output file/media reference exists
- unresolved required layers, missing assets, invalid fonts, allocation failures or compression failures must prevent false COMPLETE status
- cancellation state must be represented truthfully
- video/audio/project-package support remains NOT_CONFIGURED or separate until genuinely implemented
- no placeholder file may be presented as a successful finished export

## THyNK catalogue/template truthfulness

The pasted labels mentioning “100 IMAGE TEMPLATES” and “50 VIDEO TEMPLATES” are requirements/targets, not evidence that those editable assets currently exist.

The app may display real catalogue counts only from validated catalogue data. Until then, do not present target counts as completed inventory.

The current plan remains to prove a smaller real editable batch before scaling.

## Rive / real Patsy boundary

This pasted Studio pack does not replace or redefine the real Patsy animation architecture.

Keep the existing Rive contract/controller direction on PR #15.

Do not create:

- a second rig contract
- sprite/GIF animation fallback presented as production animation
- static pose swapping presented as a real rig
- a fake `.riv`

The genuine production `.riv` remains blocked until an authored asset is supplied and validated against the current contract.

## Replit boundary

Replit may be used to inspect/test visual or UX concepts quickly, but its Expo/React Native architecture is not merged into the native Android production app.

Reusable from Replit:

- high-level UX grouping
- copy/interaction ideas
- prototype feedback

Not reusable as production implementation:

- Expo Router
- React state as production app state
- AsyncStorage identity/auth
- static circular Patsy avatar treatment
- client-only safety/age/owner boundaries

## Security and safety

This extraction must not weaken current fail-closed behavior.

- auth remains provider-neutral and fail closed until configured
- owner/admin checks stay server/authority backed
- under-16 and protected-mode enforcement must not be client-only
- Remember Me must not store passwords
- logout must use the real auth gateway and only report success after confirmed sign-out
- no privileged keys or provider secrets belong in the APK

## Testing strategy

Implementation follows TDD on each isolated branch.

### Account/settings

Tests should cover:

- four-item account menu ordering/stable IDs
- master paw resource contract once asset is present
- reduced-motion behavior
- tap debouncing/navigation acknowledgement
- logout result handling

### Renderer

Tests should cover:

- deterministic z-order
- invisible layers skipped
- opacity/rotation/flip transformations
- invalid dimensions rejected before bitmap allocation
- text/font unavailable behavior
- crop clamping
- missing/corrupt media failure
- transparent PNG background
- JPEG opaque background rules
- partial render diagnostics
- required-layer failure preventing successful export

### Thumbnail/export

Tests should cover:

- safe filename/path behavior
- compression failure and zero-byte failure
- COMPLETE requiring real output metadata/reference
- unsupported export types remaining truthful
- cancellation state

### Regression

Existing auth, Studio, Rive and CI tests must continue to pass. No previous GREEN result is carried forward to a new commit without re-verification.

## Delivery order

1. Finish the current auth/account RED-to-GREEN foundation on PR #25 without expanding scope.
2. Add/verify the exact `patsy_master_paw` drawable through the approved asset pipeline.
3. Integrate the account dropdown and Remember Me micro-interaction against the shared visual system.
4. Complete the current THyNK PR #24 RED requirements before layering renderer/export work on top.
5. Translate the shared renderer against existing Studio models.
6. Add thumbnail generation using the shared renderer.
7. Add truthful image export backed by real output metadata.
8. Keep Replit as prototype validation only.
9. Continue Rive real-Patsy work independently against the existing rig contract.

## Explicit non-goals for this extraction

- no merge to `main`
- no second app/package architecture
- no fake production `.riv`
- no generic replacement paw
- no generic cream/white Maltipoo redesign of real Patsy
- no social-platform branding without permission
- no fake template counts
- no fake export completion
- no new monetization model
- no Supabase redesign
- no replacement of existing auth/security contracts

## Acceptance criteria

This extraction is successful when:

- useful pasted concepts are represented inside existing native contracts rather than duplicated beside them
- locked Patsy/THyNK visuals remain unchanged
- account/settings UI compiles and passes tests with the real master paw asset
- the Studio renderer consumes the current `StudioProject`/`StudioLayer` model
- image/thumbnail output is real and verified, not placeholder output
- missing assets/fonts fail truthfully
- Android CI is GREEN on the relevant isolated branch after each implementation stage
- PRs remain unmerged until explicit approval
