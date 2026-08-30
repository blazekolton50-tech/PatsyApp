package com.patsy.app

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter
import kotlinx.coroutines.delay

private val CompanionCharcoal = Color(0xFF2A2B2E)
private val CompanionWhite = Color(0xFFF7F7F7)
private val CompanionRainbow = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF6B35),
        Color(0xFFFFD447),
        Color(0xFF4CD964),
        Color(0xFF36A9FF),
        Color(0xFF9B59FF),
        Color(0xFFFF4FA3),
    )
)

/**
 * Patsy's dedicated animated companion boundary.
 *
 * The real Rive asset activates only after PatsyRiveHost validates the locked rig contract.
 * Until then the approved transparent generated Patsy remains visible. Real reference photos are
 * identity references only and are never rendered here.
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

@Composable
fun PatsyMotion(
    label: String = "Hi! 🐾",
    pointing: Boolean = false,
    action: PatsyAction = if (pointing) PatsyAction.POINTING else PatsyAction.IDLE,
    modifier: Modifier = Modifier,
) {
    val riveRuntime = remember { PatsyRiveRuntimeAdapter() }
    val rigCoordinator = remember(riveRuntime) { PatsyRigCoordinator(riveRuntime) }
    val hostView = LocalView.current
    var stage by remember { mutableStateOf(PatsyStageSnapshot()) }
    val transition = rememberInfiniteTransition(label = "patsy-motion")
    val bob by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob",
    )
    val breathe by transition.animateFloat(
        0.985f,
        1.015f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe",
    )
    val look by transition.animateFloat(
        -1f,
        1f,
        infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "look",
    )
    val jump by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "jump",
    )

    val rigMotion = when {
        action == PatsyAction.JUMPING -> PatsyRigMotion.JUMP
        action == PatsyAction.POINTING || pointing -> PatsyRigMotion.POINT
        action == PatsyAction.CELEBRATE -> PatsyRigMotion.WAVE
        action == PatsyAction.SLEEPY -> PatsyRigMotion.LIE
        else -> PatsyRigMotion.IDLE
    }
    val rigExpression = when (action) {
        PatsyAction.THINKING -> PatsyRigExpression.CURIOUS
        PatsyAction.TALKING, PatsyAction.IDLE -> PatsyRigExpression.CHEEKY
        PatsyAction.POINTING -> PatsyRigExpression.PROUD
        PatsyAction.JUMPING, PatsyAction.HAPPY, PatsyAction.CELEBRATE -> PatsyRigExpression.EXCITED
        PatsyAction.WARNING -> PatsyRigExpression.CONCERNED
        PatsyAction.SLEEPY -> PatsyRigExpression.SLEEPY
    }
    val talking = action == PatsyAction.TALKING

    SideEffect {
        rigCoordinator.render(
            PatsyRigPose(
                motion = rigMotion,
                motionSpeed = when (rigMotion) {
                    PatsyRigMotion.JUMP -> 0.85f
                    PatsyRigMotion.WAVE, PatsyRigMotion.POINT -> 0.45f
                    else -> 0.12f
                },
                pointX = if (pointing || action == PatsyAction.POINTING) 0.88f else 0.5f,
                pointY = if (pointing || action == PatsyAction.POINTING) 0.55f else 0.5f,
                stageX = stage.x,
                stageY = stage.y,
                stageScale = breathe,
                lookX = look,
                lookY = (bob - 0.5f) * 0.12f,
                headTilt = -look * 0.18f,
                leftEarDrive = (look * 0.32f + (bob - 0.5f) * 0.08f).coerceIn(-1f, 1f),
                rightEarDrive = (-look * 0.22f + (bob - 0.5f) * 0.12f).coerceIn(-1f, 1f),
                earPhysicsEnabled = true,
                tailDrive = look * 0.2f,
                tailEnergy = when (action) {
                    PatsyAction.HAPPY, PatsyAction.CELEBRATE, PatsyAction.JUMPING -> 0.85f
                    PatsyAction.SLEEPY, PatsyAction.WARNING -> 0.18f
                    else -> 0.38f
                },
                expression = rigExpression,
                expressionIntensity = when (action) {
                    PatsyAction.IDLE -> 0.45f
                    PatsyAction.SLEEPY -> 0.65f
                    else -> 0.82f
                },
                talking = talking,
                viseme = if (talking) PatsyRigViseme.A else PatsyRigViseme.REST,
                visemeIntensity = if (talking) 0.25f + (bob * 0.5f) else 0f,
                speechEnergy = if (talking) 0.2f + (bob * 0.35f) else 0f,
            )
        )
    }

    LaunchedEffect(action, pointing) {
        when (rigMotion) {
            PatsyRigMotion.JUMP, PatsyRigMotion.WAVE, PatsyRigMotion.POINT ->
                rigCoordinator.retriggerAction(rigMotion)
            else -> Unit
        }
    }
    LaunchedEffect(rigCoordinator) {
        var blinkSequence = 0L
        while (true) {
            delay(patsyBlinkDelayMillis(blinkSequence++))
            rigCoordinator.blink()
        }
    }
    DisposableEffect(riveRuntime) {
        onDispose { riveRuntime.close() }
    }

    val x = when (action) {
        PatsyAction.JUMPING -> ((jump * 210f) - 105f).dp
        PatsyAction.POINTING -> 18.dp
        else -> 0.dp
    }
    val y = when (action) {
        PatsyAction.JUMPING -> (-32f * kotlin.math.sin(jump * Math.PI)).toFloat().dp
        else -> (12f * bob).dp
    }

    Box(modifier.fillMaxWidth().height(210.dp)) {
        Text(
            label,
            color = CompanionWhite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(CompanionCharcoal, RoundedCornerShape(22.dp))
                .padding(12.dp),
        )
        Box(
            modifier = Modifier
                .size(155.dp)
                .align(Alignment.Center)
                .offset(x = x, y = y)
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInWindow()
                    stage = normalisePatsyStage(
                        centreX = position.x + coordinates.size.width / 2f,
                        centreY = position.y + coordinates.size.height / 2f,
                        viewportWidth = hostView.width.toFloat(),
                        viewportHeight = hostView.height.toFloat(),
                    )
                }
                .graphicsLayer(
                    scaleX = breathe,
                    scaleY = breathe,
                    rotationY = look * 7f,
                    rotationZ = look * 1.8f,
                    translationX = look * 3f,
                ),
        ) {
            PatsyRiveHost(
                runtime = riveRuntime,
                modifier = Modifier.fillMaxSize(),
                fallback = {
                    Image(
                        painter = painterResource(R.drawable.patsy_generated_main),
                        contentDescription = "Patsy AI — moving generated fallback; validated Rive rig activates automatically when present",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }
        if (action == PatsyAction.POINTING || pointing) {
            Text(
                "🐾",
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-52).dp, y = 20.dp),
            )
        }
        if (action == PatsyAction.JUMPING) {
            Text(
                "↗",
                style = TextStyle(brush = CompanionRainbow, fontSize = 40.sp),
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-34).dp, y = (-28).dp),
            )
        }
    }
}
