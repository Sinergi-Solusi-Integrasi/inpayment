# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##---------------Begin: proguard configuration for Gson ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
@com.google.gson.annotations.SerializedName <fields>;
}


##---------------Begin: proguard configuration for Retrofit ----------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
@retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-dontwarn kotlinx.**


# Existing Rules

#########################################
# ========== BASIC OPTIMIZATION ======== #
#########################################
-keep class com.s2i.** { *; }
# Hilangkan log saat release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep Source Debug Info (optional untuk debug purposes)
# -keepattributes SourceFile,LineNumberTable

#########################################
# ========== KOTLIN / COROUTINES ======= #
#########################################

-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

-dontwarn kotlinx.coroutines.**

#########################################
# ========== JETPACK COMPOSE =========== #
#########################################

-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-dontwarn androidx.compose.**

#########################################
# ========== RETROFIT ================== #
#########################################

-keepattributes Signature, InnerClasses, EnclosingMethod, Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-dontwarn org.codehaus.mojo.animal_sniffer.*
# Jangan obfuscate Retrofit API
-keep interface * {
    @retrofit2.http.* <methods>;
}

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>


#########################################
# ========== GSON ====================== #
#########################################

-keepattributes Signature, *Annotation*

-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.annotations.SerializedName { *; }

#########################################
# ========== KOIN ====================== #
#########################################

-dontwarn org.koin.**
-keep class org.koin.** { *; }

#########################################
# ========== ROOM ====================== #
#########################################

-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
# Untuk Koin agar tidak strip class module-nya
-keep class org.koin.core.module.Module
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Untuk ViewModel yang di-inject
-keep class * extends androidx.lifecycle.ViewModel { *; }

-keepclassmembers class * {
    @org.koin.core.annotation.* *;
}

-dontwarn androidx.room.**

#########################################
# ========== FIREBASE ================== #
#########################################

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

#########################################
# ========== ML KIT ==================== #
#########################################

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.** { *; }

#########################################
# ========== CAMERA X ================== #
#########################################

-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

#########################################
# ========== TIMBER ==================== #
#########################################

-dontwarn timber.log.**

#########################################
# ========== VIEWMODEL ================= #
#########################################

-keep class * extends androidx.lifecycle.ViewModel { *; }

#########################################
# ========== YOUR MODULES ============== #
#########################################

# Core domain/data layer (bisa spesifik jika perlu)
-keep class com.s2i.core.** { *; }
-keep class com.s2i.domain.** { *; }
-keep class com.s2i.data.** { *; }
-keep class com.s2i.common.** { *; }

# SessionManager, ApiService, UseCases
-keep class com.s2i.domain.** { *; }
-keep class com.s2i.data.local.auth.SessionManager { *; }
-keep class com.s2i.data.remote.client.ApiServices { *; }
# Keep Retrofit API interface WalletServices
-keep class com.s2i.data.remote.client.WalletServices { *; }
-keep class com.s2i.domain.usecase.** { *; }
-keep class com.s2i.data.remote.client.** { *; }
# Keep semua interface Retrofit
-keep interface com.s2i.data.remote.client.** { *; }


# ViewModel UI Layer
-keep class com.s2i.inpayment.ui.viewmodel.** { *; }

#########################################
# ========== THIRD PARTY UI ============ #
#########################################

# Dialog Sheets
-keep class com.maxkeppeler.sheets.** { *; }
-dontwarn com.maxkeppeler.sheets.**

# Accompanist
-dontwarn com.google.accompanist.**

# Coil & Glide
-keep class coil.** { *; }
-keep class com.bumptech.glide.** { *; }
-dontwarn coil.**
-dontwarn com.bumptech.glide.**

#########################################
# ========== SUPPRESS WARNINGS ========= #
#########################################

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn com.s2i.domain.entity.model.**
-dontwarn com.s2i.data.remote.client.WalletServices
