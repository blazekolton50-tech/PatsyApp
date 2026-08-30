# Account Bootstrap, Protected Mode, and Locked Navigation Plan

**Goal:** Make authenticated shell entry and navigation depend on one trusted, fail-closed account bootstrap without changing the locked five-tab UI.

**Baseline:** `codex/instruction-2-secure-auth-owner` at `ae5b8aa`, reconstructed from verified Instruction 2 delivery. Do not merge.

## Task 1: Specify shell authority in tests

- Extend account bootstrap tests for malformed/inconsistent fields, unknown age, expired sessions, onboarding resume, and logout/account-switch clearing.
- Add typed navigation contract tests for exact route IDs/order/labels, Home-to-feed mapping, secondary Schedule/More, protected-mode gates, capability gates, and safe fallbacks.
- Add deep-link tests proving all routes pass through the same gate.
- Run focused tests and retain the expected RED evidence before implementation.

## Task 2: Harden the bootstrap contract

- Validate canonical identity, validity, profile/onboarding consistency, account status, settings state, restrictions, and server capabilities.
- Normalize unknown/unverified age to protected behavior and never upgrade authority from cached or local state.
- Add a transport adapter for the existing authenticated `account-bootstrap` function contract using only the publishable client key and bearer session token.
- Model unavailable, malformed, 401/403, and expired results explicitly and fail closed.

## Task 3: Centralize shell navigation authorization

- Introduce one typed route contract with stable IDs, exact five primary destinations, labels, availability rules, required capability, and fallback.
- Resolve bottom-nav taps, secondary navigation, and deep links through one authorization service.
- Keep protected routes visually present where required while routing denied access to a truthful safe state.

## Task 4: Wire authenticated shell lifecycle

- After login/session restore, fetch bootstrap before loading feature content.
- Resume explicit onboarding states rather than entering unrestricted Home.
- Keep local onboarding age choice presentation-only; never treat it as trusted authority.
- Clear bootstrap, capability decisions, and selected/deep-link state on logout/account switch.
- Preserve Owner verification as a separate server capability check.

## Task 5: Verify exact current head

- Run focused bootstrap/navigation/security tests.
- Run `./gradlew testDebugUnitTest assembleDebug --stacktrace` on the exact final head.
- Commit and push only the isolated feature branch; do not merge.
- Report backend dependencies and any live-provider verification that remains blocked.
