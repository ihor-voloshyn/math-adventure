plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.ihorvoloshyn.mathadventure"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ihorvoloshyn.mathadventure"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions { jvmTarget = "21" }
}

dependencies {
    // Android's local unit-test Kotlin compilation must inherit JUnit from the app compile classpath.
    // Keep the dependency explicit here while the test-variant wiring is stabilized.
    implementation("junit:junit:4.13.2")
}
