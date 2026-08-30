# Supabase registration backend — 2026-08-30

Status: DEPLOYED BACKEND + ANDROID SOURCE IMPLEMENTED; current-head Android CI and real-email end-to-end verification still required before calling signup production-verified.

## Live backend changes

Applied migrations:
- `add_secure_registration_attempts`
- `add_registration_attempt_service_rpcs`
- `add_registration_finalize_rpc`

Added `private.registration_attempts`:
- 20-minute expiry
- no password storage
- RLS enabled
- anon/authenticated have no CRUD privileges
- expired/consumed attempts are removed by the existing hourly cleanup function

Service-role-only RPCs in `public`:
- `internal_issue_registration_attempt`
- `internal_claim_registration_attempt`
- `internal_finalize_registration`

Direct privilege verification after migration:
- `anon` EXECUTE: false on all three
- `authenticated` EXECUTE: false on all three
- `service_role` EXECUTE: true on all three

The RPCs are intentionally `SECURITY DEFINER` but inaccessible to public client roles. The public schema location exists only so the server-side Edge Functions can call them through the Data API; no Android client is authorized to execute them.

## Edge Functions

Deployed ACTIVE with `verify_jwt=false` because these are pre-authentication endpoints:
- `auth-register-start`
- `auth-register-complete`

`auth-register-start` validates username/email/experience mode and issues a short-lived one-time registration attempt only if the username is not already used/reserved.

`auth-register-complete`:
- validates password server-side;
- atomically claims the registration attempt;
- delegates password hashing/account creation to Supabase Auth `/auth/v1/signup`;
- finalizes the profile username with server authority;
- maps Under-16 self-selection to `under_16` but maps 16+/Protected self-selection to the safer `unknown` tier until a real age-verification path exists;
- rolls back the newly-created auth user on a profile-finalization failure where possible;
- reports confirmation email as QUEUED only after Supabase Auth accepted the signup request.

No service-role key is shipped in the APK.

## Android source

Added `SupabaseHttpRegistrationTransport` and connected it through `SupabaseAuthGateway` / `PatsyApplication`, preserving the existing locked onboarding UI sequence:

`username + email -> password -> email confirmation notice -> login`

## Security/advisor result

Supabase security advisor now reports one INFO item: RLS is enabled on `private.registration_attempts` with no policies. This is deliberate deny-by-default behavior for the private server-only table; direct privileges for anon/authenticated are also revoked. Remediation reference: https://supabase.com/docs/guides/database/database-linter?lint=0008_rls_enabled_no_policy

The performance advisor reports pre-existing informational unindexed-FK/unused-index findings plus the newly-created registration expiry index as currently unused; this is expected before registration traffic exists. Do not delete indexes merely because a new/empty project has not used them yet.

## Still not claimed complete

- A real signup with a real email has not yet been run.
- Confirmation-email delivery/inbox arrival has not been verified.
- Android deep-link handling for email confirmation is not yet implemented.
- Password recovery is still a separate incomplete flow.
- OWNER authorization is not created by registration and remains fail-closed/server-authorized.
