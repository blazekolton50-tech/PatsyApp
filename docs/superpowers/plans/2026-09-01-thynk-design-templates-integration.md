# THyNK Design & Templates Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Route the locked Design & Templates category into the one generic THyNK image canvas, expose only verified donor templates, and autosave/restore editable design drafts without conflating project state with exported PNGs.

**Architecture:** Build on the donor manifest from Job 1 and canvas/import/export model from Job 2. Add a small design-entry contract, a verified-template adapter, one native Compose design route inside the existing `ThynkStudioScreen`, and an account-scoped DataStore draft repository using a deterministic pure-Kotlin codec. Do not create a second editor or second THyNK home.

**Tech Stack:** Kotlin/JVM tests, Jetpack Compose, existing `androidx.datastore:datastore-preferences:1.1.1`, existing THyNK/canvas models, no new serialization dependency.

**Spec:** `docs/superpowers/specs/2026-08-31-thynk-donor-consolidation-design.md`

## Global Constraints

- Work on `chatgpt/codex-ready-2026-09-01`; keep work Draft and unmerged.
- Execute only after donor-asset intake and generic canvas/image-import/PNG-export jobs are GREEN.
- Preserve the locked ten-category THyNK hub.
- Preserve semantic navigation `HOME · THyNK · CAMERA · PATSY DMS · PROFILE` and the shared outer primary bar.
- Design & Templates must use the generic image canvas; no competing editor architecture.
- Only donor records whose `isProductionReady` is true may become usable production templates.
- Reference-only/rejected wrappers never appear as usable templates.
- Blank Design and Custom Size must work even when there are zero verified templates.
- Autosave stores editable project state; PNG export remains a separate output action.
- Do not claim cloud sync until a real account-backed sync repository is separately implemented.

---

### Task 1: Add stable Design entry routing contract

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/ThynkDesignRouting.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/ThynkDesignRoutingTest.kt`

**Interfaces:**
- Consumes: existing Design & Templates item labels from `ThynkStudioCatalog.kt`.
- Produces: `DesignEntryPoint`, `designEntryForThynkItem(item: String): DesignEntryPoint?`.

- [ ] **Step 1: Write failing routing tests**

```kotlin
package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkDesignRoutingTest {
    @Test fun blankAndCustomHaveDedicatedEntryPoints() {
        assertEquals(DesignEntryPoint.Blank, designEntryForThynkItem("BLANK DESIGNS"))
        assertEquals(DesignEntryPoint.CustomSize, designEntryForThynkItem("CUSTOM SIZE"))
    }

    @Test fun designFormatsOpenTemplateBrowserWithStableKind() {
        assertEquals(DesignEntryPoint.Templates("posters"), designEntryForThynkItem("POSTERS"))
        assertEquals(DesignEntryPoint.Templates("flyers"), designEntryForThynkItem("FLYERS"))
        assertEquals(DesignEntryPoint.Templates("templates"), designEntryForThynkItem("TEMPLATES"))
    }

    @Test fun nonDesignItemDoesNotCreateDesignRoute() {
        assertNull(designEntryForThynkItem("VIDEO EDITOR"))
    }
}
```

- [ ] **Step 2: Run test to verify RED**

Run:
```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkDesignRoutingTest'
```
Expected: FAIL because the design routing contract does not exist.

- [ ] **Step 3: Implement minimal stable mapping**

```kotlin
package com.patsy.app.thynk

sealed interface DesignEntryPoint {
    data object Blank : DesignEntryPoint
    data object CustomSize : DesignEntryPoint
    data class Templates(val kind: String) : DesignEntryPoint
}

private val designTemplateKinds = mapOf(
    "POSTERS" to "posters",
    "FLYERS" to "flyers",
    "INVITATIONS" to "invitations",
    "CARDS" to "cards",
    "MENUS" to "menus",
    "PRICE LISTS" to "price-lists",
    "SIGNS" to "signs",
    "CERTIFICATES" to "certificates",
    "BROCHURES" to "brochures",
    "LABELS" to "labels",
    "TEMPLATES" to "templates",
)

