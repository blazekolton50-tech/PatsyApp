# Patsy Shrink-to-Travel Lock — 2026-09-02

This behavior is owner-locked and must not be removed or replaced by a static/sprite workaround.

## Required whole-app companion behavior

Patsy is one transparent app-wide companion. When she needs to guide a user to a control or location on the current page she must:

1. start at her normal resting size and position;
2. shrink using the same Patsy rig/character instance;
3. travel visibly across the page while small;
4. arrive beside, not directly over, the target;
5. look toward the target and point at it;
6. remain able to talk/drive visemes while guiding;
7. when guidance ends or navigation changes, travel back while small;
8. return to her resting position;
9. expand back to normal size and resume idle behavior.

## Current V1 implementation

- Normal resting stage: `stage/x = 0.50`, `stage/y = 0.75`, `stage/scale = 1.00`.
- Small travel scale: `stage/scale = 0.45`, matching the current V1 minimum scale.
- Travel uses multiple interpolated `stage/x` / `stage/y` updates while `motion/mode = walk`.
- Arrival uses `motion/mode = point` with exact `motion/point_x` / `motion/point_y` target coordinates plus head-look controls.
- Return uses the same small walk sequence, then expands to `stage/scale = 1.00` and idles.
- Reduced-motion mode avoids the large animated travel sequence and uses the existing reduced-motion contract.

## Identity and architecture lock

- Do not create a separate chibi/mini Patsy.
- Do not swap to a second sprite/artboard to fake shrinking or movement.
- Do not introduce React, GSAP, WebView or Unity as the production movement runtime.
- Production remains Kotlin + Jetpack Compose + Rive.
- Until a genuine validated `app/src/main/res/raw/patsy_assistant.riv` exists, the transparent safe fallback may render the same approved Patsy image while using the same app-owned travel state.

## Native integration

The behavior is implemented through:

- `PatsyCompanionController.kt` — shrink/travel/guide/return/expand sequence.
- `PatsyCompanionOverlay.kt` — one full-screen transparent companion surface.
- `FinalMainActivity.kt` — authenticated-shell hosting and command dispatch.
- `PatsyCompanionTravelTest.kt` — sequence regression coverage.
- `PatsyCompanionOverlayContractTest.kt` — full-screen shell wiring regression coverage.

The Home `Ask Patsy anything...` control currently provides a real on-device guide trigger so the travel path can be exercised before the final production `.riv` is authored. App navigation issues `ReturnHome`, so Patsy returns and expands instead of remaining stranded in guide mode.
