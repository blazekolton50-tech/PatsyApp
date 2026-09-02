# Patsy Composite Animation Handoff — 2026-09-02

## Status

This document records the latest Patsy animation/control work and reconciles it with the current **THyNK-IN** Android production architecture.

- Product/runtime remains native **Kotlin + Jetpack Compose + Rive**.
- The Unity/C# subsystem prototype discussed on 2026-09-02 is **behavioral reference only** and must not be introduced as a second production runtime.
- GitHub issue #45 is the active implementation/delegation tracker.
- This branch is intentionally stacked from the current PR #43 head so device-launcher/THyNK Panel work is preserved.

## Existing native Rive foundation — preserve

Current native source of truth includes:

- `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigRuntimePort.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveHost.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapter.kt`

The V1 ABI already exposes:

- motion: idle / walk / sit / lie / jump / wave / point
- stage x/y/scale
- head look x/y + tilt
- independent left/right ear drive + ear physics toggle
- tail drive + energy
- expression + expression intensity
- blink sequence
- speech talking flag
- viseme + viseme intensity + speech energy
- reduced-motion normalization

Do not replace these property names, enums, buffering rules, safe fallback, or validation contract without an explicit ABI migration.

## Composite semantic model to preserve

The current control design uses five simultaneous layers:

### Body — persistent

- Idle
- Walking
- Sitting
- Lying

Body remains active until explicitly changed.

### Action — transient

Requested semantic actions:

- None
- Wave
- Point
- Jump
- Peek
- CoverEyes
- Celebrate

Current V1 motion ABI directly supports Wave, Point and Jump. Celebrate can be composed from supported motion/expression/tail behavior. Peek and CoverEyes are **not V1 motion values** and must remain unsupported/diagnosed until a genuine ABI + authored-Rive extension exists.

A repeated identical composite update must not restart an already-running one-shot action every frame.

### Emotion — layered, intensity 0..1

Requested semantic emotions:

- Neutral
- Happy
- Curious
- Focused
- Concerned
- Shy

Current V1 expression ABI directly includes Neutral, Curious and Concerned, plus Cheeky, Excited, Confused, Proud and Sleepy. Happy may be represented only through an explicitly documented semantic mapping to an existing compatible V1 expression. Focused and Shy must not be silently claimed as authored Rive expressions until the compatibility mapping or ABI extension is explicitly approved.

### Attention — layered

Requested semantic targets:

- Neutral
- User
- Camera
- UIControl
- WorldTarget
- AIExplicit

Attention resolves into normalized `head/look_x`, `head/look_y`, optional head tilt, and—where genuinely supported by app context—target-aware pointing. Neutral must smoothly return gaze/head controls to rest rather than freezing on the last target.

### Voice — transient

Composite state may include:

- speech text
- optional audio clip/provider identifier

Speech must not restart indefinitely when the same composite payload is repeatedly delivered. `speech/talking`, viseme controls and speech energy remain the native Rive output boundary. Empty speech in an ordinary composite request means “no new speech”; explicit stop/reset cancels active speech.

## Full neutral reset

A full neutral reset must truthfully return the logical state to:

- Body: Idle
- Action: None / cancelled
- Emotion: Neutral
- Attention: Neutral / cleared
- Voice: stopped

Visual return may remain smoothly animated by the Rive state machine. Do not force fake frame swaps.

## Example composite semantics

### Greeting

- body: Sitting
- action: Wave for 1.8s
- emotion: Happy semantic request, intensity 0.75
- attention: User
- speech: `Heeeyy, you're back!`

Expected behavior: wave completes, while sitting/emotion/attention remain active until changed.

### Walking focus

- body: Walking
- action: None
- emotion: Focused semantic request, intensity 0.9
- attention: WorldTarget
- speech: `Moving to target location now.`

Focused must use an explicitly approved compatibility mapping or report that the current V1 authored expression is unavailable.

### Lying concern

- body: Lying
- action: CoverEyes requested for 3s
- emotion: Concerned 0.6
- attention: Camera
- speech: `I'd rather not look right now...`

`CoverEyes` must remain truthfully unsupported unless the actual Rive ABI/asset is extended. Do not substitute a fake static pose and claim success.

## Reconciliation requirement

The older branch `codex/patsy-rive-ui-foundation` / PR #15 contains fuller `PatsyCompanionController` and regression coverage that are not all present on the latest working line. Reconcile only compatible native pieces forward; do not blindly copy old UI or supersede newer THyNK-IN work.

## Required implementation order

1. Preserve current PR #43 work.
2. RED test proving the missing native companion/composite behavior.
3. Reconcile compatible `PatsyCompanionController` behavior from PR #15.
4. Add native composite command parsing/mapping above the existing rig boundary.
5. Add duplicate-action/speech suppression and full reset behavior.
6. Restore/strengthen rig coordinator + Rive adapter tests.
7. Run unit tests, debug build, release build and GitHub Actions on the exact final head.

## Production visual blocker

Do not fabricate or rename a placeholder into:

`app/src/main/res/raw/patsy_assistant.riv`

A genuine authored production `.riv` matching `PatsyRigContractV1` remains required before production animation can replace the transparent safe fallback.
