# Real Supabase login — TDD RED checkpoint

Required next code:
- production `SupabaseAuthGateway` behind the existing `AuthGateway` interface;
- HTTPS login through the deployed `auth-login` Edge Function;
- encrypted refresh/access token persistence on Android;
- refresh-based session restore;
- sign-out clears local session and attempts server revocation;
- production app startup binds the real gateway;
- debug preview remains source-set isolated and never grants OWNER;
- registration/email-confirmation remain honest `NOT_CONFIGURED` until implemented separately.

No production success claim until CI is green and a real account successfully authenticates end-to-end.
