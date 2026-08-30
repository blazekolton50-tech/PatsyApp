package com.patsy.app.patsy.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsy.app.R
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionIntent
import com.patsy.app.patsy.PatsyCompanionReaction
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigViseme
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter
import com.patsy.app.ui.PatsyColors

/**
 * High-level app actions retained for current screen call-sites while the companion itself is
 * driven through semantic [PatsyCompanionIntent] commands.
 */
enum class PatsyAction {
    IDLE,
    THINKING,
    TALKING,
    POINTING,
    JUMPING,
    HAPPY,
    WARNING,
    SLEEPY,
    CELEBRATE,
}

/**
 * Transparent, unboxed Patsy companion host.
 *
 * The production Rive asset activates only when it exists and validates against the locked ABI.
 * Until then the generated transparent Patsy fallback remains visible. No GIF, sprite sheet,
 * static pose sequence, decorative paw or arrow is used as a substitute for the real rig.
 */
@Composable
fun PatsyCompanion(
    label: String = "Hi!",
    pointing: Boolean = false,
    action: PatsyAction = if (pointing) PatsyAction.POINTING else PatsyAction.IDLE,
    target: PatsyCompanionTarget? = null,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val runtime = remember { PatsyRiveRuntimeAdapter() }
    val coordinator = remember(runtime) { PatsyRigCoordinator(runtime) }
    val controller = remember(coordinator) { PatsyCompanionController(coordinator) }
    val resolvedAction = if (pointing) PatsyAction.POINTING else action
    val resolvedTarget = target ?: PatsyCompanionTarget(0.88f, 0.55f)

    LaunchedEffect(resolvedAction, resolvedTarget, reducedMotion) {
        controller.dispatch(PatsyCompanionIntent.SetReducedMotion(reducedMotion))
        when (resolvedAction) {
            PatsyAction.IDLE -> controller.dispatch(PatsyCompanionIntent.Idle)
            PatsyAction.THINKING -> controller.dispatch(PatsyCompanionIntent.Think)
            PatsyAction.TALKING -> controller.dispatch(
                PatsyCompanionIntent.Speak(
                    viseme = PatsyRigViseme.A,
                    visemeIntensity = 0.45f,
                    speechEnergy = 0.45f,
                )
            )
            PatsyAction.POINTING -> controller.dispatch(PatsyCompanionIntent.PointAt(resolvedTarget))
            PatsyAction.JUMPING -> controller.dispatch(PatsyCompanionIntent.Jump)
            PatsyAction.HAPPY -> controller.dispatch(
                PatsyCompanionIntent.React(PatsyCompanionReaction.HAPPY)
            )
            PatsyAction.WARNING -> controller.dispatch(
                PatsyCompanionIntent.React(PatsyCompanionReaction.CONCERNED)
            )
            PatsyAction.SLEEPY -> controller.dispatch(PatsyCompanionIntent.Sleep)
            PatsyAction.CELEBRATE -> controller.dispatch(PatsyCompanionIntent.Celebrate)
        }
    }

    DisposableEffect(runtime) {
        onDispose { runtime.close() }
    }

    val transition = rememberInfiniteTransition(label = "patsy-fallback-motion")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fallback-bob",
    )
    val breathe by transition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fallback-breathe",
    )
    val look by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fallback-look",
    )

    val fallbackY = if (reducedMotion) 0.dp else (8f * bob).dp
    val fallbackScale = if (reducedMotion) 1f else breathe
    val fallbackLook = if (reducedMotion) 0f else look

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 156.dp),
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                color = PatsyColors.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(PatsyColors.CharcoalRaised, RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
                .offset(y = fallbackY)
                .graphicsLayer(
                    scaleX = fallbackScale,
                    scaleY = fallbackScale,
                    rotationY = fallbackLook * 5f,
                    rotationZ = fallbackLook * 1.2f,
                    translationX = fallbackLook * 2f,
                ),
        ) {
            PatsyRiveHost(
                runtime = runtime,
                modifier = Modifier.fillMaxSize(),
                playing = !reducedMotion,
                fallback = {
                    Image(
                        painter = painterResource(R.drawable.patsy_generated_main),
                        contentDescription = "Patsy AI companion",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }
    }
}

/** Compatibility wrapper for the existing app screens while MainActivity is decomposed. */
@Composable
fun PatsyMotion(
    label: String = "Hi!",
    pointing: Boolean = false,
    action: PatsyAction = if (pointing) PatsyAction.POINTING else PatsyAction.IDLE,
    modifier: Modifier = Modifier,
) {
    PatsyCompanion(
        label = label,
        pointing = pointing,
        action = action,
        modifier = modifier,
    )
}
