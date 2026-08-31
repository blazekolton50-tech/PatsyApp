package com.patsy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.account.AccountBootstrapResult
import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.debug.DebugTestAccessFactory
import com.patsy.app.auth.ui.DataStoreRememberMePreferenceStore
import com.patsy.app.auth.ui.RememberMeCoordinator
import com.patsy.app.navigation.FinalShellNavigation
import com.patsy.app.navigation.NavigationDecision
import com.patsy.app.navigation.ShellDestination
import com.patsy.app.navigation.ShellNavigationGate
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalDebugSetPasswordRoute
import com.patsy.app.ui.finaldesign.FinalHomeDestination
import com.patsy.app.ui.finaldesign.FinalHomeScreen
import com.patsy.app.ui.finaldesign.FinalLoginRoute
import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar
import com.patsy.app.ui.finaldesign.FinalWhite
import kotlinx.coroutines.launch

/**
 * SAVE MAIN APP / LOCK IN SAVE launcher for the three FINAL approved pages.
 *
 * The legacy MainActivity remains in the branch for the still-unmigrated secondary pages, but this
 * activity is the launcher so Login, Set Password and Home use the final visual contract now.
 */
class FinalMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FinalPatsyApp(initialDeepLink = intent?.dataString) }
    }
}

private enum class FinalAppPage {
    LOGIN,
    DEBUG_SET_PASSWORD,
    HOME,
    THYNK,
    CREATE,
    DMS,
    PROFILE,
    PROTECTED,
}

@Composable
private fun FinalPatsyApp(initialDeepLink: String?) {
    val context = LocalContext.current.applicationContext
    val authGateway = remember { PatsyServiceBindings.authGateway }
    val accountBootstrapService = remember { PatsyServiceBindings.accountBootstrapService }
    val debugTestAccess = remember(context) { DebugTestAccessFactory.create(context) }
    val rememberMeStore = remember(context) { DataStoreRememberMePreferenceStore(context) }
    val rememberMeCoordinator = remember(rememberMeStore) { RememberMeCoordinator(rememberMeStore) }
    var page by remember { mutableStateOf(FinalAppPage.LOGIN) }
    var session by remember { mutableStateOf<PublicSession?>(null) }
    var bootstrapResult by remember { mutableStateOf<AccountBootstrapResult?>(null) }
    var bootstrapLoading by remember { mutableStateOf(false) }
    var debugPreview by remember { mutableStateOf(false) }
    var pendingKeepSignedIn by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun acceptSession(authenticated: PublicSession) {
        val isDebugPreview = BuildConfig.DEBUG &&
            authenticated.sessionId.startsWith("debug-") &&
            authenticated.userId.startsWith("debug-")
        session = authenticated
        debugPreview = isDebugPreview
        bootstrapResult = null
        bootstrapLoading = !isDebugPreview
        page = FinalAppPage.HOME
    }

    fun signOut() {
        scope.launch {
            debugTestAccess.logout()
            rememberMeCoordinator.signOut(authGateway)
            session = null
            bootstrapResult = null
            bootstrapLoading = false
            debugPreview = false
            page = FinalAppPage.LOGIN
        }
    }

    fun navigate(destination: FinalHomeDestination) {
        if (debugPreview) {
            // Debug APK preview only. This bypass never grants backend or Owner capabilities and
            // cannot exist in the release implementation of DebugTestAccess.
            page = destination.toPage()
            return
        }

        val account = (bootstrapResult as? AccountBootstrapResult.Available)?.account
        if (account == null) {
            page = FinalAppPage.PROTECTED
            return
        }

        page = when (FinalShellNavigation.authorize(destination, account)) {
            is NavigationDecision.Allowed -> destination.toPage()
            is NavigationDecision.Denied -> FinalAppPage.PROTECTED
        }
    }

    LaunchedEffect(authGateway, debugTestAccess) {
        val debugSession = debugTestAccess.restoreSessionIfRemembered()
        if (debugSession != null) {
            acceptSession(debugSession)
            return@LaunchedEffect
        }
        when (val restored = rememberMeCoordinator.restoreSession(authGateway)) {
            is SessionState.Authenticated -> acceptSession(restored.session)
            SessionState.Anonymous,
            is SessionState.Expired,
            is SessionState.Unavailable -> Unit
        }
    }

    LaunchedEffect(session?.sessionId, accountBootstrapService, debugPreview) {
        val activeSession = session ?: run {
            bootstrapResult = null
            bootstrapLoading = false
            return@LaunchedEffect
        }
        if (debugPreview) {
            bootstrapResult = null
            bootstrapLoading = false
            return@LaunchedEffect
        }

        bootstrapLoading = true
        val fetched = accountBootstrapService.fetch(activeSession)
        if (session?.sessionId == activeSession.sessionId && !debugPreview) {
            bootstrapResult = fetched
            bootstrapLoading = false
        }
    }

    LaunchedEffect(initialDeepLink, bootstrapResult, debugPreview) {
        if (initialDeepLink.isNullOrBlank() || debugPreview) return@LaunchedEffect
        val account = (bootstrapResult as? AccountBootstrapResult.Available)?.account
            ?: return@LaunchedEffect
        page = when (val decision = ShellNavigationGate.resolveDeepLink(initialDeepLink, account)) {
            is NavigationDecision.Allowed -> decision.route.destination.toFinalPage() ?: FinalAppPage.PROTECTED
            is NavigationDecision.Denied -> FinalAppPage.PROTECTED
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = FinalCharcoal,
            surface = FinalCharcoal,
            primary = FinalWhite,
        ),
    ) {
        Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {
            when (page) {
                FinalAppPage.LOGIN -> FinalLoginRoute(
                    authGateway = authGateway,
                    debugTestAccess = debugTestAccess,
                    rememberMeCoordinator = rememberMeCoordinator,
                    onAuthenticated = ::acceptSession,
                    onNeedDebugPasswordSetup = { keepSignedIn ->
                        pendingKeepSignedIn = keepSignedIn
                        page = FinalAppPage.DEBUG_SET_PASSWORD
                    },
                )

                FinalAppPage.DEBUG_SET_PASSWORD -> FinalDebugSetPasswordRoute(
                    keepSignedIn = pendingKeepSignedIn,
                    debugTestAccess = debugTestAccess,
                    onAuthenticated = ::acceptSession,
                    onBack = { page = FinalAppPage.LOGIN },
                )

                FinalAppPage.HOME -> when {
                    debugPreview -> FinalHomeScreen(onNavigate = ::navigate)
                    bootstrapLoading -> FinalProtectedAccessState(
                        message = "Checking your account securely…",
                        onSignOut = ::signOut,
                    )
                    else -> {
                        val account = (bootstrapResult as? AccountBootstrapResult.Available)?.account
                        if (
                            account != null &&
                            FinalShellNavigation.authorize(FinalHomeDestination.HOME, account) is NavigationDecision.Allowed
                        ) {
                            FinalHomeScreen(onNavigate = ::navigate)
                        } else {
                            FinalProtectedAccessState(
                                message = "We couldn't securely verify access to this area. Your account stays protected.",
                                onSignOut = ::signOut,
                            )
                        }
                    }
                }

                FinalAppPage.THYNK,
                FinalAppPage.CREATE,
                FinalAppPage.DMS,
                FinalAppPage.PROFILE -> {
                    val selectedDestination = page.toDestination()
                    val account = (bootstrapResult as? AccountBootstrapResult.Available)?.account
                    val authorized = debugPreview || (
                        account != null &&
                            FinalShellNavigation.authorize(selectedDestination, account) is NavigationDecision.Allowed
                        )
                    if (!authorized) {
                        FinalProtectedAccessState(
                            message = "This area isn't available for this account.",
                            onSignOut = ::signOut,
                        )
                    } else {
                        Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                when (page) {
                                    FinalAppPage.THYNK -> Chat()
                                    FinalAppPage.CREATE -> CreateStudio()
                                    FinalAppPage.DMS -> Dms()
                                    FinalAppPage.PROFILE -> More(
                                        profile = session?.let {
                                            Profile(
                                                displayName = it.username,
                                                username = it.username,
                                                email = it.maskedEmail,
                                            )
                                        },
                                        emailVerified = session?.emailVerified == true,
                                        ownerAccessChecked = true,
                                        canViewOwnerProfile = false,
                                        canViewOwnerTools = false,
                                        openOwnerProfile = {},
                                        openOwnerTools = {},
                                        signOut = ::signOut,
                                    )
                                    else -> Unit
                                }
                            }
                            FinalPrimaryNavigationBar(
                                selected = selectedDestination,
                                onNavigate = ::navigate,
                            )
                        }
                    }
                }

                FinalAppPage.PROTECTED -> FinalProtectedAccessState(
                    message = "This area isn't available until secure account access is verified.",
                    onSignOut = ::signOut,
                )
            }
        }
    }
}

