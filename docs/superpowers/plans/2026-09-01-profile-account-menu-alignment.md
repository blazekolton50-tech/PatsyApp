# Profile & Account Menu Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the legacy `More`-style final-profile surface with the latest locked Profile layout and make the exact five-item top-right account menu consistently route to secondary account pages without changing the five primary destinations or weakening authorization.

**Architecture:** Add pure contracts for account-menu order and profile sections, then create focused Compose files under `ui/finaldesign` instead of expanding legacy `MainActivity.kt`. Hoist secondary page/menu routing into `FinalMainActivity`, keep Owner checks exactly server-backed, and reuse existing final design tokens/logo assets. Secondary Account/About/Settings/Remember Me pages are not primary bottom destinations.

**Tech Stack:** Kotlin/JVM unit tests, Jetpack Compose/Material3, current auth/bootstrap/Owner contracts, existing final design tokens.

**Spec:** `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`

## Global Constraints

- Work on `chatgpt/codex-ready-2026-09-01`; keep Draft and unmerged.
- Execute after the main THyNK donor/canvas/design/music jobs remain GREEN.
- Primary semantic destinations remain exactly `HOME · THyNK · CAMERA · PATSY DMS · PROFILE`.
- Schedule and Calendar remain secondary tools, never primary bottom tabs.
- Top-right account menu order is exactly `Account · About · Profile · Settings · Remember Me`.
- Owner Profile/Owner Tools stay server-authorized and fail closed.
- Do not invent bio/social/gallery/project data when it is absent; render truthful empty states.
- Do not turn Remember Me into plaintext password storage.
- Preserve the approved centered Patsy logo/chrome and realistic-main-Patsy boundary.

---

### Task 1: Lock account-menu and profile-section contracts under unit tests

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContract.kt`
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileContract.kt`
- Create: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContractTest.kt`
- Create: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalProfileContractTest.kt`

**Interfaces:**
- Produces: `FinalAccountMenuDestination`, `FinalAccountMenuContract.items`, `FinalProfileSection`, `FinalProfileContract.sections`.

- [ ] **Step 1: Write failing account-menu order test**

```kotlin
package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals

class FinalAccountMenuContractTest {
    @Test fun accountMenuOrderIsLocked() {
        assertEquals(
            listOf("Account", "About", "Profile", "Settings", "Remember Me"),
            FinalAccountMenuContract.items.map { it.label },
        )
    }
}
```

- [ ] **Step 2: Write failing profile-section test**

```kotlin
package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals

class FinalProfileContractTest {
    @Test fun profileSectionsKeepSecondaryToolsOutOfPrimaryNavigation() {
        assertEquals(
            listOf("Identity", "Bio & Social", "Gallery", "Recent Projects", "Saved Projects", "Schedule & Calendar", "Remember Me"),
            FinalProfileContract.sections.map { it.label },
        )
    }
}
```

- [ ] **Step 3: Run tests to verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest'
```
Expected: FAIL because the contracts do not exist.

- [ ] **Step 4: Implement the contracts**

```kotlin
package com.patsy.app.ui.finaldesign

enum class FinalAccountMenuDestination { ACCOUNT, ABOUT, PROFILE, SETTINGS, REMEMBER_ME }

data class FinalAccountMenuItem(val destination: FinalAccountMenuDestination, val label: String)

object FinalAccountMenuContract {
    val items = listOf(
        FinalAccountMenuItem(FinalAccountMenuDestination.ACCOUNT, "Account"),
        FinalAccountMenuItem(FinalAccountMenuDestination.ABOUT, "About"),
        FinalAccountMenuItem(FinalAccountMenuDestination.PROFILE, "Profile"),
        FinalAccountMenuItem(FinalAccountMenuDestination.SETTINGS, "Settings"),
        FinalAccountMenuItem(FinalAccountMenuDestination.REMEMBER_ME, "Remember Me"),
    )
}
```

```kotlin
package com.patsy.app.ui.finaldesign

enum class FinalProfileSection(val label: String) {
    IDENTITY("Identity"),
    BIO_SOCIAL("Bio & Social"),
    GALLERY("Gallery"),
    RECENT_PROJECTS("Recent Projects"),
    SAVED_PROJECTS("Saved Projects"),
    SCHEDULE_CALENDAR("Schedule & Calendar"),
    REMEMBER_ME("Remember Me"),
}

