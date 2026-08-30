# PatsyCompanion extraction — TDD RED checkpoint

This branch intentionally starts with a failing contract test before production refactoring.

Required result:
- extract the Patsy companion renderer from `MainActivity.kt` into `app/src/main/java/com/patsy/app/patsy/PatsyCompanion.kt`;
- preserve `PatsyRigCoordinator`, `PatsyRiveHost`, `PatsyRiveRuntimeAdapter`, the transparent fallback, and the locked no-fake-animation rule;
- do not redesign Patsy or the locked UI.

After the extraction, run `./gradlew testDebugUnitTest assembleDebug` and require green before proceeding.
