package com.patsy.app.auth

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DebugPreviewIsolationRegressionTest {
    @Test
    fun debugLauncherDoesNotMutateGlobalProductionAuthBinding() {
        val launcher = File("src/debug/java/com/patsy/app/DebugPreviewActivity.kt").readText()
        val main = File("src/main/java/com/patsy/app/MainActivity.kt").readText()

        assertFalse(
            launcher.contains("PatsyServiceBindings.authGateway ="),
            "Debug preview must be selected per MainActivity launch, never by replacing the process-wide production AuthGateway",
        )
        assertTrue(
            main.contains("resolveLaunchAuthGateway"),
            "MainActivity must resolve an activity-scoped gateway from source-set policy",
        )
    }
}
