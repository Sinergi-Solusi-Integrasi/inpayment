plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleDevtoolsKsp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.s2i.inpayment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.s2i.inpayment"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.15"
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    //koin
    // Koin dependencies
    implementation(platform("io.insert-koin:koin-bom:4.0.0"))
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-core-viewmodel")

    //accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.5.0-alpha03"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    /*Text Recognition */
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    //swiperefresh
    implementation(libs.androidx.swiperefreshlayout)

    //splash screen
    implementation(libs.androidx.core.splashscreen)

    // Optional if you're using Jetpack Compose
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation")
    implementation("io.insert-koin:koin-androidx-compose-navigation")

    implementation("androidx.compose.runtime:runtime:1.7.4")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // Retrofit with Gson converter

    // Gson for JSON serialization/deserialization
    implementation(libs.gson)

    //module
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core"))
    implementation(project(":common"))

    //material design
    // Choose one of the following:
    // Material Design 3
    implementation(libs.material3)
    // or Material Design 2
    implementation(libs.androidx.material)
    // or skip Material Design and build directly on top of foundational components
    implementation(libs.androidx.foundation)
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation(libs.ui)




    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3.adaptive:adaptive")

    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation(libs.rxjava)
    implementation(libs.rxbinding)
    implementation(libs.androidx.security.crypto.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.runtime.android)
    ksp(libs.androidx.room.compiler)  // KSP for Room annotation processing
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.ktx)

    //retrofit
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)  // Gson Converter for Retrofit

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.transition)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // UI Tests
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}