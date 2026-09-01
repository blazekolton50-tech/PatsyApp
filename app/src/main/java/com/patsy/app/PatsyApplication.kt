package com.patsy.app

import android.app.Application
import com.patsy.app.account.ServerAccountBootstrapService
import com.patsy.app.account.SupabaseAccountBootstrapTransport
import com.patsy.app.auth.EncryptedAuthSessionStore
import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.SupabaseAuthGateway
import com.patsy.app.auth.SupabaseHttpAuthTransport
import com.patsy.app.auth.SupabaseHttpRecoveryTransport
import com.patsy.app.auth.SupabaseHttpRegistrationTransport
import com.patsy.app.dms.DmServiceBindings
import com.patsy.app.dms.ServerDmDataService
import com.patsy.app.dms.SupabaseDmDataTransport
import com.patsy.app.profile.ProfileServiceBindings
import com.patsy.app.profile.ServerProfileDataService
import com.patsy.app.profile.SupabaseProfileDataTransport
import com.patsy.app.security.SupabaseOwnerAuthorizationService
import com.patsy.app.security.SupabaseOwnerAuthorizationTransport
import com.patsy.app.storage.ServerStorageDataService
import com.patsy.app.storage.StorageServiceBindings
import com.patsy.app.storage.SupabaseStorageDataTransport

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
        PatsyServiceBindings.accountBootstrapService = ServerAccountBootstrapService(
            sessionStore = sessionStore,
            transport = SupabaseAccountBootstrapTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        PatsyServiceBindings.ownerAuthorizationService = SupabaseOwnerAuthorizationService(
            sessionStore = sessionStore,
            transport = SupabaseOwnerAuthorizationTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        ProfileServiceBindings.profileDataService = ServerProfileDataService(
            sessionStore = sessionStore,
            transport = SupabaseProfileDataTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        DmServiceBindings.dmDataService = ServerDmDataService(
            sessionStore = sessionStore,
            transport = SupabaseDmDataTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
        StorageServiceBindings.storageDataService = ServerStorageDataService(
            sessionStore = sessionStore,
            transport = SupabaseStorageDataTransport(
                baseUrl = BuildConfig.SUPABASE_URL,
                publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
            ),
        )
    }
}
