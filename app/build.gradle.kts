plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
//    id("com.android.application")
}

android {
    namespace = "com.krishnareddy.voicerails"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.krishnareddy.voicerails"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.firebase:firebase-firestore-ktx:24.4.4")
    implementation ("com.google.firebase:firebase-storage-ktx:20.1.0")
    implementation ("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation ("com.google.firebase:firebase-database:20.0.2")
    implementation ("com.google.firebase:firebase-firestore:24.0.0")
    implementation ("com.google.mlkit:translate:16.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}