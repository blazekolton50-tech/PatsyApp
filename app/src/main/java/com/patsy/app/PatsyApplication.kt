package com.patsy.app

import android.app.Application
import com.patsy.app.auth.EncryptedAuthSessionStore
import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.SupabaseAuthGateway
import com.patsy.app.auth.SupabaseHttpAuthTransport
import com.patsy.app.auth.SupabaseHttpRecoveryTransport
import com.patsy.app.auth.SupabaseHttpRegistrationTransport
import com.patsy.app.account.ServerAccountBootstrapService
import com.patsy.app.account.SupabaseAccountBootstrapTransport
import com.patsy.app.security.ServerOwnerAuthorizationService
import com.patsy.app.security.SupabaseHttpOwnerAuthorizationTransport

class PatsyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val sessionStore = EncryptedAuthSessionStore(this)
        PatsyServiceBindings.authGateway = SupabaseAuthGateway(
            transport = SupabaseHttpAuthTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
            sessionStore = sessionStore,
            registrationTransport = SupabaseHttpRegistrationTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
            recoveryTransport = SupabaseHttpRecoveryTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        PatsyServiceBindings.ownerAuthorizationService = ServerOwnerAuthorizationService(
            sessionStore = sessionStore,
            transport = SupabaseHttpOwnerAuthorizationTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        PatsyServiceBindings.accountBootstrapService = ServerAccountBootstrapService(
            sessionStore = sessionStore,
            transport = SupabaseAccountBootstrapTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
    }
}
