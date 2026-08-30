# THyNK imported delta audit — 2026-08-30

This branch translates useful concepts from external prototype code into the existing native Patsy Studio architecture. It does not import the prototype package tree wholesale.

## Accepted concepts
- neutral canvas preset validation and aspect metadata
- explicit layer anchors and per-element scaling behaviour for reflow
- typed text properties and crop state on layers
- deterministic editor command dispatch, bring-forward/send-backward
- project artifact separation (editable project, export, retained media, personal template, community template)
- catalogue truth validation: stable IDs, forbidden visible platform names, licence/bundling honesty
- media safe-reference validation, hash/version/licence metadata
- timeline trim/position/volume validation and explicit filter/effect/transition attachments
- camera/export fail-closed request/result contracts
- asset resolution/licence evidence contracts and safe-reference rules
- offline-first draft/autosave concept, with lifecycle-safe cancellation and no private-ID logging

## Rejected or deferred
- Supabase service-role key in Android BuildConfig/local.properties: rejected; privileged secrets stay server-side
- incomplete Supabase mapper that discards saved canvas/layers/timeline: rejected
- generated font registry claiming unverified files/licences: rejected until files and evidence exist
- placeholder thumbnail renderer claiming real project rendering: deferred until the real layer renderer exists
- blank-charcoal image export claiming project export: deferred until project layers actually render
- CameraX implementation: deferred pending dependencies, manifest permission, PreviewView/lifecycle integration and device tests
- Room persistence: deferred pending Room/KSP dependencies and complete serialization/migrations
- fake tests that only print PASS or skip missing catalogue: rejected
- invented Basic/Creator/Studio tiers, export caps and upgrade gates: rejected
- visual lint forcing all user templates to Patsy charcoal/rainbow: rejected; app UI branding does not constrain every user template
- claiming filters/effects implemented without native renderer: rejected

## Verification plan
1. RED: JUnit tests demonstrate missing anchored fixed-size reflow, text/crop state, artifact separation, editor ordering commands and catalogue validator.
2. GREEN: implement only the accepted deltas in `com.patsy.app.studio.*`.
3. Run `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` in GitHub Actions.
4. Do not merge this branch to main without explicit user approval.
