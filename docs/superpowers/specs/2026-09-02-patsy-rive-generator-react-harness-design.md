# Patsy Rive Generator + React Harness Design — 2026-09-02

## Status

Approved architecture from the 2026-09-02 ChatGPT session. This design extends the existing Patsy animation stack without replacing or renaming the native V1 ABI.

## Goal

Add a development-side TypeScript/Rive generator and React interaction harness that exercises the real Patsy animation contract already present in THyNK-IN, while preserving the production Kotlin + Jetpack Compose + Rive architecture.

The work must not modify Camera, Supabase, global navigation, THyNK Panel routing, Media3, authentication, or the native app foundation.

## Existing Production Contract — Authoritative

Keep these files and interfaces authoritative:

- `app/src/main/java/com/patsy/app/patsy/PatsyAnimationContract.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigRuntimePort.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveHost.kt`
- `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapter.kt`
- `docs/PATSY_RIVE_RIG_CONTRACT_3.3.8.md`
- `docs/codex/PATSY_RIVE_MASTER_MOTION_SPEC_CANONICAL_2026-09-01.md`
- `docs/codex/PATSY_RIVE_ACCEPTANCE_GATE_CANONICAL_2026-09-01.md`

Locked production names remain:

- Artboard: `PatsyAssistant`
- State Machine: `PatsyAssistantMachine`
- View Model: `PatsyAssistantVM`
- Default instance: `Default`

Locked V1 motion values remain:

- `idle`
- `walk`
- `sit`
- `lie`
- `jump`
- `wave`
- `point`

Locked expression values remain:

- `neutral`
- `cheeky`
- `excited`
- `curious`
- `confused`
- `concerned`
- `proud`
- `sleepy`

Locked speech visemes remain:

- `rest`
- `a`
- `e`
- `i`
- `o`
- `u`
- `mbp`
- `fv`
- `l`
- `sz`

## Architecture

### 1. Generator Package

Add a small isolated TypeScript workspace under `tools/patsy-rive-generator/`.

It will use `@stevysmith/rive-generator` only for capabilities that package actually exposes: artboard creation, drawable/transform construction, timeline animations and `.riv` export.

The generator must not invent undocumented state-machine APIs.

The first generated development fixture may use a lightweight `Pet` artboard and `pet.riv` output to prove the pipeline. This fixture is explicitly non-production and must never replace the real Patsy asset.

A second generator definition will describe the production Patsy-compatible animation layer using the V1 names where the package supports them. The generator output is considered incomplete until the real Rive authoring step adds and validates the required state machine/View Model ABI.

### 2. React Development Harness

Add a React/TypeScript harness under `tools/patsy-rive-harness/` or as a sibling package in the same tools workspace.

The harness is development-only. It must never be embedded in the Android app through WebView or become production runtime architecture.

The harness will:

- load a generated `.riv` fixture;
- expose controls for talking, pointing, scale/tiny mode, gaze and position;
- map simple fixture controls onto the richer Patsy V1 concepts;
- accept a DOM element reference as a point target;
- use `getBoundingClientRect()` to calculate the target centre;
- convert the target centre into normalized viewport coordinates;
- set normalized point coordinates;
- lead with gaze/head direction before triggering the point action;
- provide a deterministic return-to-rest path after one-shot actions.

### 3. Android Contract Bridge

Do not redesign the Kotlin ABI.

Any shared TypeScript constants used by the harness will mirror the existing V1 values exactly and will be covered by tests that prevent drift.

The production app continues to own:

- real safe-area screen translation;
- Compose positioning and scaling;
- Android lifecycle;
- Rive runtime attachment;
- pending property dispatch;
- reduced-motion policy;
- final `.riv` validation.

The Rive asset continues to own character-local motion such as gait, breathing, blink, ears, tail, head response, mouth/viseme movement and body-action animation.

## Data Flow

