import com.google.devtools.ksp.gradle.model.Ksp
import org.jetbrains.kotlin.gradle.model.Kapt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    //hilt
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.frontendapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.frontendapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Básicas
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation(libs.firebase.auth)

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // Maps
    implementation("com.google.maps.android:maps-compose:3.1.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Retrofit & JSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Google Identity SDK: Sign-in with Google via Android Credential Manager
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Optional: Jetpack Credentials for federated sign-in
    implementation("androidx.credentials:credentials-play-services-auth:1.0.0-alpha01")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Hilt
//    implementation("com.google.dagger:hilt-android:2.56.2")
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//    ksp("com.google.dagger:hilt-compiler:2.56.2")
}