plugins {
    id("com.android.application")
}

android {
    namespace = "com.mokshasolutions.pdfpicker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mokshasolutions.pdfpicker"
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
//    def camerax_version = "1.2.2"
    implementation ("androidx.camera:camera-core:1.3.1")
    implementation ("androidx.camera:camera-camera2:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-video:1.3.1")

    implementation ("androidx.camera:camera-view:1.3.1")
    implementation ("androidx.camera:camera-extensions:1.3.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
//    implementation(composeBom)
//    androidTestImplementation(composeBom)
//
//    // Choose one of the following:
//    // Material Design 3
//    implementation("androidx.compose.material3:material3")
//    // or Material Design 2
//    implementation("androidx.compose.material:material")
//    // or skip Material Design and build directly on top of foundational components
//    implementation("androidx.compose.foundation:foundation")
//    // or only import the main APIs for the underlying toolkit systems,
//    // such as input and measurement/layout
//    implementation("androidx.compose.ui:ui")
//
//    // Android Studio Preview support
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//
//    // UI Tests
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
//
//    // Optional - Included automatically by material, only add when you need
//    // the icons but not the material library (e.g. when using Material3 or a
//    // custom design system based on Foundation)
//    implementation("androidx.compose.material:material-icons-core")
//    // Optional - Add full set of material icons
//    implementation("androidx.compose.material:material-icons-extended")
//    // Optional - Add window size utils
//    implementation("androidx.compose.material3:material3-window-size-class")
//
//    // Optional - Integration with activities
//    implementation("androidx.activity:activity-compose:1.8.1")
//    // Optional - Integration with ViewModels
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
//    // Optional - Integration with LiveData
//    implementation("androidx.compose.runtime:runtime-livedata")
//    // Optional - Integration with RxJava
//    implementation("androidx.compose.runtime:runtime-rxjava2")
}