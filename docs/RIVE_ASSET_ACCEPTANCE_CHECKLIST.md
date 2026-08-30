# Patsy production Rive asset acceptance checklist

Status: **ASSET_REQUIRED / NOT_CONFIGURED** until every gate below passes.

The Android runtime, rig ABI, semantic companion controller and transparent fallback are code-side infrastructure. They do **not** prove that the final production Patsy `.riv` has been authored. Do not remove the fallback or claim production Rive completion until a genuine export is supplied and validated.

## 1. Exact file and runtime names

The genuine export must be placed at:

`app/src/main/res/raw/patsy_assistant.riv`

It must contain these exact names:

- Artboard: `PatsyAssistant`
- State machine: `PatsyAssistantMachine`
- View Model: `PatsyAssistantVM`
- View Model instance: `Default`

Case and spelling are part of the Android/Rive ABI.

## 2. Required View Model properties

All properties must resolve with the correct Rive type.

### Motion
- enum `motion/mode`
- number `motion/speed`
- number `motion/facing`
- number `motion/action_sequence`
- number `motion/point_x`
- number `motion/point_y`
- boolean `motion/reduced`

### Stage
- number `stage/x`
- number `stage/y`
- number `stage/scale`

### Head
- number `head/look_x`
- number `head/look_y`
- number `head/tilt`

### Ears
- number `ears/left_drive`
- number `ears/right_drive`
- boolean `ears/physics_enabled`

Left and right ears must be genuinely independent deformable controls; do not mirror one ear as a substitute for two controls.

### Tail
- number `tail/drive`
- number `tail/energy`

### Face
- enum `face/expression`
- number `face/expression_intensity`
- number `face/blink_sequence`

### Speech
- boolean `speech/talking`
- enum `speech/viseme`
- number `speech/viseme_intensity`
- number `speech/energy`

## 3. Exact enum values

### `motion/mode`
`idle`, `walk`, `sit`, `lie`, `jump`, `wave`, `point`

### `face/expression`
`neutral`, `cheeky`, `excited`, `curious`, `confused`, `concerned`, `proud`, `sleepy`

### `speech/viseme`
`rest`, `a`, `e`, `i`, `o`, `u`, `mbp`, `fv`, `l`, `sz`

## 4. Character identity gate

The final rig must preserve the approved realistic/high-grade Patsy identity rather than using a cartoon PawMoji character or a photograph.

Required visual traits include:
- small grey shaggy/curly dog appearance;
- light grey coat with white chest/muzzle treatment;
- darker grey ears and tail;
- long, lower-hanging straighter ear hair with independent secondary movement;
- dark/brown expressive eyes and black nose;
- slightly messy top hair;
- slim natural proportions;
- curved darker fluffy tail.

Real Patsy photos are reference-only. They must not be embedded in, exported from, or rendered by the app.

## 5. Continuous rig gate

The production asset must be one continuously animated rig. The following are release blockers:
- GIF animation;
- sprite sheets;
- PNG sequence animation;
- whole-character still-pose swapping;
- a boxed/circular/halo mascot treatment used as the animation system.

Durable motions must transition smoothly between `idle`, `walk`, `sit` and `lie`.

One-shot motions `jump`, `wave` and `point` must be retriggerable three consecutive times by incrementing `motion/action_sequence`; the third trigger must animate just as reliably as the first.

## 6. Interaction gate

Verify on the authored rig:
- natural idle breathing/secondary motion;
- blink retrigger through `face/blink_sequence`;
- horizontal and vertical eye/head look;
- head tilt;
- target-aware look and point;
- guide/notice behaviour;
- think and listen poses without whole-character swaps;
- talking with all required visemes;
- cheeky, curious, concerned, proud, excited/happy and sleepy expressions;
- celebration/wave;
- jump;
- sleep/rest using `lie`;
- helper shrink and full assistant expand through `stage/scale`;
- reposition and return through `stage/x` / `stage/y`;
- independent ear drives;
- tail energy/drive changes.

## 7. Reduced-motion gate

When `motion/reduced = true`:
- high-motion one-shots must not be required to communicate meaning;
- jump/point/wave motion is suppressed by the Android controller where appropriate;
- gaze, expression and target meaning remain available;
- durable `sit`/`lie` may remain;
- no fallback bob/rotation should run;
- the companion remains usable with TalkBack and large text.

## 8. Layout and accessibility gate

Test the production rig with the current transparent `PatsyCompanion` host in:
- compact portrait;
- large portrait;
- landscape;
- keyboard open/closed states;
- large font scaling;
- TalkBack enabled;
- reduced motion enabled.

Patsy must remain visually unboxed and must not obscure primary navigation, focused text fields, permission prompts or primary CTAs. Any later screen-space avoidance integration must preserve the same rig ABI rather than requiring a new `.riv` contract.

## 9. Android validation gate

With the genuine `.riv` present, run:

```bash
./gradlew testDebugUnitTest assembleDebug --stacktrace
```

Required result:
- unit tests PASS;
- debug APK assembles;
- `PatsyRiveHost` resolves the raw resource;
- artboard/state-machine/View Model/instance validation passes;
- every required property resolves;
- runtime status reaches Ready rather than fallback/InvalidAsset/Failed.

## 10. Physical-device smoke gate

Before changing fallback policy, verify on at least one physical Android device:
- app launches;
- Patsy loads as Rive rather than fallback;
- idle runs continuously;
- blink works three times;
- point works three times;
- jump works three times;
- wave/celebrate works three times;
- speech/visemes update;
- reduced-motion behavior is respected;
- rotation/relaunch does not break the runtime;
- no crash when the app backgrounds/foregrounds.

Record the device model, Android version, app commit SHA and Rive asset SHA/checksum with the test evidence.

## 11. Fallback removal gate

The generated transparent fallback stays in the app until **all** previous sections pass. Even after production launch, keeping the fallback as an error path is recommended so a corrupt/incompatible asset never leaves the companion area blank.

## Current truthful status

- Android Rive ABI: **IMPLEMENTED**
- Android Rive runtime adapter/validator: **IMPLEMENTED**
- Semantic Patsy companion controller: **IMPLEMENTED**
- Transparent unboxed companion host: **IMPLEMENTED ON THE RIVE FOUNDATION BRANCH, pending current-head CI verification**
- Production `patsy_assistant.riv`: **ASSET_REQUIRED / NOT CONFIGURED**
- Production rig/device acceptance: **BLOCKED BY REAL `.riv` EXPORT**