fun designEntryForThynkItem(item: String): DesignEntryPoint? = when (item.trim().uppercase()) {
    "BLANK DESIGNS" -> DesignEntryPoint.Blank
    "CUSTOM SIZE" -> DesignEntryPoint.CustomSize
    else -> designTemplateKinds[item.trim().uppercase()]?.let(DesignEntryPoint::Templates)
}
```

- [ ] **Step 4: Run targeted test and full THyNK tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkDesignRoutingTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.*'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk/ThynkDesignRouting.kt app/src/test/java/com/patsy/app/thynk/ThynkDesignRoutingTest.kt
git commit -m "feat: add THyNK design entry routing"
```

### Task 2: Adapt verified donor templates without leaking reference-only assets

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/templates/VerifiedStudioTemplateCatalog.kt`
- Create: `app/src/test/java/com/patsy/app/studio/templates/VerifiedStudioTemplateCatalogTest.kt`

**Interfaces:**
- Consumes: `DonorAssetManifest`, `DonorAssetType.TEMPLATE`, `DonorAssetRecord.isProductionReady` from Job 1.
- Produces: `VerifiedStudioTemplateRef`, `verifiedStudioTemplates(manifest, kind)`.

- [ ] **Step 1: Write failing filtering tests**

```kotlin
package com.patsy.app.studio.templates

import com.patsy.app.studio.assets.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerifiedStudioTemplateCatalogTest {
    private fun record(id: String, availability: DonorAssetAvailability, license: DonorLicenseStatus) =
        DonorAssetRecord(
            id = id,
            origin = "Owner supplied",
            category = "design",
            subcategory = "posters",
            type = DonorAssetType.TEMPLATE,
            location = "res/raw/$id.json",
            licenseStatus = license,
            duplicateStatus = DonorDuplicateStatus.UNIQUE,
            checksumSha256 = "abc123",
            availability = availability,
        )

    @Test fun onlyProductionReadyTemplatesAreReturned() {
        val manifest = DonorAssetManifest(listOf(
            record("verified", DonorAssetAvailability.VERIFIED, DonorLicenseStatus.VERIFIED_FOR_APP),
            record("reference", DonorAssetAvailability.REFERENCE_ONLY, DonorLicenseStatus.UNKNOWN),
        ))
        assertEquals(listOf("verified"), verifiedStudioTemplates(manifest, "posters").map { it.id })
    }

    @Test fun unknownKindReturnsEmptyList() {
        val manifest = DonorAssetManifest(listOf(record("verified", DonorAssetAvailability.VERIFIED, DonorLicenseStatus.VERIFIED_FOR_APP)))
        assertTrue(verifiedStudioTemplates(manifest, "menus").isEmpty())
    }
}
```

- [ ] **Step 2: Run test to verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.templates.VerifiedStudioTemplateCatalogTest'
```
Expected: FAIL because the template adapter does not exist.

- [ ] **Step 3: Implement the verified-only adapter**

```kotlin
package com.patsy.app.studio.templates

import com.patsy.app.studio.assets.DonorAssetManifest
import com.patsy.app.studio.assets.DonorAssetType

data class VerifiedStudioTemplateRef(
    val id: String,
    val kind: String,
    val location: String,
)

fun verifiedStudioTemplates(
    manifest: DonorAssetManifest,
    kind: String,
): List<VerifiedStudioTemplateRef> = manifest.productionReady()
    .asSequence()
    .filter { it.type == DonorAssetType.TEMPLATE }
    .filter { it.category == "design" }
    .filter { it.subcategory == kind }
    .map { VerifiedStudioTemplateRef(it.id, it.subcategory, it.location) }
    .sortedBy { it.id }
    .toList()
```

- [ ] **Step 4: Run template and donor tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.templates.*'
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.*'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/templates app/src/test/java/com/patsy/app/studio/templates
git commit -m "feat: expose only verified THyNK templates"
```

### Task 3: Add account-scoped editable-draft codec and real local DataStore persistence

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/drafts/StudioDraftSnapshot.kt`
- Create: `app/src/main/java/com/patsy/app/studio/drafts/StudioDraftCodec.kt`
- Create: `app/src/main/java/com/patsy/app/studio/drafts/DataStoreStudioDraftRepository.kt`
- Create: `app/src/test/java/com/patsy/app/studio/drafts/StudioDraftCodecTest.kt`

