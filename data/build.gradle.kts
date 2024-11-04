plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.s2i.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://inpayment.app-intracs.co.id/\"")
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
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

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
}