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
import com.patsy.app.security.FailClosedOwnerAuthorizationGate
import com.patsy.app.security.FinalOwnerAccessPolicy
import com.patsy.app.security.OwnerAuthorizationDecision
import com.patsy.app.security.OwnerCapability
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalDebugSetPasswordRoute
import com.patsy.app.ui.finaldesign.FinalHomeDestination
import com.patsy.app.ui.finaldesign.FinalHomeScreen
import com.patsy.app.ui.finaldesign.FinalLoginRoute
import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar
import com.patsy.app.ui.finaldesign.FinalWhite
import kotlinx.coroutines.delay
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
    OWNER_PROFILE,
    OWNER_TOOLS,
    PROTECTED,
}

@Composable
private fun FinalPatsyApp(initialDeepLink: String?) {
    val context = LocalContext.current.applicationContext
    val authGateway = remember { PatsyServiceBindings.authGateway }
    val accountBootstrapService = remember { PatsyServiceBindings.accountBootstrapService }
    val ownerGate = remember {
        FailClosedOwnerAuthorizationGate(PatsyServiceBindings.ownerAuthorizationService)
    }
    val debugTestAccess = remember(context) { DebugTestAccessFactory.create(context) }
    val rememberMeStore = remember(context) { DataStoreRememberMePreferenceStore(context) }
    val rememberMeCoordinator = remember(rememberMeStore) { RememberMeCoordinator(rememberMeStore) }
    var page by remember { mutableStateOf(FinalAppPage.LOGIN) }
    var session by remember { mutableStateOf<PublicSession?>(null) }
    var bootstrapResult by remember { mutableStateOf<AccountBootstrapResult?>(null) }
    var bootstrapLoading by remember { mutableStateOf(false) }
    var debugPreview by remember { mutableStateOf(false) }
    var pendingKeepSignedIn by remember { mutableStateOf(true) }
    var ownerProfileGrant by remember { mutableStateOf<OwnerAuthorizationDecision.Allowed?>(null) }
    var ownerToolsGrant by remember { mutableStateOf<OwnerAuthorizationDecision.Allowed?>(null) }
    var ownerAccessChecked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun clearOwnerAccess(checked: Boolean) {
        ownerProfileGrant = null
        ownerToolsGrant = null
        ownerAccessChecked = checked
    }

    suspend fun refreshOwnerAccess(): Pair<OwnerAuthorizationDecision.Allowed?, OwnerAuthorizationDecision.Allowed?> {
        val activeSession = session
        if (activeSession == null || debugPreview) {
            clearOwnerAccess(checked = true)
            return null to null
        }

        ownerAccessChecked = false
        val profileGrant = (ownerGate.verify(activeSession, OwnerCapability.VIEW_OWNER_PROFILE)
            as? OwnerAuthorizationDecision.Allowed)
            ?.takeIf {
                FinalOwnerAccessPolicy.canOpen(it, OwnerCapability.VIEW_OWNER_PROFILE)
            }
        val toolsGrant = (ownerGate.verify(activeSession, OwnerCapability.VIEW_OWNER_TOOLS)
            as? OwnerAuthorizationDecision.Allowed)
            ?.takeIf {
                FinalOwnerAccessPolicy.canOpen(it, OwnerCapability.VIEW_OWNER_TOOLS)
            }

        if (session?.sessionId == activeSession.sessionId && !debugPreview) {
            ownerProfileGrant = profileGrant
            ownerToolsGrant = toolsGrant
            ownerAccessChecked = true
            return profileGrant to toolsGrant
        }

        clearOwnerAccess(checked = true)
        return null to null
    }

    fun acceptSession(authenticated: PublicSession) {
        val isDebugPreview = BuildConfig.DEBUG &&
            authenticated.sessionId.startsWith("debug-") &&
            authenticated.userId.startsWith("debug-")
        session = authenticated
        debugPreview = isDebugPreview
        bootstrapResult = null
        bootstrapLoading = !isDebugPreview
        clearOwnerAccess(checked = isDebugPreview)
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
            clearOwnerAccess(checked = false)
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
            clearOwnerAccess(checked = false)
            return@LaunchedEffect
        }
        if (debugPreview) {
            bootstrapResult = null
            bootstrapLoading = false
            clearOwnerAccess(checked = true)
            return@LaunchedEffect
        }

        bootstrapLoading = true
        val fetched = accountBootstrapService.fetch(activeSession)
        if (session?.sessionId == activeSession.sessionId && !debugPreview) {
            bootstrapResult = fetched
            bootstrapLoading = false
        }
    }

    LaunchedEffect(session?.sessionId, bootstrapResult, debugPreview) {
        if (debugPreview) {
            clearOwnerAccess(checked = true)
            return@LaunchedEffect
        }

        val account = (bootstrapResult as? AccountBootstrapResult.Available)?.account
        if (account == null) {
            clearOwnerAccess(checked = bootstrapResult != null)
            return@LaunchedEffect
        }

        val mayViewOwnerProfile = OwnerCapability.VIEW_OWNER_PROFILE in account.capabilities
        val mayViewOwnerTools = OwnerCapability.VIEW_OWNER_TOOLS in account.capabilities
        if (!mayViewOwnerProfile && !mayViewOwnerTools) {
            clearOwnerAccess(checked = true)
            return@LaunchedEffect
        }

        refreshOwnerAccess()
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

    LaunchedEffect(page, ownerProfileGrant?.authorizationId, ownerToolsGrant?.authorizationId) {
        val activeGrant = when (page) {
            FinalAppPage.OWNER_PROFILE -> ownerProfileGrant
            FinalAppPage.OWNER_TOOLS -> ownerToolsGrant
            else -> null
        } ?: return@LaunchedEffect
        val waitMillis = activeGrant.expiresAtEpochMillis - System.currentTimeMillis()
        if (waitMillis > 0L) delay(waitMillis)
        val stillCurrent = when (page) {
            FinalAppPage.OWNER_PROFILE -> FinalOwnerAccessPolicy.canOpen(
                ownerProfileGrant,
                OwnerCapability.VIEW_OWNER_PROFILE,
            )
            FinalAppPage.OWNER_TOOLS -> FinalOwnerAccessPolicy.canOpen(
                ownerToolsGrant,
                OwnerCapability.VIEW_OWNER_TOOLS,
            )
            else -> true
        }
        if (!stillCurrent) page = FinalAppPage.PROFILE
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
                                        ownerAccessChecked = ownerAccessChecked,
                                        canViewOwnerProfile = FinalOwnerAccessPolicy.canOpen(
                                            ownerProfileGrant,
                                            OwnerCapability.VIEW_OWNER_PROFILE,
                                        ),
                                        canViewOwnerTools = FinalOwnerAccessPolicy.canOpen(
                                            ownerToolsGrant,
                                            OwnerCapability.VIEW_OWNER_TOOLS,
                                        ),
                                        openOwnerProfile = {
                                            scope.launch {
                                                val refreshed = refreshOwnerAccess().first
                                                if (
                                                    FinalOwnerAccessPolicy.canOpen(
                                                        refreshed,
                                                        OwnerCapability.VIEW_OWNER_PROFILE,
                                                    )
                                                ) {
                                                    page = FinalAppPage.OWNER_PROFILE
                                                }
                                            }
                                        },
                                        openOwnerTools = {
                                            scope.launch {
                                                val refreshed = refreshOwnerAccess().second
                                                if (
                                                    FinalOwnerAccessPolicy.canOpen(
                                                        refreshed,
                                                        OwnerCapability.VIEW_OWNER_TOOLS,
                                                    )
                                                ) {
                                                    page = FinalAppPage.OWNER_TOOLS
                                                }
                                            }
                                        },
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

                FinalAppPage.OWNER_PROFILE -> {
                    if (
                        FinalOwnerAccessPolicy.canOpen(
                            ownerProfileGrant,
                            OwnerCapability.VIEW_OWNER_PROFILE,
                        )
                    ) {
                        OwnerProfile(
                            profile = session?.let {
                                Profile(
                                    displayName = it.username,
                                    username = it.username,
                                    email = it.maskedEmail,
                                )
                            },
                            back = { page = FinalAppPage.PROFILE },
                        )
                    } else {
                        OwnerAccessDenied { page = FinalAppPage.PROFILE }
                    }
                }

                FinalAppPage.OWNER_TOOLS -> {
                    if (
                        FinalOwnerAccessPolicy.canOpen(
                            ownerToolsGrant,
                            OwnerCapability.VIEW_OWNER_TOOLS,
                        )
                    ) {
                        OwnerTools { page = FinalAppPage.PROFILE }
                    } else {
                        OwnerAccessDenied { page = FinalAppPage.PROFILE }
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
    FinalAppPage.PROFILE,
    FinalAppPage.OWNER_PROFILE,
    FinalAppPage.OWNER_TOOLS -> FinalHomeDestination.PROFILE
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
