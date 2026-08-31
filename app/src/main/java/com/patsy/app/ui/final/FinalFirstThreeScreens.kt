package com.patsy.app.ui.final

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.PatsyAction
import com.patsy.app.R
import com.patsy.app.Screen
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

val FinalBackground = Color(0xFF08090C)
val FinalPanel = Color(0xFF111216)
val FinalPanelRaised = Color(0xFF17181C)
val FinalWhite = Color(0xFFF7F7F8)
val FinalMuted = Color(0xFFB8B8BF)
val FinalLine = Color(0xFF34343C)

val FinalRainbow = Brush.horizontalGradient(
    listOf(
        Color(0xFF8B5CFF),
        Color(0xFFFF4FA3),
        Color(0xFFFF8A5B),
        Color(0xFFFFDF5A),
        Color(0xFF67E889),
        Color(0xFF56D6F5),
        Color(0xFF9B59FF),
        Color(0xFFFF4FA3),
    ),
)

@Composable
fun FinalLoginScreen(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    error: String,
    busy: Boolean,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    FinalAuthScreenShell(
        speech = FinalFirstThreeScreensContract.login.speech,
        title = FinalFirstThreeScreensContract.login.title,
    ) {
        FinalLabelledField("Username", username, "patsyowner_blaze", onValueChange = onUsernameChange)
        Spacer(Modifier.height(20.dp))
        FinalLabelledField("Email", email, "you@example.com", onValueChange = onEmailChange)
        Spacer(Modifier.height(28.dp))
        FinalOwnerSetupCard(rememberMe, onRememberMeChange)
        if (error.isNotBlank()) {
            Text(error, color = Color(0xFFFF7B86), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
        }
        Spacer(Modifier.height(18.dp))
        FinalRainbowButton(if (busy) "Logging in…" else "Login", !busy, onLogin)
        Spacer(Modifier.height(30.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(FinalLine))
        Spacer(Modifier.height(30.dp))
        FinalGradientTextLink("Forgot Password?", onForgotPassword)
    }
}

@Composable
fun FinalSetPasswordScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    error: String,
    busy: Boolean,
    onSaveAndLogin: () -> Unit,
    onBackToLogin: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    FinalAuthScreenShell(
        speech = FinalFirstThreeScreensContract.setPassword.speech,
        title = FinalFirstThreeScreensContract.setPassword.title,
    ) {
        FinalLabelledField("Set password", password, "•••••••", true, onPasswordChange)
        Spacer(Modifier.height(20.dp))
        FinalLabelledField("Confirm password", confirmPassword, "•••••••", true, onConfirmPasswordChange)
        Spacer(Modifier.height(28.dp))
        FinalOwnerSetupCard(rememberMe, onRememberMeChange)
        if (error.isNotBlank()) {
            Text(error, color = Color(0xFFFF7B86), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
        }
        Spacer(Modifier.height(18.dp))
        FinalRainbowButton(if (busy) "Setting password…" else "Set Password & Login", !busy, onSaveAndLogin)
        Spacer(Modifier.height(28.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.weight(1f).height(1.dp).background(FinalLine))
            Text("Back to Login", color = FinalWhite, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onBackToLogin).padding(4.dp))
            Box(Modifier.weight(1f).height(1.dp).background(FinalLine))
        }
        Spacer(Modifier.height(24.dp))
        Text("Forgot Password?", color = FinalWhite, fontSize = 15.sp, modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onForgotPassword).padding(4.dp))
    }
}

@Composable
private fun FinalAuthScreenShell(speech: String, title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(FinalBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 50.dp),
        ) {
            item { FinalAuthHero(speech) }
            item {
                FinalRainbowFrame(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).offset(y = (-16).dp),
                    radius = 38.dp,
                    borderWidth = 2.dp,
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp)) {
                        Text(title, color = FinalWhite, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))
                        content()
                    }
                }
            }
            item { FinalRainbowWave(Modifier.fillMaxWidth().height(40.dp).padding(top = 22.dp)) }
        }
    }
}

