# Patsy App Android Build Status

Status date: 30 August 2026  
Working branch: `codex/patsy-rive-ui-foundation`  
Pull request: `#15` — Patsy Rive/UI foundation  
Project: `PatsyApp`

## 30 August consolidation update

- The ChatGPT/Library/Google AI Studio/Codex/archive consolidation has been added to the branch through `CODEX_START_HERE.md`, `docs/codex/PATSY_MEGA_MASTER_HANDOFF_2026-08-30.md`, and `docs/codex/PATSY_ARCHIVE_COMPARE_2026-08-30.md`.
- The Mega Master handoff records the latest source-of-truth order so older Android/Creation Assistant ZIPs cannot silently override newer locked requirements or current architecture.
- The archive comparison confirms that current GitHub architecture is newer than the old direct `BackendClient`, static `PatsyActor`, older `MainActivity`, and historical backend prototype. Those files must not be restored wholesale.
- Current branch includes locked visual wrappers (`PatsyHeader`, `PatsyPrimaryButton`), Rive host/runtime boundaries, Patsy rig contracts/coordinator, auth/service boundaries, fail-closed owner authorization, service coordinators, and regression tests.
- PR #15 remains a draft while foundation work is being consolidated and verified.
- Android CI is configured to use JDK 21, run unit tests, assemble the debug APK, and upload the debug artifact. Always check the current PR head workflow before claiming the newest commit has passed.
- No production `.riv`, live AI provider, live social publisher, production email provider, or production auth/backend authority may be claimed unless it is actually configured and verified.

## Verified passes from the 29 August lineage

- Final clean `assembleDebug`: **passes** with zero Kotlin, Compose, Gradle, manifest or resource compilation errors after the Rive runtime, secure auth/Owner UI, service-state additions, final `PatsyMotion` -> Rive control wiring, fail-closed feature bindings, and the Patsy AI/search coordinator. All 36 tasks executed and the verified build completed on 29 August 2026 in 4m 03s. The debug APK was produced at `app/build/outputs/apk/debug/app-debug.apk` in that verified environment.
- A final combined `testDebugUnitTest lintDebug` invocation was attempted again after the successful build, but the sandbox denied Gradle access to the installed Android SDK `package.xml`/Build Tools metadata before either task could start. This is recorded as **not run**, not as a source failure or a pass. The earlier pre-Rive lineage lint run passed.
- The provider-neutral DM, scheduling and Creation Studio coordinators compile independently with Kotlin 2.0.21 targeting JVM 17.
- Java source/target compatibility and Kotlin `jvmTarget` are set to JVM 17.
- Gradle wrapper 8.10.2 and required wrapper scripts/config are included.
- Debug APK installed and launched on the connected OUKITEL C68 as `com.patsy.app.debug` during the earlier device verification, alongside the differently signed `com.patsy.app` without overwriting its data.
- Welcome screen was visually inspected on device in the earlier lineage. Official white Patsy logo #4 was used.
- The former square storyboard used as the live Patsy image was replaced with a generated transparent full-body Patsy cutout. Real photos remain reference-only assets and are not rendered by the app.
- Compose contains continuous breathing, bobbing, looking/turning, jumping and contextual movement behaviour plus explicit animation states.
- `PatsyMotion` maps states into the Rive ABI for motion, expression, gaze/head tilt, independent left/right ear drive, tail energy, pointing coordinates, jump/wave/point triggers, and deterministic talking-viseme amplitude. The generated transparent fallback remains active until a validated production `.riv` is bundled.
- The official Rive Android Compose runtime is integrated behind a strict validating host. It resolves only `res/raw/patsy_assistant.riv`, validates the stable artboard/state-machine/View Model/property ABI, and keeps the generated Patsy fallback visible whenever the asset is missing, loading, invalid or incompatible.
- Signup/login/reset/session/sign-out use the authentication boundary rather than local pretend success. Secret character buffers are erased after transport and confirmation-email success is displayed only for provider-confirmed queued/sent states.
- The former any-credentials login and local Owner boolean were removed. Owner Profile and Owner Tools surfaces depend on current server-verified per-capability grants and fail closed on unavailable/denied/expired decisions.
- DMs, scheduling, Creation Studio and AI/search have authenticated, age-policy-aware coordinator states for loading, success, unavailable, denied and failure. Provider composition defaults to explicit `NOT_CONFIGURED` until production adapters replace it.

## Current implementation priorities

1. Keep the current branch architecture; do not restore superseded historical app/backend files wholesale.
2. Finish any remaining locked visual-wrapper migration and realistic unboxed `PatsyCompanion` extraction without altering PawMoji assets.
3. Preserve the Rive ABI/fallback until a genuine production `.riv` exists and passes validation.
4. Continue service integration behind secure provider-neutral boundaries: auth/backend authority, AI/search, image/video generation, email, DMs and publishing.
5. Continue the THyNK Creation Studio from its real editable-canvas contract rather than reducing it to a generic generator page.
6. Audit PawMoji keyboard archives separately for missing native Android IME code/assets.
7. Re-run/inspect current-head CI after every code-bearing change before claiming a pass.

## Not production-complete / service-level blockers

- **Fully rigged Patsy animation:** the Rive cloud project `patsy1_3.3.8` exists with the stable `PatsyAssistant` artboard/state-machine/View Model contract, but no production `.riv` export has been verified. The transparent generated fallback is not the finished Rive-quality rig.
- **Authentication and Owner setup:** UI/contracts and fail-closed integration exist, but production auth endpoint/database/session issuer/OWNER claim authority still require secure configuration and verification.
- **Email:** no production email provider/backend is verified.
- **AI/web search:** no production provider or secure proxy is verified.
- **DMs, scheduling and Creation Studio providers:** UI/architecture exists, but production messaging, publishing and AI-generation services are not yet verified end-to-end.
- **Social provider branding:** platform names/logos must only appear where current integration terms permit them; otherwise use generic Share/Publish/Download to Share actions.
- **Under-16 safety:** restrictions must stay enforced at backend/security boundaries, not only in UI.

## Android Studio

Open the repository as the Android project, select a JDK supported by Android Gradle Plugin 8.7.3 (JDK 17 or 21), sync, and run the `app` configuration. CI currently uses JDK 21 while app bytecode targets JVM 17.
