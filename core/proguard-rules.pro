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
# Keep models used in the core module
# === Keep data classes & shared models ===
-keep class com.s2i.core.model.** { *; }
-keep class com.s2i.core.model.transaction.Transaction { *; }

# ProGuard rules for :core module

# === Kotlin Serialization ===
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# === Gson annotations support ===
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}


# === Jetpack Compose (runtime, livedata, etc.) ===
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**

# === Optional: If you use kotlinx.coroutines in shared logic ===
-dontwarn kotlinx.coroutines.**

# Keep Kotlin Metadata
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes KotlinMetadata

-dontwarn java.lang.invoke.StringConcatFactory