object FinalProfileContract { val sections = FinalProfileSection.entries.toList() }
```

- [ ] **Step 5: Run tests and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest'
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContract.kt app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileContract.kt app/src/test/java/com/patsy/app/ui/finaldesign
git commit -m "test: lock profile and account menu contracts"
```

### Task 2: Build reusable top-right account menu UI with exact actions

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenu.kt`
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/NativeCameraHub.kt`

**Interfaces:**
- Consumes: `FinalAccountMenuContract.items`.
- Produces: `FinalAccountMenuButton`, `FinalAccountMenuPopup`, `onOpenAccountMenu` callbacks on Home/THyNK/Camera headers.

- [ ] **Step 1: Add a pure selection helper and failing test**

Add to `FinalAccountMenuContract.kt`:

```kotlin
fun accountMenuDestinationForLabel(label: String): FinalAccountMenuDestination? =
    FinalAccountMenuContract.items.firstOrNull { it.label == label }?.destination
```

Add test:

```kotlin
@Test fun profileLabelRoutesToProfileWithoutChangingPrimaryContract() {
    assertEquals(FinalAccountMenuDestination.PROFILE, accountMenuDestinationForLabel("Profile"))
    assertEquals(null, accountMenuDestinationForLabel("Schedule"))
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest'
```
Expected: RED before helper implementation, then PASS after implementation.

- [ ] **Step 2: Create reusable menu composables**

`FinalAccountMenuButton(onClick)` uses the existing rainbow/charcoal circular treatment and visible `•••` semantics.

`FinalAccountMenuPopup(expanded, onDismiss, onSelect)` renders exactly the five contract items in contract order. It must not add Schedule, Calendar, Owner Tools, sign out or any sixth primary destination.

- [ ] **Step 3: Replace decorative ellipses with real callback buttons**

Change signatures:

```kotlin
fun FinalHomeScreen(
    onNavigate: (FinalHomeDestination) -> Unit,
    onOpenAccountMenu: () -> Unit,
    onAskPatsy: () -> Unit = {},
    onCreatePost: () -> Unit = {},
)
```

```kotlin
fun ThynkStudioScreen(
    accountScopeKey: String,
    onOpenAccountMenu: () -> Unit,
)
```

```kotlin
fun NativeCameraHub(
    onOpenThynk: () -> Unit = {},
    onOpenAccountMenu: () -> Unit = {},
)
```

Use `FinalAccountMenuButton` in the current top-right location. Do not alter the centered logo or bottom navigation.

- [ ] **Step 4: Run unit tests and debug build**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.*'
./gradlew assembleDebug --stacktrace
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/ui/finaldesign app/src/main/java/com/patsy/app/thynk
git commit -m "feat: wire locked top-right account menu"
```

### Task 3: Create truthful final Profile screen and stop using legacy `More` as the final profile UI

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Test: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalProfileContractTest.kt`

**Interfaces:**
- Consumes: current `Profile` session model, Owner capability booleans/callbacks, `FinalProfileContract.sections`.
- Produces: `FinalProfileViewState`, `FinalProfileScreen`.

- [ ] **Step 1: Add failing truth-state test**

```kotlin
@Test fun absentOptionalProfileContentRemainsEmpty() {
    val state = FinalProfileViewState(
        displayName = "Blaze",
        username = "blaze",
        maskedEmail = "b***@example.com",
        emailVerified = true,
        bio = null,
        socialLinks = emptyList(),
        galleryCount = 0,
        recentProjectCount = 0,
        savedProjectCount = 0,
    )
    assertEquals(null, state.bio)
    assertEquals(0, state.galleryCount)
    assertEquals(0, state.recentProjectCount)
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest'
```
Expected: RED because `FinalProfileViewState` does not exist.

- [ ] **Step 2: Implement truthful view state**

```kotlin
data class FinalProfileViewState(
    val displayName: String,
    val username: String,
    val maskedEmail: String,
    val emailVerified: Boolean,
    val bio: String?,
    val socialLinks: List<String>,
    val galleryCount: Int,
    val recentProjectCount: Int,
    val savedProjectCount: Int,
)
```

