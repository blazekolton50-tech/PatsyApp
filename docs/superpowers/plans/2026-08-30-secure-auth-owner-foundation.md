# Secure Auth and Owner Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the existing PR #21 security baseline by isolating debug preview auth, consolidating truthful Supabase auth/recovery behavior, and adding fail-closed server-backed Owner capability and account-bootstrap contracts.

**Architecture:** Continue from `codex/login-preview-bypass` at upstream SHA `1bada95f1221d27e9b8d2ceaa2514d984e523485`. Production auth remains installed once by `PatsyApplication`; preview selection is activity-scoped through a debug-only launch resolver. Owner decisions and bootstrap data come only from authenticated HTTPS backend responses and are denied on any missing, malformed, expired, or unavailable authority.

**Tech Stack:** Android, Kotlin, Jetpack Compose, JUnit/kotlin.test, Supabase Auth/Edge Functions over HTTPS, encrypted Android session storage.

**Spec:** User-provided `PATSY APP — CODEX EXECUTION INSTRUCTION 2` plus `MAIN_APP_SAVE_2026-08-30.md` and `docs/FINALIZED_BUILD_MASTER_2026-08-30.md`.

## Global Constraints

- Do not merge to `main` or redesign the locked UI.
- Supabase Auth UUID is canonical identity; no privileged/provider secrets ship in Android.
- Preview is debug-only, non-persistent, and never grants Owner/moderator/admin authority.
- Owner and bootstrap authority are server-issued, capability-specific, short-lived, and fail closed.
- `QUEUED` is not `SENT` or delivered; reset requests are non-enumerating.
- Preserve all existing Patsy/Rive controllers and transparent fallback; no `.riv` is invented.

---

### Task 1: Restore a Compilable Exact PR #21 Baseline

**Files:**
- Modify: `app/src/main/java/com/patsy/app/auth/AuthContracts.kt`
- Modify: `app/src/main/java/com/patsy/app/auth/SupabaseAuthGateway.kt`
- Test: `app/src/test/java/com/patsy/app/auth/SupabaseAuthGatewayTest.kt`

- [ ] Run the targeted auth/isolation tests and capture the current compiler failure.
- [ ] Add a suspend-safe secret-copy API test that preserves redaction and zeroization.
- [ ] Implement only the suspend-safe secret use needed by existing transports.
- [ ] Run auth tests and the isolation test again.

### Task 2: Isolate Debug Preview Authentication

**Files:**
- Modify: `app/src/debug/java/com/patsy/app/DebugPreviewActivity.kt`
- Modify: `app/src/debug/java/com/patsy/app/auth/DebugPreviewAccess.kt`
- Modify: `app/src/release/java/com/patsy/app/auth/DebugPreviewAccess.kt`
- Modify: `app/src/main/java/com/patsy/app/MainActivity.kt`
- Test: `app/src/test/java/com/patsy/app/auth/DebugPreviewIsolationRegressionTest.kt`
- Test: `app/src/test/java/com/patsy/app/auth/DebugPreviewAccessTest.kt`

- [ ] Verify the existing isolation test fails for the expected global-mutation reason.
- [ ] Pass preview intent/session policy to `MainActivity` and resolve an activity-scoped gateway.
- [ ] Keep release resolution production-only and preview Owner authority denied.
- [ ] Re-run preview/access tests and confirm the production binding remains unchanged.

### Task 3: Make Email and Recovery Results Truthful

**Files:**
- Modify: `app/src/main/java/com/patsy/app/auth/AuthContracts.kt`
- Modify: `app/src/main/java/com/patsy/app/auth/SupabaseRecoveryTransport.kt`
- Modify: `app/src/main/java/com/patsy/app/auth/SupabaseRegistrationTransport.kt`
- Test: `app/src/test/java/com/patsy/app/auth/SupabaseRecoveryGatewayTest.kt`
- Test: `app/src/test/java/com/patsy/app/auth/SupabaseRegistrationGatewayTest.kt`

- [ ] Add failing tests distinguishing `QUEUED`, `SENT`, `FAILED`, and `UNKNOWN`.
- [ ] Preserve non-enumerating reset responses for known and unknown identifiers.
- [ ] Map backend/provider evidence without treating HTTP acceptance as delivery.
- [ ] Run registration and recovery tests.

### Task 4: Add Server-Backed Capability Authorization

**Files:**
- Create: `app/src/main/java/com/patsy/app/security/ServerOwnerAuthorizationService.kt`
- Modify: `app/src/main/java/com/patsy/app/PatsyApplication.kt`
- Test: `app/src/test/java/com/patsy/app/security/ServerOwnerAuthorizationServiceTest.kt`

- [ ] Add failing tests for ordinary/under-16/preview/expired/unknown-capability/backend-failure denial and capability isolation.
- [ ] Implement authenticated HTTPS capability verification using current session authority only.
- [ ] Reject malformed, missing, expired, mismatched, or unavailable grants.
- [ ] Ensure logout removes usable privileged cached state and run Owner tests.

### Task 5: Add the Authenticated Account Bootstrap Contract

**Files:**
- Create: `app/src/main/java/com/patsy/app/account/AccountBootstrap.kt`
- Create: `app/src/main/java/com/patsy/app/account/AccountBootstrapService.kt`
- Test: `app/src/test/java/com/patsy/app/account/AccountBootstrapServiceTest.kt`

- [ ] Add failing tests for canonical UUID, profile/onboarding/settings state, protected unknown age, trusted capabilities, and malformed/unavailable fail-closed behavior.
- [ ] Implement the typed authenticated bootstrap boundary without client-invented defaults that upgrade authority.
- [ ] Clear bootstrap/capability state on logout and run bootstrap tests.

### Task 6: Final-Head Verification

**Files:**
- Update only truthful status documentation if implementation evidence changed.

- [ ] Run relevant targeted security/auth tests.
- [ ] Run `./gradlew testDebugUnitTest assembleDebug --stacktrace` on the exact final head.
- [ ] Record branch, SHA, commands, pass/fail, changed files, backend gaps, Owner gaps, and provider/manual checks.
- [ ] Do not merge; recommend exactly one next task.

