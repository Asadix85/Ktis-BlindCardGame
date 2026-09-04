plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
}

android {
    namespace = "com.example.ktis"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.ktis"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")

    implementation("androidx.activity:activity-compose:1.10.1")

    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.animation:animation:1.7.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")

    testImplementation("junit:junit:4.13.2")
}