1. A UI action in the React harness produces a normalized development command.
2. The harness adapter maps that command to Patsy V1 semantics.
3. For point-to-element, the adapter measures the DOM target and computes normalized X/Y.
4. Gaze values are updated first.
5. Point coordinates are set.
6. Point mode is selected and the one-shot sequence is incremented in the V1-compatible model.
7. The fixture animation plays.
8. The harness returns to idle without changing the production Kotlin implementation.

For Android production, the existing `PatsyRigCoordinator`/runtime path remains the source of actual runtime property dispatch.

## Generator Animation Scope

The generator should author every animation it can truthfully represent with the package API:

- idle breathing;
- blink;
- look left/right and up/down;
- head tilt;
- independent ear movement;
- tail movement;
- walk gait;
- sit;
- lie;
- jump;
- wave;
- point;
- talking/mouth cycle;
- scale/shrink development fixture behavior.

No generated animation may be represented as production-complete unless it passes the canonical Rive acceptance gate.

## State Machine Boundary

`@stevysmith/rive-generator` v0.1.1 does not currently provide a documented state-machine/View Model authoring API sufficient for the locked Patsy ABI.

Therefore:

- generator code may create timelines and export `.riv` files;
- the required production `PatsyAssistantMachine`, `PatsyAssistantVM`, `Default` instance and V1 properties must be added through a genuine Rive authoring route that supports those features;
- validation must fail closed if any required production ABI item is absent;
- no fake metadata, placeholder state machine or false Ready state is permitted.

## Error Handling

The development harness must show a clear non-production error state when the `.riv` fixture cannot load.

Production behavior remains unchanged: missing or invalid `.riv` must keep the transparent safe fallback active and must not claim a Ready production Rive state.

DOM target failures must be harmless. If a target element is missing, detached, zero-sized or outside a measurable viewport, the harness must skip the point action and retain the previous safe pose.

All normalized values must be clamped to the same ranges as `PatsyRigPose.normalised()`.

## Testing

Implementation follows RED/GREEN TDD.

Required tests:

### Generator

- produces deterministic output metadata;
- creates the expected artboard names for each fixture;
- includes expected named animations;
- rejects unsupported attempts to create a fake production state machine;
- keeps production and fixture names distinct.

### React Harness

- maps simple controls to V1-compatible values;
- normalizes DOM target coordinates correctly;
- clamps off-screen coordinates;
- points only after gaze/target data is prepared;
- increments one-shot action sequence rather than using it as an action ID;
- reduced-motion mode suppresses large movement/jump behavior;
- missing DOM targets fail safely.

### Contract Drift

- TypeScript mirror values exactly match the Kotlin V1 documented values;
- artboard/state-machine/View Model/instance names remain exact;
- motion/expression/viseme spelling and case remain exact.

### Native Regression

Run the existing Android unit tests plus debug and release builds. No unrelated native file should change unless a test-only contract fixture is required.

## Non-Goals

This slice does not:

- replace native Kotlin/Compose with React;
- embed a WebView;
- create a second backend;
- modify Camera or Media3;
- modify Supabase/auth/security;
- modify global navigation or THyNK Panel routing;
- rename THyNK-IN or the legacy repository/package namespace;
- claim the final production Patsy `.riv` is complete before validation;
- use GIFs, sprites, static pose swapping or fake bobbing.

## Acceptance Criteria

The slice is acceptable when:

1. an isolated TypeScript generator workspace exists;
2. the package genuinely generates a `.riv` development fixture using documented APIs;
3. a React harness can control the fixture and point toward a measured DOM element;
4. all control mapping is expressed in terms compatible with the existing V1 Patsy contract;
5. no production Kotlin ABI is redesigned;
6. unsupported state-machine authoring is explicitly blocked rather than faked;
7. generator/harness tests pass;
8. existing Android unit tests plus debug/release builds still pass;
9. Camera, Supabase, navigation, Media3 and THyNK-IN foundation files remain untouched by the implementation slice;
10. the final production Rive asset is still considered pending until a real authored `.riv` passes the canonical acceptance gate.
