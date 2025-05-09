import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleDevtoolsKsp)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

val localProperties = Properties()  // Menggunakan import java.util.Properties
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val keystoreFile = localProperties["RELEASE_STORE_FILE"]?.toString()?.let { rootProject.file(it) }
val keystoreAlias = localProperties["RELEASE_KEY_ALIAS"]?.toString() ?: ""
val keystorePassword = localProperties["RELEASE_STORE_PASSWORD"]?.toString() ?: ""
val keyPasswords = localProperties["RELEASE_KEY_PASSWORD"]?.toString() ?: ""

android {
    namespace = "com.s2i.inpayment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.s2i.inpayment"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release"){
            storeFile = keystoreFile
            storePassword = keystorePassword
            keyAlias = keystoreAlias
            keyPassword = keyPasswords
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
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

    // koin annotation
    implementation("io.insert-koin:koin-annotations:2.0.0-Beta2")
    implementation(libs.material)
    ksp("io.insert-koin:koin-ksp-compiler:2.0.0-Beta2")

    //qrcode
    implementation("com.google.zxing:core:3.5.2")

    //dialog
    val dialogVersion = "1.3.0"
    implementation("com.maxkeppeler.sheets-compose-dialogs:state:1.3.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:core:${dialogVersion}")
    // CALENDAR
    implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:${dialogVersion}")

    // CLOCK
    implementation("com.maxkeppeler.sheets-compose-dialogs:clock:${dialogVersion}")

    // Shimmer
    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.2")


    // DATE TIME
    implementation("com.maxkeppeler.sheets-compose-dialogs:date-time:1.3.0")

    // DURATION
    implementation("com.maxkeppeler.sheets-compose-dialogs:duration:${dialogVersion}")

    //accompanist
    implementation(libs.accompanist.permissions)

    // CameraX core library using the camera2 implementation
    val cameraXVersion = "1.5.0-alpha06"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${cameraXVersion}")
    implementation("androidx.camera:camera-camera2:${cameraXVersion}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${cameraXVersion}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${cameraXVersion}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${cameraXVersion}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${cameraXVersion}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${cameraXVersion}")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-messaging-directboot")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    /*Text Recognition */
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
//    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")

    //swiperefresh
    implementation(libs.androidx.swiperefreshlayout)

    //splash screen
    implementation(libs.androidx.core.splashscreen)

    // Optional if you're using Jetpack Compose
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation")
    implementation("io.insert-koin:koin-androidx-compose-navigation")

    implementation("androidx.compose.runtime:runtime:1.7.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")

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

    implementation("androidx.work:work-runtime-ktx:2.10.0")

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

    implementation(libs.glide)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")


    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3.adaptive:adaptive")

    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
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


    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    // Mockito Core & JUnit 5 Extension
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    // Kotlin Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // AndroidX Test (untuk pengujian Android)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("org.robolectric:robolectric:4.10")
    implementation("com.jakewharton.timber:timber:5.0.1")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

