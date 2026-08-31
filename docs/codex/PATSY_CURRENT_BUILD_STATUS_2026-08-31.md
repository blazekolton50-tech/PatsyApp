# Patsy Current Build Status — 2026-08-31

## Production source of truth
Native Android/Kotlin repository: `blazekolton50-tech/PatsyApp`.

## FINAL visual baseline
PR #33 (`feat/final-login-password-home-design-lock`) remains the visual source of truth for:
- Login
- Set Password
- Home

The active consolidation line is Draft PR #35:
`codex/consolidate-final3-auth-rive-2026-08-31`

Verified consolidation head:
`f967b3b095ea6bb08ba46f6a6595624487640556`

Verified GitHub Actions run `33347904868` / Patsy Consolidation CI #22:
- Unit tests: PASS
- Debug APK build: PASS
- Release variant build: PASS
- Debug APK upload: PASS
- Workflow conclusion: SUCCESS

Fresh consolidation artifact:
- Name: `patsy-consolidation-debug-apk`
- GitHub artifact id: `9742691232`
- Artifact ZIP SHA-256: `02517e427323208c653445cb728b78887c1c98fe4578c8c0d22a60645584f373`
- Extracted `app-debug.apk` SHA-256: `b272f59f4d9c088b5b6c739f85433261416236e31a56ce6f59f6d66320b14518`
- Extracted APK size: 64,571,472 bytes

## Auth — integrated on PR #35
The current consolidation branch now includes:
- FINAL Login / Set Password auth routes
- debug/release test-access isolation
- login session-retention contracts
- password redaction and secret zeroization across suspend transports
- encrypted Android session-token storage
- production `SupabaseAuthGateway`
- Supabase HTTPS login / refresh / logout transport
- registration-start transport
- registration-complete transport
- password-reset-request transport
- `PatsyApplication` startup binding
- client-safe Supabase URL and publishable key only

No service-role secret is shipped in Android.

Supabase currently has active Edge Functions for:
- `auth-login`
- `auth-register-start`
- `auth-register-complete`
- `auth-reset-request`
- `create-dm-thread`

Email-token consumption / final confirmation completion remains a separate unconfigured slice and must not be faked.

## Patsy / Rive
The repository has:
- `PatsyRigContractV1`
- `PatsyRigRuntimePort`
- `PatsyRiveRuntimeAdapter`
- `PatsyRiveHost`
- ABI validation and truthful generated fallback

The authored production `patsy_assistant.riv` asset is still not present. Full-body motion, real lip-sync, walking/target-aware pointing and final on-device animation remain unverified until that asset is supplied.

## THyNK
PR #23 contains useful editor state, tool catalog, Media3 video player and THyNK shell work. Its latest CI is still failing on the single-clip state contract. Keep this staged until repaired; do not replace the locked FINAL screens or destabilize the green APK to import broken Studio code.

## Backend
Supabase project is ACTIVE_HEALTHY. Current tables include profiles, user settings, Patsy memories, projects, media, posts, DMs, notifications, safety/consent/capability tables and THyNK Studio tables. `studio_presets` currently contains 48 rows.

Security blocker before production merge is tracked in issue #36:
- private tables currently reported with RLS disabled require deliberate policy/service-role review
- `private.registration_attempts` has RLS enabled but no policy

Do not auto-apply the suggested RLS SQL without deciding required access policies first.

## Owner tools / protected shell
Server-backed owner authorization and protected account bootstrap code exist in earlier Draft work but the matching `owner-authorize` / account-bootstrap backend endpoints are not in the currently deployed Edge Function set. Preserve fail-closed behavior until those server endpoints are deliberately deployed and verified.

## Replit
`Patsy Android Companion` remains reference-only Expo/React Native prototype work. It has no production Login/Set Password, no THyNK, no Patsy DMs, no Supabase product integration and no Rive. Do not import its architecture into the native app.

## Device gate
The next truth gate for PR #35 is physical-device validation:
1. install the consolidation debug APK
2. test debug login and custom password flow
3. test close/reopen and logout/relogin
4. capture Login / Set Password / Home screenshots
5. compare against the three FINAL references
6. fix visual differences before any merge recommendation

Do not merge PR #35 to main until the owner approves the device screenshots and the Supabase security gate is resolved.
