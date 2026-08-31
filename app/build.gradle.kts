plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android { namespace = "com.patsy.app"; compileSdk = 35
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    defaultConfig { applicationId = "com.patsy.app"; minSdk = 26; targetSdk = 35; versionCode = 338; versionName = "3.3.8-patsy1" }
    buildTypes { debug { applicationIdSuffix = ".debug"; versionNameSuffix = "-debug" } }
}

dependencies {
    // Official Rive Android runtime. The authored patsy_assistant.riv is supplied separately.
    implementation("app.rive:rive-android:11.9.2") {
        // Keep Patsy's verified compileSdk 35 / Compose 1.7 toolchain. These runtime libraries
        // are already provided by the app; Rive's newer transitive matrix targets SDK 36.
        exclude(group = "androidx.compose", module = "compose-bom")
        exclude(group = "androidx.core", module = "core-ktx")
        exclude(group = "androidx.lifecycle", module = "lifecycle-runtime-compose")
        exclude(group = "androidx.lifecycle", module = "lifecycle-runtime-ktx")
    }
    implementation("androidx.startup:startup-runtime:1.2.0")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
