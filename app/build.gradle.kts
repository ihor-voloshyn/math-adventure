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
    implementation(project(":core"))
    testImplementation("junit:junit:4.13.2")
}

tasks.register("diagnoseUnitTestConfigurations") {
    doLast {
        listOf(
            "implementation",
            "testImplementation",
            "debugUnitTestImplementation",
            "debugUnitTestCompileClasspath",
            "debugUnitTestRuntimeClasspath"
        ).forEach { name ->
            val configuration = configurations.findByName(name)
            println("CONFIG $name exists=${configuration != null} canBeResolved=${configuration?.isCanBeResolved} canBeConsumed=${configuration?.isCanBeConsumed}")
            if (configuration != null) {
                println("  extendsFrom=${configuration.extendsFrom.map { it.name }}")
                println("  declaredDependencies=${configuration.dependencies.map { it.group + ":" + it.name + ":" + it.version }}")
            }
        }
    }
}
