import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

// Baca local.properties dari root project
val localProperties = Properties()  // Menggunakan import java.util.Properties
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Ambil QRIS_URL dan BASE_URL dari local.properties
val qrisUrl = localProperties["QRIS_URL"] ?: "https://default.qris.url/"
val baseUrl = localProperties["BASE_URL"] ?: "https://default.base.url/"
val mid = localProperties["MID"] ?: "default_mid"
val tid = localProperties["TID"] ?: "default_tid"
val clientId = localProperties["CLIENT_ID"] ?: "default_client_id"
val clientSecret = localProperties["CLIENT_SECRETE"] ?: "default_client_secrete"
android {
    namespace = "com.s2i.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://inpayment.app-intracs.co.id/\"")
        buildConfigField("String", "QRIS_URL", "\"$qrisUrl\"")
        buildConfigField("String", "MID", "\"$mid\"")
        buildConfigField("String", "TID", "\"$tid\"")
        buildConfigField("String", "CLIENT_ID", "\"$clientId\"")
        buildConfigField("String", "CLIENT_SECRETE", "\"$clientSecret\"")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
//        debug {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }

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

    // Koin dependencies
    implementation("io.insert-koin:koin-core:4.0.0")

    // Room for database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(project(":common"))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    ksp(libs.androidx.room.compiler)  // KSP for Room annotation processing
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.ktx)

    implementation("androidx.compose.runtime:runtime")
//    implementation("androidx.compose.runtime:runtime-livedata:1.7.4")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Retrofit for network requests
    implementation(libs.retrofit)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)  // Gson Converter for Retrofit

    implementation(libs.kotlin.stdlib)


    // DataStore for local data storage
    implementation(libs.androidx.datastore.preferences)

    // Gson for JSON parsing
    implementation(libs.gson)

    // Kotlinx Serialization for JSON serialization
    implementation(libs.kotlinx.serialization.json)

    //module domain
    implementation(project(":domain"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.secure.preferences.lib)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

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
    testImplementation ("org.robolectric:robolectric:4.10")
    implementation ("com.jakewharton.timber:timber:5.0.1")
}
tasks.withType<Test> {
    useJUnitPlatform()
}
