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

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions { jvmTarget = "21" }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}
