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
    // Android local-unit-test variant classpaths in this prototype do not inherit the
    // variant test configurations reliably, so keep JUnit on the app compile classpath.
    implementation("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")
}
