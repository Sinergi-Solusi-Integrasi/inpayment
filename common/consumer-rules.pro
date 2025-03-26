# Consumer rules untuk modul common

# Keep model yang akan di-serialize
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Jika modul ini expose fungsi ekstensi atau class utility penting
-keep class com.s2i.common.utils.** { *; }

# Jika ada anotasi yang digunakan oleh modul luar
-keepattributes *Annotation*

# Room models & DAO
-keep class com.s2i.common.database.** { *; }

# Untuk mencegah obfuscate logika penting (jika ada class yang dipakai lintas modul/app)
-keep class com.s2i.common.api.** { *; }