**Interfaces:**
- Consumes: `StudioCanvasState`, `StudioCanvasSize`, `StudioCanvasElement`, `CanvasElementType`, `CanvasTransform` from Job 2.
- Produces: `StudioDraftSnapshot.fromCanvas(projectId, state)`, `StudioDraftCodec.encode/decode`, `StudioDraftRepository.load/save/clear`.

- [ ] **Step 1: Write failing round-trip tests**

```kotlin
package com.patsy.app.studio.drafts

import com.patsy.app.studio.canvas.*
import kotlin.test.Test
import kotlin.test.assertEquals

class StudioDraftCodecTest {
    @Test fun editableCanvasRoundTripsWithoutExportState() {
        val state = StudioCanvasState(
            canvasSize = StudioCanvasSize(1080, 1350),
            elements = listOf(
                StudioCanvasElement(
                    id = "image-1",
                    type = CanvasElementType.IMAGE,
                    content = "content://patsy/image/1",
                    transform = CanvasTransform(centerX = 0.4f, centerY = 0.6f, rotationDegrees = 15f),
                )
            ),
            selectedElementId = "image-1",
        )
        val original = StudioDraftSnapshot.fromCanvas("draft-1", state)
        assertEquals(original, StudioDraftCodec.decode(StudioDraftCodec.encode(original)))
    }
}
```

- [ ] **Step 2: Run test to verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.drafts.StudioDraftCodecTest'
```
Expected: FAIL because draft snapshot/codec do not exist.

- [ ] **Step 3: Implement snapshot and deterministic codec**

Use a versioned text format with one header line and one Base64-URL-safe element line per canvas element. Use `java.util.Base64.getUrlEncoder().withoutPadding()` / `getUrlDecoder()` so IDs/content cannot break delimiters. Encode every transform field explicitly. `decode` must return `null` for malformed/unknown versions instead of manufacturing a draft.

Required snapshot shape:

```kotlin
data class StudioDraftSnapshot(
    val version: Int = 1,
    val projectId: String,
    val widthPx: Int,
    val heightPx: Int,
    val selectedElementId: String?,
    val elements: List<StudioCanvasElement>,
) {
    fun toCanvas(): StudioCanvasState = StudioCanvasState(
        StudioCanvasSize(widthPx, heightPx),
        elements,
        selectedElementId?.takeIf { id -> elements.any { it.id == id } },
    )

    companion object {
        fun fromCanvas(projectId: String, state: StudioCanvasState) = StudioDraftSnapshot(
            projectId = projectId,
            widthPx = state.canvasSize.widthPx,
            heightPx = state.canvasSize.heightPx,
            selectedElementId = state.selectedElementId,
            elements = state.elements,
        )
    }
}
```

- [ ] **Step 4: Implement account-scoped DataStore repository**

Use the existing `androidx.datastore.preferences` dependency. The repository key must include a sanitized account scope plus project ID, for example `studio_draft_<sha256/account-safe-key>_<projectId>`. Store only the codec string. `load` returns null when absent/malformed; `save` writes the current editable draft; `clear` removes it.

Interface:

```kotlin
interface StudioDraftRepository {
    suspend fun load(projectId: String): StudioDraftSnapshot?
    suspend fun save(snapshot: StudioDraftSnapshot)
    suspend fun clear(projectId: String)
}
```

Do not store auth tokens or passwords in this repository.

- [ ] **Step 5: Run draft tests and full unit suite**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.drafts.*'
./gradlew testDebugUnitTest --stacktrace
```
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/drafts app/src/test/java/com/patsy/app/studio/drafts
git commit -m "feat: autosave editable THyNK design drafts"
```

### Task 4: Wire one Design route into the existing THyNK screen

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Create: `app/src/main/java/com/patsy/app/studio/design/ThynkDesignScreen.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/ThynkDesignRoutingIntegrationTest.kt`

**Interfaces:**
- Consumes: Tasks 1-3 plus the Job 2 canvas screen/state/history/import/export APIs.
- Produces: `ThynkStudioScreen(accountScopeKey: String)` and native `ThynkDesignScreen`.

- [ ] **Step 1: Add failing route integration test**

```kotlin
@Test fun designItemsResolveBeforeVideoEditorRouting() {
    assertEquals(DesignEntryPoint.Blank, designEntryForThynkItem("BLANK DESIGNS"))
    assertEquals(null, editorPageForThynkItem("BLANK DESIGNS"))
}
```

- [ ] **Step 2: Run RED if current generic editor routing captures design items incorrectly**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkDesignRoutingIntegrationTest'
```
Expected: RED until the current route resolver and screen route support the Design path.

