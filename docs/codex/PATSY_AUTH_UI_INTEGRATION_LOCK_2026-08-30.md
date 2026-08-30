# Patsy Auth UI Integration Lock

Date: 30 August 2026  
Status: confirmed implementation scope  
Reference review: draft PR #25

## Branch and review boundary

- Implement on a new branch from `main`.
- Leave PR #25 unchanged and in Draft.
- Open the implementation pull request as Draft.
- Do not merge either pull request without explicit approval.
- Keep this slice limited to auth UI behavior, the account menu, and the locked primary-navigation labels needed to reach Profile.

## Relationship-aware greetings

- The first introduction is exactly: `Hi, I’m Patsy! Your AI Pet Pal!`
- A signed-in returning user receives a returning greeting, never the first-introduction phrase.
- Returning selection consumes explicit relationship context: username, completed-visit count, local time period, whether continuable work exists, a deterministic variant seed, and the previously shown greeting.
- Morning-specific text must never be selected outside the morning period.
- If the selected candidate equals the previous greeting, choose a different available candidate.
- This slice does not claim durable cross-device relationship memory. The future backend memory service remains the authority for durable visit/project history.

## Login Remember Me

- Login Remember Me means session restoration only.
- It never permits storage of the password or any reusable password representation.
- Unchecked login requests current-process-only session retention.
- Checked login requests a provider-owned session that may be restored on a later app launch.
- The app may store only a Boolean opt-in preference locally; refresh/access tokens remain inside the secure AuthGateway adapter.
- Startup must not call `restoreSession()` when opt-in is false.
- Expired, revoked, signed-out, or anonymous restoration clears the opt-in.
- Temporary service unavailability keeps opt-in so restoration can be retried.
- Explicit sign-out clears opt-in even when the remote sign-out operation is unavailable.

## Account menu

Render exactly these four primary account items, in this order:

1. My Account — Username, password, info
2. Security & Privacy — PIN, 2FA, devices, privacy
3. Patsy Settings — Patsy's personality & prefs
4. Log Out — Sign out of your account

Owner Profile and Owner Tools remain separately server-authorized. They may appear inside My Account only after current capability verification; they are not extra primary account-menu entries.

## Locked primary navigation

The bottom navigation labels and order are:

`HOME • THyNK • CREATE • PATSY DMS • PROFILE`

This task changes labels/order and route mapping only. It must not redesign the locked visual system.

## Truthfulness limits

- No live Supabase Auth or other provider is claimed.
- No production session adapter is claimed.
- No finished production `.riv` is claimed.
- Unit tests plus APK assembly do not replace device, accessibility, backend, or security testing.
- The implementation pull request remains Draft after CI becomes green.
