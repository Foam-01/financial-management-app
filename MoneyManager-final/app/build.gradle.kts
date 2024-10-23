

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.moneymanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moneymanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation ("androidx.room:room-runtime:2.4.2")
    annotationProcessor ("androidx.room:room-compiler:2.4.2")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("androidx.fragment:fragment:1.6.0")
    implementation ("androidx.core:core-ktx:1.6.0")


}