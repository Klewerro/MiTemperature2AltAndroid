// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.jvm).apply(false)
    alias(libs.plugins.hilt).apply(false)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent
}
