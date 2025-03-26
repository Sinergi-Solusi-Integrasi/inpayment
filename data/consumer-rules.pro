# === CONSUMER RULES FOR :data ===

# Retrofit & Gson models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @retrofit2.http.* <methods>;
}

# Room DB and DAO
-keep class com.s2i.data.local.db.** { *; }

# Kotlin Serialization support
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep remote interfaces if used in DI
-keep interface com.s2i.data.remote.** { *; }

# Koin injection setup
-keep class org.koin.** { *; }

# Keep annotations
-keepattributes *Annotation*
