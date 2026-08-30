# THyNK Stage 2 Translation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Translate the safe, reusable parts of the supplied Stage 2 web prototype into the existing native Android/Kotlin Patsy THyNK architecture without importing fake-complete provider behavior.

**Architecture:** Keep the local catalogue and editor core as pure Kotlin domain code under `com.patsy.app.studio`. Keep private user persistence behind the existing authenticated service boundary. Camera/export remain truthful contracts until native implementations are separately verified.

**Tech Stack:** Kotlin/JVM, Android app module, JUnit 4, existing Patsy service contracts.

**Spec:** `docs/superpowers/specs/2026-08-30-thynk-local-catalog-drive-dev-design.md`

## Global Constraints

- Production remains native Android/Kotlin.
- User-facing sizing is platform-neutral.
- Locked presets are Square 1080x1080, Portrait 1080x1350, Full Vertical 1080x1920, Landscape 1920x1080, Wide 1200x628, plus Custom Size.
- Catalogue count 1,110 is a planning structure, not proof of 1,110 completed designs.
- Imported effects remain `CATALOGUE_ONLY` until a native renderer is tested.
- No fake MP4 success, camera availability, provider success, Supabase credentials, Drive credentials, or production `.riv` claims.
- Private projects remain authenticated and owner-scoped through service contracts.

---

### Task 1: Neutral sizing and template reflow

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/sizing/CanvasSizing.kt`
- Test: `app/src/test/java/com/patsy/app/studio/StudioCoreModelsTest.kt`

- [ ] Add the locked neutral presets and custom-size validation.
- [ ] Add typed reflow strategies and deterministic layer-bound transforms.
- [ ] Verify platform-brand terms are absent from visible preset labels.

### Task 2: Catalogue and truth-state models

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/catalog/StudioCatalogue.kt`
- Create: `app/src/main/java/com/patsy/app/studio/effects/StudioEffects.kt`

- [ ] Preserve the 1,110 family/count structure as a plan only.
- [ ] Add origin/licence/evidence/bundling metadata.
- [ ] Mark imported filter/effect/transition/text-animation/overlay entries `CATALOGUE_ONLY`.

### Task 3: Editor state and undo/redo

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/editor/StudioEditor.kt`
- Test: `app/src/test/java/com/patsy/app/studio/StudioEditorTest.kt`

- [ ] Implement stable layer models and add/select/move/resize/rotate/z/opacity/lock/visibility/flip/text/filter/effect operations.
- [ ] Implement duplicate/delete plus deterministic undo/redo.
- [ ] Correct the source prototype resize/history issues rather than copying them.

### Task 4: Timeline domain model

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/timeline/StudioTimeline.kt`
- Test: `app/src/test/java/com/patsy/app/studio/StudioTimelineAndTruthTest.kt`

- [ ] Implement tracks/clips/playhead/duration/trim/volume/fades/effect attachment.
- [ ] Validate invalid trim ranges and timeline overflow.
- [ ] Do not add a renderer or claim MP4 completion in this task.

### Task 5: Media/project/camera/export contracts

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/media/StudioMedia.kt`
- Create: `app/src/main/java/com/patsy/app/studio/project/StudioProject.kt`
- Create: `app/src/main/java/com/patsy/app/studio/camera/StudioCamera.kt`
- Create: `app/src/main/java/com/patsy/app/studio/export/StudioExport.kt`

- [ ] Resolve development assets by stable IDs rather than arbitrary paths.
- [ ] Keep project persistence owner-scoped via `AuthenticatedContext` and `ServiceResult`.
- [ ] Prevent unavailable camera states from advertising capture.
- [ ] Prevent `COMPLETE` exports without real output metadata.

### Task 6: Build/test wiring

**Files:**
- Modify: `app/build.gradle.kts`

- [ ] Add JUnit 4 unit-test dependency.
- [ ] Run pure Kotlin smoke compilation for domain files.
- [ ] Run Android unit tests in CI/local Gradle when the Android SDK environment is available.