Do not seed fake bio, links, media or projects. Use `Not set`, `No gallery items yet`, `No recent projects yet` and `No saved projects yet` only as empty-state copy.

- [ ] **Step 3: Implement `FinalProfileScreen`**

Use existing `FinalCharcoal`, `FinalCard`, `FinalRainbow`, `FinalWhite`, `FinalMuted` and approved logo asset. Render:
- identity/avatar area using real available profile/session data
- Bio & Social empty/real state
- Gallery
- Recent Projects
- Saved Projects
- Schedule & Calendar secondary actions
- Remember Me secondary action
- Owner Profile and Owner Tools buttons only when their current server-authorized booleans are true
- sign out as an account action, not a primary navigation destination

- [ ] **Step 4: Replace Final `More(...)` call only**

In `FinalMainActivity`, replace the `FinalAppPage.PROFILE -> More(...)` final-shell call with `FinalProfileScreen(...)`. Do not delete legacy `MainActivity.kt` in this task; it remains non-launcher recovery code until separately cleaned up.

Preserve all existing Owner authorization refresh/expiry checks and callbacks.

- [ ] **Step 5: Run tests/build and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.*'
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileScreen.kt app/src/main/java/com/patsy/app/FinalMainActivity.kt app/src/test/java/com/patsy/app/ui/finaldesign
git commit -m "feat: align final Profile with locked layout"
```

### Task 4: Add secondary Account/About/Settings/Remember Me routes without altering the five primary destinations

**Files:**
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalSecondaryAccountScreens.kt`
- Modify: `app/src/test/java/com/patsy/app/navigation/FinalShellNavigationTest.kt`

**Interfaces:**
- Consumes: `FinalAccountMenuDestination`.
- Produces: secondary `FinalAppPage` values `ACCOUNT`, `ABOUT`, `SETTINGS`, `REMEMBER_ME` and secondary screens.

- [ ] **Step 1: Add test that primary navigation remains five destinations**

Keep/add an assertion against the existing primary navigation contract so adding secondary pages cannot expand the bottom bar:

```kotlin
@Test fun accountMenuPagesDoNotBecomePrimaryDestinations() {
    assertEquals(
        listOf("Home", "THyNK", "PDMs", "Profile"),
        FinalVisualContract.primaryVisibleTextLabels,
    )
    assertEquals(5, FinalVisualContract.primarySemanticDestinationCount)
}
```

If the existing visual contract exposes equivalent constants under different names, use those exact existing constants rather than adding duplicate truth.

- [ ] **Step 2: Add secondary page enum values and menu mapping**

Map:
- Account -> `FinalAppPage.ACCOUNT`
- About -> `FinalAppPage.ABOUT`
- Profile -> `FinalAppPage.PROFILE`
- Settings -> `FinalAppPage.SETTINGS`
- Remember Me -> `FinalAppPage.REMEMBER_ME`

For the shared bottom bar, all four new secondary pages have `selected = null` or the existing Profile selection behavior chosen by the locked visual contract; they are never new bottom buttons.

- [ ] **Step 3: Implement truthful secondary screens**

`Account`: show only current session/account facts already available to the client (display name/username, masked email, email verification status) plus sign-out/account actions that already exist.

`About`: static app identity/version/legal/about copy only; no fabricated network/provider state.

`Settings`: expose only settings that are genuinely local/wired at implementation time. Missing settings appear disabled/unavailable rather than simulated.

`Remember Me`: if the production user-memory repository is not wired yet, show a clear unavailable state and route copy explaining that saved memories will appear here once the secure memory repository is connected. Do not conflate this page with login password storage.

- [ ] **Step 4: Hoist menu state in FinalMainActivity**

Maintain one `accountMenuExpanded` state. Pass `onOpenAccountMenu` to Home, THyNK and Camera. Add the same top-right button to DMs/Profile/secondary screens using the reusable component. Selecting a menu item closes the menu and changes only the secondary page route.

- [ ] **Step 5: Final verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0.

- [ ] **Step 6: Push and require fresh Patsy Consolidation CI**

Report exact starting/ending SHA, changed files, RED/GREEN evidence, CI run, artifact IDs/digests and remaining device/backend limitations. Keep Draft and unmerged.
