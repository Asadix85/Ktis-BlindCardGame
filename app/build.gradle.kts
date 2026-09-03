plugins {
    id("com.android.application")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")

    testImplementation("junit:junit:4.13.2")
}