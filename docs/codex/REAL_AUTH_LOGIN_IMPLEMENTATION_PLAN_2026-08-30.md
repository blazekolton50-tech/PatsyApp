# Real Supabase login integration — 2026-08-30

Status: implementation plan for the next TDD slice.

Goal: replace production `NOT_CONFIGURED` login/session restore with real Supabase-backed username-or-email login while keeping the debug preview isolated and keeping OWNER authorization separate/fail-closed.

Constraints:
- Do not redesign locked login/onboarding screens.
- Client uses only the project URL and client-safe publishable key.
- Service-role credentials remain inside Supabase Edge Functions.
- Username login must not expose a username-to-email lookup endpoint.
- Invalid credentials use a generic error.
- No production Owner capability is inferred from username/profile metadata.
- Debug bypass remains debug-only.

Implementation sequence:
1. Deploy an unauthenticated `auth-login` Edge Function with `verify_jwt=false` because login occurs before a user JWT exists. The function validates method/input, resolves a username privately with service-role access, gets the linked auth email privately, then delegates password verification to Supabase Auth. It returns a normal Supabase user session only on successful password verification.
2. Add an Android `SupabaseAuthGateway` transport behind the existing `AuthGateway` interface for login, secure token persistence, refresh-based session restore, and local+server sign-out. Registration/confirmation stay explicitly unavailable until their own tested slice.
3. Configure the production gateway at app startup. Keep `DebugPreviewAuthGateway` only in `src/debug`.
4. Add unit tests around response mapping, generic invalid-credential handling, secure session-store behavior, refresh/session restore and debug/release separation.
5. Run Android CI (`testDebugUnitTest assembleDebug`) before any completion claim.
6. Backend smoke: verify the Edge Function rejects malformed and invalid credentials generically. A successful login cannot be claimed until a real verified test account exists and authenticates end-to-end.

Source compatibility note: the current app is Kotlin 2.0.21. This slice deliberately avoids adding a newer Supabase Kotlin dependency whose recent releases require newer Kotlin; it uses a small HTTPS transport against documented Supabase Auth/Functions endpoints behind the existing provider-neutral interface.
