buildscript {

}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(kotlinx.plugins.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.protubuf) apply false
}