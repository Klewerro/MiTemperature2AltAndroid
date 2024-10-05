plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.klewerro.mitemperature2alt"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "com.klewerro.mitemperature2alt"
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.klewerro.mitemperature2alt.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        )
    }
}

dependencies {

    // Module imports
    implementation(project(":temperatureSensor"))
    implementation(project(":persistence"))
    implementation(project(":domain"))
    implementation(project(":coreUi"))
    implementation(project(":addThermometer:addThermometerPresentation"))
    implementation(project(":thermometerDetails"))
    testImplementation(project(":coreTest"))
    androidTestImplementation(project(":coreTest"))

    implementation(libs.bundles.androidX)
    implementation(libs.bundles.compose)
    implementation(libs.contraintLayout)
    implementation(libs.revealswipe)
    implementation(libs.timber)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.android.compiler)

    // Test dependencies
    // Test
    testImplementation(libs.junit)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)
    testImplementation(libs.assertK)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)

    // UI test
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.compose.junit)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.assertK)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.rules)

    // Debug dependencies
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.manifest)
    debugImplementation(libs.leakCanary)
}
