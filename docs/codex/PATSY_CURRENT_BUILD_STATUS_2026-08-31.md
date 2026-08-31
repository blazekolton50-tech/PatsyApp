# Patsy Current Build Status — 2026-08-31

## Production source of truth
Native Android/Kotlin repository: `blazekolton50-tech/PatsyApp`.

## FINAL visual baseline
PR #33 (`feat/final-login-password-home-design-lock`) is the current visual baseline for:
- Login
- Set Password
- Home

Head SHA: `e4565570326ca98ba040934364f204f5aab4e133`

Verified GitHub Actions run `33346848093` / Final Screen CI #54:
- Unit tests: PASS
- Debug APK build: PASS
- Release variant build: PASS
- Debug APK upload: PASS
- Workflow conclusion: SUCCESS

Artifact:
- Name: `patsy-final-screens-debug-apk`
- GitHub artifact id: `9742362019`
- Artifact ZIP SHA-256: `247624846137aa8d3ca5dcad5aeee659391c07a33d1cbc0eecb00bb9a9a84f92`
- Extracted `app-debug.apk` SHA-256: `1b0527bd1616efc1e62ba6e4c411e0ceafceda1e56ac178031c3c506c33ab5ea`
- Extracted APK size: 64,538,688 bytes

## Auth
The FINAL branch has debug/release test-access isolation and session-retention contracts. Production `PatsyServiceBindings.authGateway` still defaults fail-closed/unconfigured. A real Supabase auth gateway exists in older Draft work and must be reconciled to the current AuthContracts rather than copied blindly.

Supabase currently has active Edge Functions for:
- `auth-login`
- `auth-register-start`
- `auth-register-complete`
- `auth-reset-request`
- `create-dm-thread`

## Patsy / Rive
The repository has:
- `PatsyRigContractV1`
- `PatsyRigRuntimePort`
- `PatsyRiveRuntimeAdapter`
- `PatsyRiveHost`
- ABI validation and truthful generated fallback

The authored production `patsy_assistant.riv` asset is still not present. Full-body motion, real lip-sync, walking/target-aware pointing and final on-device animation remain unverified until that asset is supplied.

## THyNK
PR #23 contains useful editor state, tool catalog, Media3 video player and THyNK shell work. Its latest CI is still failing on the single-clip state contract; do not treat Studio foundation as complete.

## Backend
Supabase project is ACTIVE_HEALTHY. Current tables include profiles, user settings, Patsy memories, projects, media, posts, DMs, notifications, safety/consent/capability tables and THyNK Studio tables. `studio_presets` currently contains 48 rows.

Security blocker before production merge:
- private tables currently reported with RLS disabled require deliberate policy/service-role review
- `private.registration_attempts` has RLS enabled but no policy

Do not auto-apply the suggested RLS SQL without deciding required access policies first.

## Replit
`Patsy Android Companion` remains reference-only Expo/React Native prototype work. It has no production Login/Set Password, no THyNK, no Patsy DMs, no Supabase product integration and no Rive. Do not import its architecture into the native app.

## Integration line
Stacked Draft PR #35 (`codex/consolidate-final3-auth-rive-2026-08-31`) starts from PR #33 and is the safe place to reconcile auth, protected shell, Rive and THyNK foundations without touching main.
