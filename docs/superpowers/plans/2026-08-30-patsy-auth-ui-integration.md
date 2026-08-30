# Patsy Auth UI Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Wire relationship-aware greetings, opt-in session restoration, and the exact account menu into the real Patsy Compose shell without changing PR #25 or claiming production auth is configured.

**Architecture:** Put policy and selection logic in small pure-Kotlin auth UI contracts, keep password values exclusively in the existing transient `SecretChars` path, and persist only the Remember Me Boolean through Preferences DataStore. The Compose shell consumes those contracts and the existing `AuthGateway`; no provider implementation or locked visual redesign is introduced.

**Tech Stack:** Kotlin 2.0.21, Android Compose, Preferences DataStore 1.1.1, Kotlin Test, kotlinx-coroutines-test, Gradle 8.10.2, GitHub Actions JDK 21.

**Spec:** `docs/codex/PATSY_AUTH_UI_INTEGRATION_LOCK_2026-08-30.md`

## Global Constraints

- PR #25 remains unchanged and Draft.
- The new implementation pull request remains Draft.
- Exact first introduction: `Hi, I’m Patsy! Your AI Pet Pal!`
- Remember Me persists only session-restoration intent; password storage is forbidden.
- Account menu order is exactly My Account, Security & Privacy, Patsy Settings, Log Out.
- Primary navigation is exactly HOME, THyNK, CREATE, PATSY DMS, PROFILE.
- Preserve current charcoal, white, and restrained-rainbow visual wrappers.
- Do not claim live Supabase Auth, a production session adapter, or a production `.riv`.

---

### Task 1: Establish executable RED behavior contracts

**Files:**
- Modify: `app/build.gradle.kts`
- Create: `app/src/test/java/com/patsy/app/auth/ui/AuthUiIntegrationTest.kt`
- Create: `app/src/test/java/com/patsy/app/PatsyPrimaryNavigationTest.kt`
- Create: `.github/workflows/android-ci.yml`

**Interfaces:**
- Consumes: existing `AuthGateway`, `LoginRequest`, `PublicSession`, and session result types.
- Produces: failing compile-time expectations for `PatsyGreetingResolver`, `RememberMeCoordinator`, `PatsyAccountMenu`, `LoginSessionRetention`, and `PatsyPrimaryNavigation`.

- [ ] **Step 1: Add JVM test dependencies**

Add:

```kotlin
testImplementation(kotlin("test"))
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
```

- [ ] **Step 2: Write relationship greeting tests**

Use literal expected strings for:
- visit count zero returning the exact first introduction;
- morning returning greeting using the username;
- continuable-work context choosing a carry-on greeting;
- previous-greeting avoidance selecting another literal candidate;
- evening context never producing morning text.

- [ ] **Step 3: Write Remember Me tests**

Use a Boolean fake preference store and a counting fake `AuthGateway` to prove:
- disabled restoration returns `Anonymous` without calling the gateway;
- enabled restoration calls the gateway once;
- expired restoration clears opt-in;
- unavailable restoration preserves opt-in;
- sign-out clears opt-in even when the gateway reports unavailable;
- enabled/disabled selection maps to the correct `LoginSessionRetention`;
- `LoginRequest.toString()` remains redacted.

- [ ] **Step 4: Write account-menu and primary-navigation tests**

Assert the observable ordered actions/labels consumed by the UI:

```kotlin
listOf("my_account", "security_privacy", "patsy_settings", "log_out")
listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE")
```

- [ ] **Step 5: Add focused Android CI**

Run:

```bash
./gradlew testDebugUnitTest assembleDebug --stacktrace
```

Upload `app/build/outputs/apk/debug/app-debug.apk` only after success.

- [ ] **Step 6: Open the draft PR and verify RED**

Expected: CI fails because the new production contracts are absent. Record the exact failing symbols; do not add production code until this failure is observed.

### Task 2: Implement the minimal pure-Kotlin policy layer

**Files:**
- Create: `app/src/main/java/com/patsy/app/auth/ui/AuthUiIntegration.kt`
- Modify: `app/src/main/java/com/patsy/app/auth/AuthContracts.kt`
- Create: `app/src/main/java/com/patsy/app/PatsyPrimaryNavigation.kt`

**Interfaces:**
- Consumes: `AuthGateway`, `SessionState`, `SignOutResult`, and the existing `Screen` enum.
- Produces:
  - `PatsyGreetingResolver.resolve(GreetingContext): String`
  - `RememberMeCoordinator.retentionFor(Boolean): LoginSessionRetention`
  - `RememberMeCoordinator.restoreSession(AuthGateway): SessionState`
  - `RememberMeCoordinator.recordSuccessfulLogin(Boolean)`
  - `RememberMeCoordinator.signOut(AuthGateway): SignOutResult`
  - `PatsyAccountMenu.items`
  - `PatsyPrimaryNavigation.items`

- [ ] **Step 1: Implement greeting context and resolver**

Define:

```kotlin
enum class GreetingTimeOfDay { MORNING, DAY, EVENING }

data class GreetingContext(
    val username: String?,
    val completedVisits: Int,
    val timeOfDay: GreetingTimeOfDay,
    val hasContinuableWork: Boolean,
    val variantSeed: Int,
    val previousGreeting: String? = null,
)
```

