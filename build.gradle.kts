// build.gradle (project-level)

buildscript {
    dependencies {
        classpath(libs.google.services) // Uses libs.versions.toml
    }
}

plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.androidApplication) apply false
}