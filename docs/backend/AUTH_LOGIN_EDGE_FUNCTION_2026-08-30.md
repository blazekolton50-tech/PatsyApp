# Auth login Edge Function — 2026-08-30

Status: DEPLOYED BACKEND FOUNDATION / successful credential login not yet verified.

Supabase function: `auth-login`
- Status returned by Supabase: ACTIVE
- Version: 1
- `verify_jwt=false` intentionally, because this endpoint exists before an authenticated user JWT exists.
- Custom authentication: accepts only POST JSON with a validated username/email identifier and password; password verification is delegated to Supabase Auth.
- Username resolution happens server-side with service-role access and does not expose a public username→email lookup.
- Service-role and legacy anon server environment variables stay inside the Edge Function.
- Generic `INVALID_CREDENTIALS` is returned for unknown user / wrong password.
- Rate-limit and provider/server failures are distinct machine-readable states.
- Successful response contains the normal user access/refresh session plus safe public session metadata; OWNER/admin role is not returned or inferred here.

Verification truth:
- Deployment itself is verified ACTIVE by the connected Supabase tool.
- The connected live project currently reports zero rows in `public.profiles`, so there is no verified real Patsy account available for a successful login smoke test yet.
- The local execution environment could not resolve the Supabase hostname, so a curl smoke attempt did not reach the endpoint. This is an environment/network limitation, not a claimed endpoint pass.
- Do not mark production login end-to-end VERIFIED until the Android adapter is wired and a real verified account successfully signs in.
