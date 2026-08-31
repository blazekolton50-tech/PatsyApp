package com.patsy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.debug.DebugTestAccessFactory
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
        setContent { FinalPatsyApp() }
    }
}

private enum class FinalAppPage { LOGIN, DEBUG_SET_PASSWORD, HOME, THYNK, CREATE, DMS, PROFILE }

@Composable
private fun FinalPatsyApp() {
    val context = LocalContext.current.applicationContext
    val authGateway = remember { PatsyServiceBindings.authGateway }
    val debugTestAccess = remember(context) { DebugTestAccessFactory.create(context) }
    var page by remember { mutableStateOf(FinalAppPage.LOGIN) }
    var session by remember { mutableStateOf<PublicSession?>(null) }
    var pendingKeepSignedIn by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authGateway, debugTestAccess) {
        val debugSession = debugTestAccess.restoreSessionIfRemembered()
        if (debugSession != null) {
            session = debugSession
            page = FinalAppPage.HOME
            return@LaunchedEffect
        }
        when (val restored = authGateway.restoreSession()) {
            is SessionState.Authenticated -> {
                session = restored.session
                page = FinalAppPage.HOME
            }
            SessionState.Anonymous,
            is SessionState.Expired,
            is SessionState.Unavailable -> Unit
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
                    onAuthenticated = {
                        session = it
                        page = FinalAppPage.HOME
                    },
                    onNeedDebugPasswordSetup = { keepSignedIn ->
                        pendingKeepSignedIn = keepSignedIn
                        page = FinalAppPage.DEBUG_SET_PASSWORD
                    },
                )

                FinalAppPage.DEBUG_SET_PASSWORD -> FinalDebugSetPasswordRoute(
                    keepSignedIn = pendingKeepSignedIn,
                    debugTestAccess = debugTestAccess,
                    onAuthenticated = {
                        session = it
                        page = FinalAppPage.HOME
                    },
                    onBack = { page = FinalAppPage.LOGIN },
                )

                FinalAppPage.HOME -> FinalHomeScreen(
                    onNavigate = { destination -> page = destination.toPage() },
                )

                FinalAppPage.THYNK,
                FinalAppPage.CREATE,
                FinalAppPage.DMS,
                FinalAppPage.PROFILE -> {
                    val selectedDestination = page.toDestination()
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
                                    signOut = {
                                        scope.launch {
                                            debugTestAccess.logout()
                                            authGateway.signOut()
                                            session = null
                                            page = FinalAppPage.LOGIN
                                        }
                                    },
                                )
                                else -> Unit
                            }
                        }
                        FinalPrimaryNavigationBar(
                            selected = selectedDestination,
                            onNavigate = { page = it.toPage() },
                        )
                    }
                }
            }
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
    FinalAppPage.DEBUG_SET_PASSWORD -> FinalHomeDestination.HOME
}