@Composable
private fun FinalProtectedAccessState(
    message: String,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(FinalCharcoal).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Protected access",
            color = FinalWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )
        Text(
            text = message,
            color = FinalWhite,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
        TextButton(
            onClick = onSignOut,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Sign out", color = FinalWhite)
        }
    }
}

private fun FinalHomeDestination.toPage(): FinalAppPage = when (this) {
    FinalHomeDestination.HOME -> FinalAppPage.HOME
    FinalHomeDestination.THYNK -> FinalAppPage.THYNK
    FinalHomeDestination.CREATE -> FinalAppPage.CREATE
    FinalHomeDestination.PATSY_DMS -> FinalAppPage.DMS
    FinalHomeDestination.PROFILE -> FinalAppPage.PROFILE
}

private fun FinalAppPage.toDestination(): FinalHomeDestination = when (this) {
    FinalAppPage.HOME -> FinalHomeDestination.HOME
    FinalAppPage.THYNK -> FinalHomeDestination.THYNK
    FinalAppPage.CREATE -> FinalHomeDestination.CREATE
    FinalAppPage.DMS -> FinalHomeDestination.PATSY_DMS
    FinalAppPage.PROFILE -> FinalHomeDestination.PROFILE
    FinalAppPage.LOGIN,
    FinalAppPage.DEBUG_SET_PASSWORD,
    FinalAppPage.PROTECTED -> FinalHomeDestination.HOME
}

private fun ShellDestination.toFinalPage(): FinalAppPage? = when (this) {
    ShellDestination.HOME_FEED -> FinalAppPage.HOME
    ShellDestination.THYNK -> FinalAppPage.THYNK
    ShellDestination.CREATE -> FinalAppPage.CREATE
    ShellDestination.DMS -> FinalAppPage.DMS
    ShellDestination.PROFILE -> FinalAppPage.PROFILE
    else -> null
}