Reject negative visit counts, normalize blank usernames to `there`, select context-appropriate candidates with `Math.floorMod`, and advance one candidate when the selected text equals `previousGreeting`.

- [ ] **Step 2: Implement session retention contract**

Add:

```kotlin
enum class LoginSessionRetention {
    CURRENT_PROCESS_ONLY,
    RESTORE_ON_NEXT_LAUNCH,
}
```

Add `sessionRetention` to `LoginRequest` with a secure default of `CURRENT_PROCESS_ONLY`. Keep its `toString()` password-redacted.

- [ ] **Step 3: Implement Remember Me coordination**

Define a Boolean-only `RememberMePreferenceStore`. `RememberMeCoordinator` must skip gateway restoration when disabled, clear the preference for anonymous/expired results, preserve it for temporary unavailable results, record the successful-login choice, and clear it in a `finally` block during sign-out.

- [ ] **Step 4: Implement the account-menu model**

Define four stable actions and immutable menu items with the exact titles/subtitles from the spec.

- [ ] **Step 5: Implement the locked navigation model**

Expose ordered `Screen`/label pairs for HOME, THyNK, CREATE, PATSY DMS, and PROFILE.

- [ ] **Step 6: Run targeted tests**

Run:

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.auth.ui.AuthUiIntegrationTest' --tests 'com.patsy.app.PatsyPrimaryNavigationTest' --stacktrace
```

Expected: PASS.

### Task 3: Add the Android Boolean preference adapter

**Files:**
- Create: `app/src/main/java/com/patsy/app/auth/ui/DataStoreRememberMePreferenceStore.kt`

**Interfaces:**
- Consumes: `RememberMePreferenceStore`.
- Produces: `DataStoreRememberMePreferenceStore(Context)`.

- [ ] **Step 1: Implement Preferences DataStore storage**

Use one Boolean key named `session_restore_enabled` in a store named `auth_ui_policy`. Read failures return false; writes persist only that Boolean.

- [ ] **Step 2: Confirm the adapter has no credential-shaped API**

The constructor takes only `Context`; public methods read or write only a Boolean. It accepts no username, email, password, token, or `SecretChars`.

- [ ] **Step 3: Run unit tests and APK assembly**

Run:

```bash
./gradlew testDebugUnitTest assembleDebug --stacktrace
```

Expected: PASS.

### Task 4: Wire the contracts into Compose

**Files:**
- Modify: `app/src/main/java/com/patsy/app/MainActivity.kt`

**Interfaces:**
- Consumes: all Task 2 and Task 3 outputs.
- Produces: opt-in login UI, gated startup restoration, relationship-aware visible greetings, exact account-menu rendering/actions, and locked bottom navigation.

- [ ] **Step 1: Gate startup restoration**

Create the DataStore preference adapter from `applicationContext`, create one `RememberMeCoordinator`, and replace direct startup `authGateway.restoreSession()` with coordinator restoration.

- [ ] **Step 2: Wire the Login Remember Me control**

Add an unchecked Compose checkbox labelled `Remember Me` with explanatory text `Session only — your password is never saved.` Pass the coordinator-selected retention in `LoginRequest`. Record the Boolean only after `LoginResult.Authenticated`.

- [ ] **Step 3: Wire sign-out clearing**

Route explicit sign-out through `RememberMeCoordinator.signOut(authGateway)`, then clear in-memory session/profile state.

- [ ] **Step 4: Render relationship-aware greetings**

Use the exact first introduction on Welcome. On Home, build `GreetingContext` from signed-in username, returning-visit state, local time period, continuation availability, and deterministic seed.

- [ ] **Step 5: Render the exact account menu**

Render `PatsyAccountMenu.items` in order. Route the first three items to truthful local panels and Log Out to the sign-out callback. Keep capability-gated owner controls inside My Account rather than adding primary menu items.

- [ ] **Step 6: Apply the locked primary navigation mapping**

Render `PatsyPrimaryNavigation.items` without changing the visual theme.

- [ ] **Step 7: Run full CI-equivalent verification**

Run:

```bash
./gradlew testDebugUnitTest assembleDebug --stacktrace
```

Expected: all unit tests pass and debug APK exists.

### Task 5: Review the draft implementation PR

**Files:**
- No production changes unless verification finds a concrete defect.

**Interfaces:**
- Consumes: CI and PR diff.
- Produces: evidence-backed Draft status report.

- [ ] **Step 1: Inspect the current-head workflow**

Confirm unit tests, APK assembly, and artifact upload each succeeded.

- [ ] **Step 2: Inspect scope**

Confirm only the spec, plan, focused workflow, auth UI files/tests, navigation model/test, build test dependencies, and necessary `MainActivity` wiring changed.

- [ ] **Step 3: Keep the PR Draft**

Document that provider-backed auth, device Compose tests, accessibility, token storage implementation, backend enforcement, and final visual assets remain unverified.

- [ ] **Step 4: Do not merge**

Return the draft PR URL and current CI evidence for explicit human review.
