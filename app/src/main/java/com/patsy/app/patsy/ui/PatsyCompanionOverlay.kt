package com.patsy.app.patsy.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsy.app.R
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionEffect
import com.patsy.app.patsy.PatsyCompanionEffectKind
import com.patsy.app.patsy.PatsyCompanionMode
import com.patsy.app.patsy.PatsyCompanionState
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter
import kotlin.math.cos
import kotlin.math.sin

sealed interface PatsyCompanionCommand {
    data class GuideTo(val target: PatsyCompanionTarget) : PatsyCompanionCommand
    data object ReturnHome : PatsyCompanionCommand
}

/** One transparent, full-screen Patsy layer for authenticated THyNK-IN pages. */
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
    var activeEffect by remember { mutableStateOf<PatsyCompanionEffect?>(null) }
    val controller = remember(rig) {
        PatsyCompanionController(
            rig = rig,
            onStateChanged = { state -> companionState = state },
            onEffectRequested = { effect -> activeEffect = effect },
        )
    }

    LaunchedEffect(reducedMotion) {
        controller.setReducedMotion(reducedMotion)
    }

    LaunchedEffect(command) {
        when (command) {
            is PatsyCompanionCommand.GuideTo -> controller.guideTo(command.target)
            PatsyCompanionCommand.ReturnHome -> controller.returnHome()
            null -> return@LaunchedEffect
        }
        activeEffect = null
        onCommandConsumed()
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

/** Native fallback for the requested rising rainbow/glitter effect while the video asset is absent. */
@Composable
internal fun PatsyShrinkRainbow(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = remember {
        listOf(
            Color(0xFFFF3C78),
            Color(0xFFFF8200),
            Color(0xFFFFE600),
            Color(0xFF50FF78),
            Color(0xFF50B4FF),
            Color(0xFFA050FF),
            Color(0xFFFF50FF),
        )
    }
    Canvas(modifier = modifier) {
        val p = progress.coerceIn(0f, 1f)
        val centre = Offset(size.width / 2f, size.height / 2f)
        repeat(20) { k ->
            val phase = ((p + k * 0.05f) % 1f)
            val angle = Math.toRadians((k * 18f + p * 360f).toDouble())
            val radius = size.minDimension * (0.18f + phase * 0.36f)
            val x = centre.x + cos(angle).toFloat() * radius
            val y = centre.y + sin(angle).toFloat() * radius * 0.8f - phase * size.height * 0.14f
            drawCircle(
                color = colors[k % colors.size],
                radius = size.minDimension * (0.008f + phase * 0.012f),
                center = Offset(x, y),
                alpha = 1f - phase * 0.5f,
            )
        }
    }
}

/** Same approved Patsy image is moved/scaled; no mini sprite or pose swap is introduced. */
@Composable
internal fun PatsyTravelFallback(
    state: PatsyCompanionState,
    modifier: Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val baseSize = 300.dp
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