@Composable
private fun FinalAuthHero(speech: String) {
    Box(Modifier.fillMaxWidth().height(350.dp)) {
        Image(
            painter = painterResource(R.drawable.patsy_logo_official_white),
            contentDescription = "Patsy",
            contentScale = ContentScale.Fit,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 26.dp).width(210.dp).height(112.dp),
        )
        FinalPatsyCompanion(PatsyAction.TALKING, Modifier.align(Alignment.BottomStart).padding(start = 55.dp, bottom = 4.dp).size(230.dp))
        FinalSpeechBubble(speech, Modifier.align(Alignment.CenterEnd).padding(end = 34.dp, top = 28.dp).width(370.dp))
    }
}

@Composable
fun FinalPatsyCompanion(action: PatsyAction, modifier: Modifier = Modifier) {
    val riveRuntime = remember { PatsyRiveRuntimeAdapter() }
    val rigCoordinator = remember(riveRuntime) { PatsyRigCoordinator(riveRuntime) }
    val transition = rememberInfiniteTransition(label = "final-patsy-life")
    val breathe by transition.animateFloat(0.985f, 1.015f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "final-patsy-breathe")
    val look by transition.animateFloat(-0.2f, 0.2f, infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "final-patsy-look")
    val talkEnergy by transition.animateFloat(0.18f, 0.58f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "final-patsy-talk")
    val talking = action == PatsyAction.TALKING
    SideEffect {
        rigCoordinator.render(
            PatsyRigPose(
                motion = when (action) {
                    PatsyAction.POINTING -> PatsyRigMotion.POINT
                    PatsyAction.CELEBRATE -> PatsyRigMotion.WAVE
                    PatsyAction.JUMPING -> PatsyRigMotion.JUMP
                    PatsyAction.SLEEPY -> PatsyRigMotion.LIE
                    else -> PatsyRigMotion.IDLE
                },
                motionSpeed = if (talking) 0.2f else 0.12f,
                pointX = if (action == PatsyAction.POINTING) 0.85f else 0.5f,
                pointY = if (action == PatsyAction.POINTING) 0.55f else 0.5f,
                lookX = look,
                headTilt = -look * 0.18f,
                leftEarDrive = look * 0.2f,
                rightEarDrive = -look * 0.15f,
                earPhysicsEnabled = true,
                tailDrive = look * 0.18f,
                tailEnergy = if (talking) 0.42f else 0.26f,
                expression = when (action) {
                    PatsyAction.WARNING -> PatsyRigExpression.CONCERNED
                    PatsyAction.THINKING -> PatsyRigExpression.CURIOUS
                    PatsyAction.POINTING -> PatsyRigExpression.PROUD
                    PatsyAction.CELEBRATE, PatsyAction.HAPPY, PatsyAction.JUMPING -> PatsyRigExpression.EXCITED
                    PatsyAction.SLEEPY -> PatsyRigExpression.SLEEPY
                    else -> PatsyRigExpression.CHEEKY
                },
                expressionIntensity = 0.72f,
                talking = talking,
                viseme = if (talking) PatsyRigViseme.A else PatsyRigViseme.REST,
                visemeIntensity = if (talking) talkEnergy else 0f,
                speechEnergy = if (talking) talkEnergy else 0f,
            ),
        )
    }
    LaunchedEffect(action) {
        when (action) {
            PatsyAction.POINTING -> rigCoordinator.retriggerAction(PatsyRigMotion.POINT)
            PatsyAction.CELEBRATE -> rigCoordinator.retriggerAction(PatsyRigMotion.WAVE)
            PatsyAction.JUMPING -> rigCoordinator.retriggerAction(PatsyRigMotion.JUMP)
            else -> Unit
        }
    }
    DisposableEffect(riveRuntime) { onDispose { riveRuntime.close() } }
    Box(modifier.graphicsLayer(scaleX = breathe, scaleY = breathe)) {
        PatsyRiveHost(
            runtime = riveRuntime,
            modifier = Modifier.fillMaxSize(),
            fallback = {
                Image(
                    painter = painterResource(R.drawable.patsy_generated_talking),
                    contentDescription = "Patsy AI companion",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
    }
}

@Composable
private fun FinalSpeechBubble(text: String, modifier: Modifier = Modifier) {
    FinalRainbowFrame(modifier, 28.dp, 2.dp) {
        Text(text, color = FinalWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp))
    }
}

@Composable
private fun FinalLabelledField(
    label: String,
    value: String,
    hint: String,
    password: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    var showSecret by remember { mutableStateOf(false) }
    Column {
        Text(label, color = FinalWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 6.dp, bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FinalRainbow, RoundedCornerShape(34.dp))
                .padding(1.6.dp)
                .background(FinalPanel, RoundedCornerShape(33.dp))
                .padding(horizontal = 18.dp, vertical = 2.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                textStyle = TextStyle(color = FinalWhite, fontSize = 16.sp),
                singleLine = true,
                cursorBrush = SolidColor(FinalWhite),
                visualTransformation = if (password && !showSecret) PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { inner ->
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty()) Text(hint, color = Color(0xFF8D8D96), fontSize = 16.sp)
                            inner()
                        }
                        if (password) {
                            Text(if (showSecret) "◉" else "◎", color = Color(0xFF8D8D96), fontSize = 26.sp, modifier = Modifier.clickable { showSecret = !showSecret }.padding(8.dp))
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun FinalOwnerSetupCard(rememberMe: Boolean, onRememberMeChange: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Owner Profile login setup", color = Color(0xFFD0D0D4), fontSize = 15.sp, modifier = Modifier.padding(bottom = 14.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(FinalPanelRaised, RoundedCornerShape(28.dp)).border(1.dp, Color(0xFF3A3A42), RoundedCornerShape(28.dp)).padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FinalRainbowAvatar()
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Blaze profile", color = FinalWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Owner • Blaze", color = FinalMuted, fontSize = 14.sp)
            }
            Row(Modifier.clickable { onRememberMeChange(!rememberMe) }.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FinalRememberMeBox(rememberMe)
                Spacer(Modifier.width(8.dp))
                Text("Remember Me", color = FinalWhite, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun FinalRainbowAvatar() {
    Box(Modifier.size(62.dp).background(FinalRainbow, CircleShape).padding(3.dp).background(FinalPanelRaised, CircleShape).padding(3.dp)) {
        Image(
            painter = painterResource(R.drawable.patsy_owner_profile_mock),
            contentDescription = "Owner profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
        )
    }
}

@Composable
private fun FinalRememberMeBox(checked: Boolean) {
    Box(Modifier.size(28.dp).background(FinalRainbow, RoundedCornerShape(7.dp)).padding(2.dp).background(FinalPanelRaised, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
        if (checked) Text("✓", color = Color(0xFF68E6F4), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FinalRainbowButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(66.dp).graphicsLayer(alpha = if (enabled) 1f else 0.55f).background(FinalRainbow, RoundedCornerShape(34.dp)).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
    }
}

@Composable
private fun FinalGradientTextLink(text: String, onClick: () -> Unit) {
    Text(text, style = TextStyle(brush = FinalRainbow, fontSize = 18.sp, fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(6.dp), textAlign = TextAlign.Center)
}

@Composable
private fun FinalRainbowFrame(
    modifier: Modifier = Modifier,
    radius: Dp = 26.dp,
    borderWidth: Dp = 1.5.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.background(FinalRainbow, RoundedCornerShape(radius)).padding(borderWidth).background(FinalPanel, RoundedCornerShape(radius - borderWidth)),
        content = content,
    )
}

@Composable
fun FinalHomeScreen(onNavigate: (Screen) -> Unit) {
    Box(Modifier.fillMaxSize().background(FinalBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 108.dp),
        ) {
            item { FinalHomeTopBar() }
            item { FinalAskBar() }
            item {
                Text("Hey! What can I help with today? 💜", color = FinalWhite, fontSize = 18.sp, fontStyle = FontStyle.Italic, modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp))
            }
            item { FinalContinueDesigns() }
            item { FinalTodaySection() }
            item { FinalCreatePost() }
            item { FinalFeedTabs() }
            item { FinalFeed() }
            item { Spacer(Modifier.height(78.dp)) }
        }
        FinalPatsyCompanion(PatsyAction.HAPPY, Modifier.align(Alignment.BottomEnd).padding(end = 2.dp, bottom = 122.dp).size(210.dp))
        Column(Modifier.align(Alignment.BottomCenter)) {
            FinalRainbowWave(Modifier.fillMaxWidth().height(28.dp))
            FinalHomeNavigation(onNavigate)
        }
    }
}

@Composable
private fun FinalHomeTopBar() {
    Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 22.dp, top = 10.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))
        Image(painterResource(R.drawable.patsy_logo_official_white), "Patsy", Modifier.width(118.dp).height(62.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.weight(1f))
        FinalTopCircle("♧", notification = true)
        Spacer(Modifier.width(10.dp))
        FinalTopCircle("•••", rainbow = true)
    }
}

@Composable
private fun FinalTopCircle(text: String, notification: Boolean = false, rainbow: Boolean = false) {
    Box(
        modifier = Modifier.size(48.dp).then(if (rainbow) Modifier.background(FinalRainbow, CircleShape).padding(1.6.dp).background(FinalBackground, CircleShape) else Modifier.border(1.dp, Color(0xFF55555F), CircleShape)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = FinalWhite, fontSize = if (text == "•••") 17.sp else 23.sp)
        if (notification) Box(Modifier.align(Alignment.TopEnd).offset(x = (-2).dp, y = 1.dp).size(10.dp).background(Color(0xFFE83F75), CircleShape))
    }
}

@Composable
private fun FinalAskBar() {
    FinalRainbowFrame(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 4.dp), 28.dp, 1.8.dp) {
        Row(Modifier.fillMaxWidth().height(66.dp).padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✦", style = TextStyle(brush = FinalRainbow, fontSize = 26.sp))
            Spacer(Modifier.width(12.dp))
            Text("Ask Patsy anything...", color = Color(0xFFC9C9CF), fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("♩", color = FinalWhite, fontSize = 28.sp)
        }
    }
}

@Composable
private fun FinalContinueDesigns() {
    FinalSectionFrame {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("CONTINUE DESIGNS", color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.weight(1f))
            Text("View all ›", color = Color(0xFFB36BFF), fontSize = 13.sp)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 10.dp, vertical = 0.dp).padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FinalDesignCard(R.drawable.home_continue_dog, "Image", "Happy dog post", "Edited 2h ago", Color(0xFF8258FF))
            FinalDesignCard(R.drawable.home_continue_sunset, "Video", "Beach vibes", "Edited yesterday", Color(0xFFFF417A))
            FinalDesignCard(R.drawable.home_continue_adopt, "Post", "Adopt don’t shop", "Edited 2d ago", Color(0xFFFFA326))
            FinalDesignCard(R.drawable.home_continue_tip, "Template", "Dog tip template", "Edited 3d ago", Color(0xFF9B59FF))
            FinalNewDesignCard()
        }
    }
}

@Composable
private fun FinalDesignCard(imageRes: Int, chip: String, title: String, subtitle: String, chipColor: Color) {
    Column(Modifier.width(154.dp).height(220.dp).background(Color(0xFF0D0E11), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF373841), RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))) {
        Box(Modifier.fillMaxWidth().height(145.dp)) {
            Image(painterResource(imageRes), title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Text(chip, color = FinalWhite, fontSize = 11.sp, modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(chipColor, RoundedCornerShape(9.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Text(title, color = FinalWhite, fontSize = 12.sp, maxLines = 1, modifier = Modifier.padding(start = 9.dp, top = 8.dp, end = 4.dp))
        Text(subtitle, color = FinalMuted, fontSize = 10.sp, maxLines = 1, modifier = Modifier.padding(start = 9.dp, top = 2.dp))
    }
}

@Composable
private fun FinalNewDesignCard() {
    Column(Modifier.width(142.dp).height(220.dp).background(Color(0xFF0D0E11), RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF373841), RoundedCornerShape(16.dp)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(64.dp).background(FinalRainbow, CircleShape).padding(2.dp).background(FinalBackground, CircleShape), contentAlignment = Alignment.Center) { Text("+", color = FinalWhite, fontSize = 36.sp, fontWeight = FontWeight.Light) }
        Spacer(Modifier.height(14.dp))
        Text("New Design", color = FinalWhite, fontSize = 12.sp)
    }
}

@Composable
private fun FinalTodaySection() {
    FinalSectionFrame {
        Text("TODAY", color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 8.dp))
        Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FinalTodayItem("▣", "2 scheduled posts", "Next one at 2:00 PM", Color(0xFFFF3E9A), Modifier.weight(1f))
            FinalTodayItem("♧", "1 reminder", "Vet appointment\nat 4:30 PM", Color(0xFFFFBD46), Modifier.weight(1f))
            FinalTodayItem("●", "Pet Awareness Day", "National Dog Day\n– 26 Aug", Color(0xFF5BE56B), Modifier.weight(1f))
        }
    }
}

@Composable
private fun FinalTodayItem(icon: String, title: String, subtitle: String, iconColor: Color, modifier: Modifier) {
    Row(modifier.padding(horizontal = 7.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(47.dp).border(1.5.dp, iconColor, CircleShape), contentAlignment = Alignment.Center) { Text(icon, color = iconColor, fontSize = 20.sp) }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(subtitle, color = FinalMuted, fontSize = 10.sp, lineHeight = 13.sp)
        }
        Text("›", color = FinalWhite, fontSize = 26.sp)
    }
}

@Composable
private fun FinalCreatePost() {
    FinalSectionFrame {
        Text("CREATE POST", color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 8.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.home_continue_dog), "Profile", Modifier.size(46.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(10.dp))
            Row(Modifier.weight(1f).height(52.dp).border(1.dp, Color(0xFF3A3B43), RoundedCornerShape(14.dp)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Share something with the community...", color = FinalMuted, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("☺", color = FinalWhite, fontSize = 23.sp)
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FinalPostTool("▣", "Image", Color(0xFF9B59FF))
            FinalPostTool("■", "Video", Color(0xFFFF3F75))
            FinalPostTool("▤", "Document", Color(0xFF4F9EFF))
            FinalPostTool("▦", "Template", Color(0xFF9B59FF))
            FinalPostTool("•••", "More", Color(0xFF9B59FF))
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            FinalSmallAction("@", "Tag people", Modifier.weight(1f))
            FinalSmallAction("#", "Add hashtag", Modifier.weight(1f))
            Box(Modifier.width(128.dp).height(44.dp).background(FinalRainbow, RoundedCornerShape(15.dp)).padding(1.6.dp).background(Color(0xFFF4F4F4), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text("POST", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun FinalPostTool(icon: String, label: String, color: Color) {
    Row(Modifier.width(128.dp).height(43.dp).border(1.dp, Color(0xFF383945), RoundedCornerShape(13.dp)).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = color, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Text(label, color = FinalWhite, fontSize = 12.sp)
    }
}

@Composable
private fun FinalSmallAction(icon: String, label: String, modifier: Modifier) {
    Row(modifier.height(44.dp).border(1.dp, Color(0xFF383945), RoundedCornerShape(13.dp)).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = FinalWhite, fontSize = 19.sp)
        Spacer(Modifier.width(8.dp))
        Text(label, color = FinalWhite, fontSize = 11.sp)
    }
}

@Composable
private fun FinalFeedTabs() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        FinalFirstThreeScreensContract.home.feedTabs.forEachIndexed { index, tab ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(tab, color = if (index == 0) FinalWhite else FinalMuted, fontSize = 12.sp)
                if (index == 0) Box(Modifier.padding(top = 6.dp).width(58.dp).height(2.dp).background(FinalRainbow))
            }
        }
        Text("☷", color = FinalWhite, fontSize = 18.sp)
    }
}

@Composable
private fun FinalFeed() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.home_continue_dog), null, Modifier.size(38.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Sarah & Buddy", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("2h ago · ◉", color = FinalMuted, fontSize = 10.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text("morning for a walk with my boy Buddy 🐾☀", color = FinalWhite, fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FinalFeedImage(R.drawable.home_feed_dog1, Modifier.weight(1f))
            FinalFeedImage(R.drawable.home_feed_dog2, Modifier.weight(1f))
            FinalFeedImage(R.drawable.home_feed_dog3, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.home_feed_dog1), null, Modifier.size(38.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Pawsome Life", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("5h ago · ◉", color = FinalMuted, fontSize = 10.sp)
            }
        }
        Text("Show me the funniest thing your dog has ever done! 😂 👍", color = FinalWhite, fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp, bottom = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
            Text("♥  42", color = FinalWhite, fontSize = 14.sp)
            Text("◯  32", color = FinalWhite, fontSize = 14.sp)
            Text("↗  12", color = FinalWhite, fontSize = 14.sp)
            Text("⌑", color = FinalWhite, fontSize = 18.sp)
        }
    }
}

@Composable
private fun FinalFeedImage(imageRes: Int, modifier: Modifier) {
    Box(modifier.background(FinalRainbow, RoundedCornerShape(14.dp)).padding(1.6.dp).background(FinalPanel, RoundedCornerShape(13.dp)).padding(1.dp)) {
        Image(painterResource(imageRes), null, Modifier.fillMaxWidth().aspectRatio(1.28f).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun FinalSectionFrame(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 5.dp).border(1.dp, Color(0xFF34353C), RoundedCornerShape(22.dp)).background(Color(0x8A0C0D10), RoundedCornerShape(22.dp)), content = content)
}

@Composable
private fun FinalHomeNavigation(onNavigate: (Screen) -> Unit) {
    Row(Modifier.fillMaxWidth().height(86.dp).background(Color(0xFA08090C)).padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        FinalNavItem("⌂", "Home", true) { onNavigate(Screen.HOME) }
        FinalNavItem("THYNK", "THYNK", false, true) { onNavigate(Screen.CHAT) }
        FinalCenterCreate { onNavigate(Screen.CREATE) }
        FinalNavItem("◌", "PDMs", false) { onNavigate(Screen.DMS) }
        FinalNavItem("♙", "Profile", false) { onNavigate(Screen.MORE) }
    }
}

@Composable
private fun FinalNavItem(icon: String, label: String, active: Boolean, wide: Boolean = false, onClick: () -> Unit) {
    Column(Modifier.width(if (wide) 96.dp else 60.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        if (wide) Text(icon, style = TextStyle(brush = FinalRainbow, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold))
        else Text(icon, color = if (active) Color(0xFFFF56AF) else FinalMuted, fontSize = 27.sp)
        Text(label, color = if (active) Color(0xFFFF56AF) else FinalMuted, fontSize = 10.sp)
    }
}

@Composable
private fun FinalCenterCreate(onClick: () -> Unit) {
    Box(Modifier.offset(y = (-16).dp).size(90.dp).background(FinalRainbow, CircleShape).padding(5.dp).background(FinalBackground, CircleShape).padding(5.dp).background(Color.White, CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text("+", color = Color.Black, fontSize = 46.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun FinalRainbowWave(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.63f)
            cubicTo(size.width * 0.28f, size.height * 0.76f, size.width * 0.42f, size.height * 0.18f, size.width * 0.52f, size.height * 0.25f)
            cubicTo(size.width * 0.64f, size.height * 0.33f, size.width * 0.76f, size.height * 0.72f, size.width, size.height * 0.62f)
        }
        drawPath(path, brush = FinalRainbow, style = Stroke(width = 4.dp.toPx()))
    }
}
