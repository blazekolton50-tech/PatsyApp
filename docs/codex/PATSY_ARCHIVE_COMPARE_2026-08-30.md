# Patsy archive-to-GitHub comparison — 2026-08-30

Status: evidence-based merge gate for Codex. Do not blindly restore historical ZIP contents.

## Archives compared

- `Patsy_Android_3.3.8_UPDATED.zip`
- `patsy1_TRIPLE_CHECKED.zip`
- current GitHub branch: `codex/patsy-rive-ui-foundation`

## Direct archive comparison

The two archives each contain 45 files at their project roots. Across them:

- 20 files are byte-identical.
- 5 shared files differ: `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/patsy/app/MainActivity.kt`, `docs/patsy_locked_config.json`, and `settings.gradle.kts`.
- `Patsy_Android_3.3.8_UPDATED.zip` uniquely contains the older direct `BackendClient.kt`, `PatsyActor.kt`, a TypeScript/Postgres backend prototype, Gradle wrapper files, and older 3.3.8 status docs.
- `patsy1_TRIPLE_CHECKED.zip` uniquely contains the later approved/generated Patsy reference sheets, official white Patsy logo, generated fallback drawable, triple-check/build-fix docs, and Codex handoff docs.

## Current GitHub is newer in critical runtime architecture

The current branch `MainActivity.kt` already uses:

- `AuthGateway` / `PatsyServiceBindings` rather than the archive's direct `BackendClient` HTTP client.
- fail-closed `OwnerAuthorizationGate` / server capability concepts rather than local owner flags.
- `PatsyRigCoordinator`, `PatsyRiveRuntimeAdapter`, and `PatsyRiveHost` rather than the archive's static/cropped `PatsyActor` image composable.
- `PatsyHeader()` and `PatsyPrimaryButton()` from the locked visual system.

The current `app/build.gradle.kts` also includes the official Rive Android runtime and the current test dependency. The Gradle wrapper is already present on the branch (`gradlew` plus Gradle 8.10.2 wrapper configuration).

## Exact decisions

### DO NOT MERGE — superseded implementation

1. `Patsy_Android_3.3.8_UPDATED/app/src/main/java/com/patsy/app/BackendClient.kt`
   - Superseded by current provider/service boundary and live Supabase direction.
   - Direct client/backend coupling would move the project backwards.

2. `Patsy_Android_3.3.8_UPDATED/app/src/main/java/com/patsy/app/PatsyActor.kt`
   - Uses a cropped/static bitmap actor.
   - Conflicts with the locked continuously rigged Patsy requirement and current Rive host/runtime boundary.

3. Both archive versions of `MainActivity.kt`
   - Current GitHub already contains newer auth, owner-security, Rive-rig, and locked visual-system integration.
   - Use archive MainActivity only as historical reference for any still-missing screen copy/flows; never replace the current file wholesale.

4. `Patsy_Android_3.3.8_UPDATED/backend/*`
   - Historical TypeScript/Postgres backend prototype only.
   - Do not replace or bypass the verified current Supabase architecture with it.

5. Old build/settings files
   - Do not overwrite current `app/build.gradle.kts`, `settings.gradle.kts`, manifest, wrapper, or version setup unless a specific verified build defect requires a targeted change.

### KEEP / ALREADY PRESENT

- `patsy_logo_official_white.png` from `patsy1_TRIPLE_CHECKED.zip` is byte-identical to the current GitHub asset by Git blob SHA: `4e0de650fd697c4399d72da6fb738e57c8b53cc5`.
- Gradle wrapper exists in current GitHub; no wrapper restore is needed.
- Current branch contains the Rive runtime dependency and current rig integration boundary.

### REVIEW AS ASSET/REFERENCE ONLY

- `patsy1_TRIPLE_CHECKED.zip` reference sheets under `app/src/main/assets/references/latest/`.
- `patsy_generated_main.png` differs from the current GitHub fallback asset. Do **not** replace current asset merely because it differs; compare visually against the latest locked realistic Patsy reference before any asset change.
- Triple-check/Codex/build-fix documentation may be retained as historical provenance if useful, but newer approved/locked specs override conflicting wording.

## Production Rive status

Neither of the audited Rive master ZIP packs contains a production `.riv` file. Preserve the current Rive ABI/runtime/fallback. Do not fake final animation with GIFs, sprite swaps, static pose switching, or a boxed character.

## Next Codex action

1. Keep current branch architecture.
2. Do not import the old `BackendClient`, `PatsyActor`, old `MainActivity`, or old backend prototype.
3. Review only the differing Patsy fallback/reference assets against the latest locked visual references.
4. Continue the existing plan: real Gradle verification, finish `MainActivity` wrapper migration, extract `PatsyCompanion`, preserve Rive fallback until a genuine production `.riv` passes validation.
5. Audit the PawMoji v0.2 archive separately for genuinely missing native Android IME code/assets.

This comparison follows the rule that newest explicitly APPROVED/LOCKED requirements and current verified architecture supersede older prototypes and dashboard completion claims.