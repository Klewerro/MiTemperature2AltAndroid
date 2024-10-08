plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.klewerro.mitemperature2alt.thermometerDetails"
    compileSdk =
        libs.versions.sdk.compile
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.sdk.min
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(project(":core"))
    implementation(project(":coreUi"))
    implementation(project(":domain"))

    implementation(libs.bundles.androidX)
    implementation(libs.bundles.compose)
    implementation(libs.timber)
    implementation(libs.androidx.material3.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.android.compiler)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)
    testImplementation(libs.assertK)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)

    // Debug dependencies
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.manifest)
}
