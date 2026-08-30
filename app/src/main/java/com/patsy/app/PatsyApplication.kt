package com.patsy.app

import android.app.Application
import com.patsy.app.auth.EncryptedAuthSessionStore
import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.SupabaseAuthGateway
import com.patsy.app.auth.SupabaseHttpAuthTransport
import com.patsy.app.auth.SupabaseHttpRecoveryTransport
import com.patsy.app.auth.SupabaseHttpRegistrationTransport

class PatsyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PatsyServiceBindings.authGateway = SupabaseAuthGateway(
            transport = SupabaseHttpAuthTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
            sessionStore = EncryptedAuthSessionStore(this),
            registrationTransport = SupabaseHttpRegistrationTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
            recoveryTransport = SupabaseHttpRecoveryTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
    }
}
