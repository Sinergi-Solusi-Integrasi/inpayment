# Consumer ProGuard rules for :core module

# Gson deserialization needs to keep model field names
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin Serialization annotations
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep models used by app or modules
-keep class com.s2i.core.model.** { *; }

# Annotation handling
-keepattributes *Annotation*

