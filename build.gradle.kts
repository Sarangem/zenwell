@file:Suppress("unused")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.android.ksp) apply false
}
val defaultVersionCode by extra(0.1)
val defaultVersionName by extra("0.1")
