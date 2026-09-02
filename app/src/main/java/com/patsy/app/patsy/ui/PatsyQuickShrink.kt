package com.patsy.app.patsy.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionEffect
import com.patsy.app.patsy.PatsyCompanionEffectKind
import com.patsy.app.patsy.PatsyCompanionMode
import com.patsy.app.patsy.PatsyCompanionState
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

/**
 * Drop-in Compose entry matching the owner-facing API:
 *
 * PatsyQuickShrink(onMissionStart = { ... })
 *
 * It shrinks Big Patsy (300 / 1.0) to Mini Patsy (150 / 0.5) in 0.8 s, runs for 0.4 s,
 * arrives beside [target], points, then invokes [onMissionStart].
 */
@Composable
fun PatsyQuickShrink(
    onMissionStart: () -> Unit,
    target: PatsyCompanionTarget = PatsyCompanionTarget(0.82f, 0.30f),
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val runtime = remember { PatsyRiveRuntimeAdapter() }
    val rig = remember(runtime) { PatsyRigCoordinator(runtime) }
    var companionState by remember { mutableStateOf(PatsyCompanionState()) }
    var activeEffect by remember { mutableStateOf<PatsyCompanionEffect?>(null) }
    val controller = remember(rig) {
        PatsyCompanionController(
            rig = rig,
            onStateChanged = { companionState = it },
            onEffectRequested = { activeEffect = it },
        )
    }

    LaunchedEffect(reducedMotion) {
        controller.setReducedMotion(reducedMotion)
    }

    LaunchedEffect(target) {
        controller.guideTo(target)
        activeEffect = null
        onMissionStart()
    }

    DisposableEffect(runtime) {
        onDispose { runtime.close() }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (
            companionState.mode == PatsyCompanionMode.SHRINKING &&
            activeEffect?.kind == PatsyCompanionEffectKind.RISING_RAINBOW_GLITTER
        ) {
            val progress = ((1f - companionState.pose.stageScale) / 0.5f).coerceIn(0f, 1f)
            val effectSize = 420.dp
            val centerX = maxWidth * companionState.pose.stageX
            val centerY = maxHeight * companionState.pose.stageY
            PatsyShrinkRainbow(
                progress = progress,
                modifier = Modifier
                    .offset(x = centerX - effectSize / 2f, y = centerY - effectSize / 2f)
                    .size(effectSize),
            )
        }

        PatsyRiveHost(
            runtime = runtime,
            modifier = Modifier.fillMaxSize(),
            playing = !reducedMotion,
            fallback = {
                PatsyTravelFallback(
                    state = companionState,
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
    }
}
