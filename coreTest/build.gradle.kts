plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.klewerro.mitemperature2alt.coreTest"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()

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
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":temperatureSensor"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlin.ble.client)
    api(libs.timber)

    // Test
    implementation(libs.junit)
    implementation(libs.junit5.api)
    runtimeOnly(libs.junit5.engine)
    implementation(libs.junit5.params)
    implementation(libs.assertK)
    implementation(libs.coroutines.test)
    implementation(libs.mockk)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}
