# PATSY APP — VISUAL PREVIEW C — LOCKED

Repository: `blazekolton50-tech/PatsyApp`
Baseline: Phone Preview B / PR #32
Status: Draft preview work only. **DO NOT MERGE PR #32.**

## Locked Preview C shell

Primary bottom navigation is exactly:

**HOME | THyNK | BIG PLUS CAMERA | PATSY DMs | PROFILE**

- The middle destination is Camera, not a normal Create tab.
- Render the middle control as a 56dp white circle with a black `+` at 28sp.
- Existing Create/New functionality remains a secondary THyNK Studio flow.
- Schedule remains secondary.

## Locked top bar

- Use the exact approved Patsy logo asset, centred.
- Preview C logo image height: 32dp (half-size treatment).
- Do not substitute plain text when the approved logo asset is available.
- Top-right three-dot menu order is exactly: Account, About, Profile, Settings, Remember Me.

## Locked Home detail

Directly under the Patsy greeting, provide a full-width action:

**Ask your Pet Pal Patsy a question**

This routes through the existing Patsy AI/chat boundary and must not claim an unconfigured provider is live.

## Patsy companion boundary

- One Patsy companion per page.
- Preserve the existing Rive/controller architecture.
- Intended authored states include idle, blink, eye tracking, head tilt, target-aware look/point, guide, think/listen/speak, reactions, celebrate, shrink/expand, reposition/return and reduced-motion handling.
- Do not fake continuous Patsy animation with GIFs, sprites or static pose swaps.
- If the final production `.riv` asset is unavailable, use the existing truthful fallback and keep the Rive boundary intact.

## Security and truthfulness boundaries

- Keep `com.patsy.app`, the real Gradle/Compose/Rive project, auth contracts, account bootstrap, age/protected-mode gates and server-backed OWNER authorization.
- Keep the DEBUG-only preview launcher isolated from release.
- Debug preview must not grant OWNER/admin authority.
- Camera capture, live Social, production DMs, production AI generation and other unfinished providers must remain visibly unconfigured/preview-only until implemented and verified.

## Verification

Required before calling Preview C buildable:

```bash
./gradlew testDebugUnitTest assembleDebug --stacktrace
```

A genuine `app-debug.apk` must be produced by Gradle. Do not use dummy APK or fake test/build scripts.
