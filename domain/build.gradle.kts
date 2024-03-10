plugins {
    id("java-library")
    alias(libs.plugins.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.coroutines.core)
}
