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

# Keep Koin modules and injection-related classes
-keep class org.koin.** { *; }

# Add keep rules for any specific model or logic in the common module
-keep class com.s2i.common.** { *; }

# Keep semua model data yang memakai Kotlin serialization / Gson / Room / Retrofit
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @androidx.room.* <methods>;
}

# Keep data class jika kamu pakai Gson atau Room
-keep class com.s2i.common.model.** { *; }

# Keep untuk Kotlin metadata (wajib untuk modul Kotlin)
-keepattributes KotlinMetadata



# Jangan obfuscate model yang dipakai oleh retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Room DAO
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase$Callback
-keep class * implements androidx.room.RoomDatabase$Callback

# Optional: jika Glide digunakan
-keep class com.bumptech.glide.** { *; }
-keep interface com.bumptech.glide.** { *; }

-dontwarn java.lang.invoke.StringConcatFactory