- [ ] **Step 3: Extend `ThynkRoute` with one Design route**

Add:

```kotlin
data class Design(val entry: DesignEntryPoint) : ThynkRoute
```

In the Design category callback, call `designEntryForThynkItem(item)` first and route to `ThynkRoute.Design(entry)`. Do not change Music routing or the current video-editor route.

Back behavior:
- Design -> Design & Templates category
- existing Music/video behavior unchanged

- [ ] **Step 4: Pass account scope without changing authorization**

Change `ThynkStudioScreen()` to `ThynkStudioScreen(accountScopeKey: String)`.

From `FinalMainActivity`, pass:

```kotlin
ThynkStudioScreen(accountScopeKey = session?.userId ?: "debug-preview")
```

This value scopes local draft storage only; it must never grant route/Owner authority.

- [ ] **Step 5: Implement native Design screen behavior**

`ThynkDesignScreen` must:
- Blank: open the Job 2 canvas with `StudioCanvasPresets.SQUARE` as the initial default.
- Custom Size: require valid width/height through `validateCustomCanvasSize`; only then create the canvas.
- Templates(kind): call `verifiedStudioTemplates(...)`; if empty, show `No verified templates are available yet.` and keep Blank/Custom options usable.
- never show reference-only/rejected donor records.
- render/import/export through the Job 2 generic canvas components; do not duplicate bitmap/export logic.

- [ ] **Step 6: Run THyNK/design tests and builds**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.*'
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.*'
./gradlew assembleDebug --stacktrace
```
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt app/src/main/java/com/patsy/app/FinalMainActivity.kt app/src/main/java/com/patsy/app/studio/design app/src/test/java/com/patsy/app/thynk
git commit -m "feat: route THyNK Design into the native canvas"
```

### Task 5: Autosave/restore while navigating and keep export independent

**Files:**
- Modify: `app/src/main/java/com/patsy/app/studio/design/ThynkDesignScreen.kt`
- Create: `app/src/test/java/com/patsy/app/studio/drafts/StudioDraftSemanticsTest.kt`

**Interfaces:**
- Consumes: `StudioDraftRepository`, `StudioDraftSnapshot`, canvas history/state.
- Produces: restore-on-entry + save-on-edit/departure behavior.

- [ ] **Step 1: Write failing semantic tests**

```kotlin
@Test fun exportResultIsNotPartOfEditableDraftSnapshot() {
    val properties = StudioDraftSnapshot::class.members.map { it.name }.toSet()
    assertTrue("elements" in properties)
    assertTrue("projectId" in properties)
    assertFalse("exportUri" in properties)
}

@Test fun missingDraftDoesNotCreateFakeTemplateContent() {
    val empty = StudioCanvasState.empty(StudioCanvasPresets.SQUARE.size)
    assertTrue(empty.elements.isEmpty())
}
```

- [ ] **Step 2: Run targeted tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.drafts.StudioDraftSemanticsTest'
```
Expected: PASS only after the draft model remains export-independent.

- [ ] **Step 3: Add restore/save lifecycle**

On route entry, load the stable project ID for the current draft and restore the canvas if a valid snapshot exists. On meaningful canvas state change, debounce approximately 300-500 ms and persist the current snapshot. On composable disposal/navigation-away, flush the latest snapshot in the active coroutine scope before releasing the screen state.

Do not show a fake `Saved` state until the repository write returns successfully. A failed local save should show a non-destructive retry/status message while preserving in-memory edits.

- [ ] **Step 4: Verify navigation keeps the shared outer bar**

Manually inspect code to confirm `ThynkDesignScreen` is nested inside `ThynkStudioScreen`, which remains inside the authenticated `FinalMainActivity` shell. Do not add any Design-owned bottom navigation.

- [ ] **Step 5: Run final verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0.

- [ ] **Step 6: Push and wait for fresh Patsy Consolidation CI**

Record exact starting SHA, ending SHA, changed files, RED/GREEN evidence, final CI run, artifact IDs/digests, and remaining physical-device limits. Keep Draft and unmerged.
