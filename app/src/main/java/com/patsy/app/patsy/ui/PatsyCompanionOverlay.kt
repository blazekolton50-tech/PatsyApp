package com.patsy.app.patsy.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsy.app.R
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionState
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

/** Commands that can move the one app-wide Patsy companion over the current page. */
sealed interface PatsyCompanionCommand {
    data class GuideTo(val target: PatsyCompanionTarget) : PatsyCompanionCommand
    data class QuickShrink(val onMissionStart: () -> Unit) : PatsyCompanionCommand
    data object ReturnHome : PatsyCompanionCommand
}

/**
 * One transparent, full-screen Patsy layer for authenticated THyNK-IN pages.
 *
 * The command sequence is owned by [PatsyCompanionController]: Patsy shrinks, walks across the
 * page, arrives beside the target and points; ReturnHome walks her back and expands her again.
 * This overlay never substitutes a second mini/chibi Patsy. The same Rive instance (or the same
 * transparent safe fallback while the production .riv is absent) is scaled and repositioned.
 */
@Composable
fun PatsyCompanionOverlay(
    command: PatsyCompanionCommand?,
    onCommandConsumed: () -> Unit,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val runtime = remember { PatsyRiveRuntimeAdapter() }
    val rig = remember(runtime) { PatsyRigCoordinator(runtime) }
    var companionState by remember { mutableStateOf(PatsyCompanionState()) }
    val controller = remember(rig) {
        PatsyCompanionController(
            rig = rig,
            onStateChanged = { companionState = it },
        )
    }

    LaunchedEffect(reducedMotion) {
        controller.setReducedMotion(reducedMotion)
    }

    LaunchedEffect(command) {
        when (command) {
            is PatsyCompanionCommand.GuideTo -> controller.guideTo(command.target)
            is PatsyCompanionCommand.QuickShrink -> {
                controller.shrinkForMission()
                command.onMissionStart()
            }
            PatsyCompanionCommand.ReturnHome -> controller.returnHome()
            null -> return@LaunchedEffect
        }
        onCommandConsumed()
    }

    DisposableEffect(runtime) {
        onDispose { runtime.close() }
    }

    PatsyRiveHost(
        runtime = runtime,
        modifier = Modifier.fillMaxSize(),
        playing = !reducedMotion,
        fallback = {
            PatsyTravelFallback(
                state = companionState,
                modifier = modifier.fillMaxSize(),
            )
        },
    )
}

/**
 * Visible safe fallback until a genuine validated patsy_assistant.riv exists.
 * It deliberately uses one source image and moves/scales that same Patsy around the page.
 */
@Composable
private fun PatsyTravelFallback(
    state: PatsyCompanionState,
    modifier: Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val baseSize = 220.dp
        val scaledSize = baseSize * state.pose.stageScale
        val x = (maxWidth * state.pose.stageX) - (scaledSize / 2f)
        val y = (maxHeight * state.pose.stageY) - (scaledSize / 2f)

        Image(
            painter = painterResource(R.drawable.patsy_generated_main),
            contentDescription = "Patsy assistant",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = x, y = y)
                .size(scaledSize),
        )
    }
}
