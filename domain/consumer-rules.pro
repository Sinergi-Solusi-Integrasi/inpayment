###############################################
#     CONSUMER PROGUARD RULES FOR :domain     #
###############################################

# === Expose entity model untuk serialization, reflection, etc ===
-keep class com.s2i.domain.entity.model.** { *; }

# === Expose usecase untuk modul lain ===
-keep class com.s2i.domain.usecase.** { *; }

# === Expose repository interfaces (akan diimplement di :data) ===
-keep interface com.s2i.domain.repository.** { *; }

# === Preserve annotations and reflection metadata ===
-keepattributes *Annotation*

# === Hindari error R8 terkait Kotlin shrinking ===
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.internal.MainDispatcherLoader
