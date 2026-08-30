# THyNK Local Catalogue + Temporary Drive Staging Design

**Date:** 2026-08-30  
**Status:** APPROVED DESIGN / implementation not yet complete  
**Scope:** Native Android/Kotlin Patsy app and existing provider-neutral service architecture

## Goal

Translate the useful THyNK Studio catalogue/editor concepts from the supplied web prototype into the existing native Patsy Android architecture without importing the prototype wholesale, changing the locked visual system, exposing third-party branding, or pretending unfinished provider-backed capabilities are complete.

## Source-of-truth constraints

- Production path remains native Android/Kotlin.
- THyNK remains the creation area name.
- Locked visual system remains black/charcoal, white typography, white primary buttons with dark text and restrained rainbow accents.
- Visible third-party social platform branding/names are not used unless permission is verified for that exact integration.
- Size selection is platform-neutral and uses shape diagrams, aspect ratios and pixel dimensions. User-facing wording may say `All social media sizing available`.
- Default flow is Category -> Sub-category -> Template -> Size, with Size permitted earlier when required to filter incompatible templates.
- Custom Size remains available.
- Production Patsy animation remains honest; no fake `.riv`, GIF, sprite or static-pose substitution.
- Provider-backed AI/image/video/export actions must never report success without real provider confirmation.
- Secrets never ship in the Android client.

## Architecture decision

### 1. Built-in catalogue is local-first

The app ships a compact typed catalogue for fast/offline browsing. Catalogue metadata, size presets, lightweight SVG/vector elements and optimized preview thumbnails may be bundled with the app.

Heavy originals should not all be bundled in the initial APK. Large source images, high-resolution editable assets, audio packs and video/template source packages are staged outside the app and imported/downloaded only when needed.

### 2. Google Drive is temporary development storage, not the production user-data backend

Google Drive may be used during development for:

- approved asset-source originals;
- large THyNK source packs;
- build-time imports;
- manual backups;
- reference exports;
- temporary staging before migration to production object storage.

The Android app must NOT contain personal Google account credentials, OAuth refresh tokens, Drive account passwords or a hard-coded list of developer Drive accounts.

The app must NOT pool several personal Drive accounts as hidden production storage for users.

Drive is therefore outside the runtime security boundary. Developer tooling or a manual build/import process may copy approved assets from Drive into the local catalogue/package. The runtime app sees only approved bundled assets or provider-neutral remote-asset references.

### 3. User data remains behind Patsy service contracts

User projects, saves, uploads, exports, cross-device state and private media continue to flow through existing authenticated service interfaces. Supabase remains the intended production backend/object-storage path once those adapters are configured and verified.

The THyNK implementation must not directly depend on Supabase SDK details in UI code. Provider-specific code belongs behind service/repository adapters.

## Native package structure

Add focused native packages under `app/src/main/java/com/patsy/app/studio/`:

- `catalog/` - catalogue models, repository and built-in loader.
- `sizing/` - platform-neutral canvas-size presets and compatibility rules.
- `editor/` - canvas document, layers, transforms, selection and undo/redo actions.
- `effects/` - typed filters/effects/transitions/text animations/overlays.
- `timeline/` - video/audio/text/effect timeline models and editing operations.
- `media/` - media references, import/download state and asset availability.
- `export/` - provider-neutral export requests/results and truthful availability states.
- `camera/` - camera capability contract and truthful unconfigured/available states.
- `project/` - editable project model, version/save intent and repository contracts.

Keep backend/provider operations under existing `services/studio/` boundaries unless a new narrow service contract is required.

## Catalogue model

The supplied 1,110-record structure is translated into typed metadata rather than copied as web JSON/HTML behavior.

Each catalogue item should include at minimum:

- stable ID;
- category;
- sub-category;
- display name;
- item type;
- preview reference;
- source-asset reference when available;
- editable capability metadata;
- supported aspect ratios/sizes;
- custom-size allowance;
- reflow strategy;
- tags/search terms;
- license/origin metadata;
- availability state (`BUNDLED`, `STAGED`, `REMOTE_AVAILABLE`, `UNAVAILABLE`);
- whether it is genuinely completed vs metadata-only.

Do not mark a generated metadata record as a finished visual template.

## Initial catalogue families translated from the prototype

Useful catalogue families to preserve as typed data:

- Templates: CVs, posters, social/general-share designs, business cards, slides, schedules, to-dos.
- Elements: stickers, icons, illustrations, patterns.
- Media: images, generic logos/marks, user uploads.
- Style: fonts, effects, filters, transitions, text animations, overlays.
- Mockups: generic phone/laptop/poster/product presentation mockups.

Visible social-template sub-categories must be platform-neutral. Do not use `Instagram Post`, `TikTok Cover`, `Reel`, `Story`, `YouTube Thumbnail`, etc. in the user-facing catalogue.

Suggested neutral grouping:

- Square
- Portrait
- Full Vertical
- Landscape
- Wide
- Custom Size

The catalogue may internally store compatibility with standardized pixel dimensions, but those dimensions are represented by shape/ratio/pixel data rather than third-party brand labels.

## Size preset model

A typed `CanvasSizePreset` should carry:

- `id`
- `displayName`
- `widthPx`
- `heightPx`
- `aspectRatioLabel`
- `shapeKind`
- `categoryCompatibility`
- `isCustom`

Initial approved neutral presets:

- Square - 1080 x 1080 - 1:1
- Portrait - 1080 x 1350 - 4:5
- Full Vertical - 1080 x 1920 - 9:16
- Landscape - 1920 x 1080 - 16:9
- Wide - 1200 x 628 - 1.91:1
- Custom Size - user width x height

These are product presets, not claims of endorsement by any social platform.

## Editor model translated from the prototype

Translate the prototype's useful state concepts into native models:

- layer ID and stable source item ID;
- x/y position;
- width/height;
- rotation;
- z-order;
- visibility;
- lock state;
- opacity;
- editable text fields;
- applied filter/effect IDs;
- crop/flip state where supported.

Initial native operations should be implemented as explicit commands/actions so undo/redo can be deterministic:

- add;
- select;
- move;
- resize;
- rotate;
- duplicate;
- delete;
- reorder;
- lock/unlock;
- hide/show;
- opacity;
- basic text updates;
- apply/remove filter/effect;
- undo/redo.

Do not claim crop, cutout, background removal, AI replace/expand or advanced video rendering complete until their implementations and tests exist.

## Filters/effects catalogue translation

The supplied prototype contains useful names and CSS-style parameter ideas. Translate only concepts that can be implemented natively and safely.

Create typed definitions for 25 filters and 25 effects plus catalogues for transitions, text animations and overlays. Each definition must explicitly state whether it is:

- `IMPLEMENTED_NATIVE`
- `CATALOGUE_ONLY`
- `PROVIDER_REQUIRED`
- `UNAVAILABLE`

This prevents a named effect from being presented as working when only metadata exists.

## Video timeline translation

Create native timeline data models before rendering-engine work:

- project duration;
- tracks;
- clips;
- clip source reference;
- start/end trim;
- timeline start;
- z/track ordering;
- text/effect/filter/transition/overlay attachment references;
- audio volume/fade metadata.

Timeline editing operations may be implemented independently of final export. Export must remain unavailable until a real Android-compatible render/encoding path is verified.

## Camera translation

The prototype's empty `CameraEngine.init()` is not imported as functionality. Native camera work begins as a capability contract and UI state:

- AVAILABLE only after camera implementation/permission/device support is verified;
- PERMISSION_REQUIRED when relevant;
- NOT_CONFIGURED/UNAVAILABLE otherwise.

No camera success state is fabricated.

## Export translation

The prototype's WebM recorder named `exportMP4Android` and fake `/api/export` response are explicitly rejected.

Native export contracts must distinguish:

- image export;
- video export;
- audio export;
- project package/export.

Each result must include real output metadata after successful completion. MP4 must not be reported unless a genuine MP4-compatible file has been created and validated.

## Project persistence

Keep editable projects separate from exported media and retained/locked media.

Project repository contracts should support:

- create;
- load;
- save/update;
- list;
- delete;
- thumbnail reference;
- version/revision token where supported;
- ownership through authenticated context.

Local draft caching may exist for resilience, but cross-device/private production state is server-backed when configured.

## Temporary Drive folder strategy

For development staging, use one logical root such as:

`Patsy THyNK Development Assets/`

Suggested folders:

- `00_MANIFESTS/`
- `01_TEMPLATE_SOURCES/`
- `02_TEMPLATE_PREVIEWS/`
- `03_ELEMENTS/`
- `04_IMAGES/`
- `05_FONTS_LICENSES/`
- `06_AUDIO_ORIGINAL/`
- `07_VIDEO_TEMPLATE_SOURCES/`
- `08_MOCKUPS_GENERIC/`
- `09_APPROVED_FOR_APP_IMPORT/`
- `90_BACKUPS/`

Multiple developer-owned Drives can mirror or hold overflow folders manually, but the app never needs to know how many accounts exist.

## Storage target

Initial installed THyNK catalogue should target roughly 100-250 MB of bundled/local content by optimizing previews and vectors and excluding most heavy originals from the APK.

The exact size is a build metric, not a locked entitlement. CI/build tooling should report packaged catalogue size so growth is visible.

## Security boundaries

- No arbitrary filesystem path supplied by a client is read by the backend.
- No fake project IDs or fake export URLs.
- No client-side service-role/provider secrets.
- No Drive credentials in source control or APK.
- Authenticated ownership remains server-authoritative for private user data.
- Under-16 and capability policies remain upstream of Studio provider actions.
- Third-party branding remains absent unless separately verified/approved.

## Testing strategy

Add unit tests before implementation for:

1. neutral size presets and custom-size validation;
2. catalogue stable IDs and category/sub-category mapping;
3. absence of forbidden visible third-party social labels in default catalogue data;
4. layer add/move/resize/rotate/delete/reorder;
5. undo/redo state restoration;
6. effect/filter availability-state truthfulness;
7. timeline trim/order model behavior;
8. project repository contracts/failure state mapping;
9. no fake export success states;
10. packaged catalogue-size reporting where practical.

## Non-goals for this phase

This phase does not claim completion of:

- production Supabase auth adapter;
- production user-media storage;
- production AI image/video generation;
- production MP4 rendering/export;
- production camera filters/face tracking;
- production Rive asset;
- 1,110 individually finished visual designs;
- full music creator;
- final community-template moderation pipeline.

Those remain subsequent implementation tracks.

## Migration path from Google Drive

Because Drive is staging-only, later migration is straightforward:

1. move approved heavy assets to production object storage/CDN;
2. update provider-neutral asset references/manifests;
3. keep stable catalogue IDs unchanged;
4. remove the manual/build-time Drive sync step;
5. verify hashes, licensing metadata and app previews before cutover.

No Android UI rewrite should be required for this migration.
