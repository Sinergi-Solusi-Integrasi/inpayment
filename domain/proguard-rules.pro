###############################################
#         PROGUARD RULES FOR :domain          #
###############################################

# ==== Kotlin Metadata & Compose ====
-keepattributes InnerClasses, EnclosingMethod, Signature, SourceFile, LineNumberTable, *Annotation*, KotlinMetadata
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.internal.MainDispatcherLoader

# ==== Jetpack Compose support ====
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-dontwarn androidx.compose.runtime.**

# ==== Coroutines ====
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.coroutines.flow.**

# ==== Entity Models ====
-keep class com.s2i.domain.entity.model.** { *; }

# ==== UseCases ====
-keep class com.s2i.domain.usecase.** { *; }
# Hindari minify nama class di package domain.model
-keeppackagenames com.s2i.domain.entity.model.**

# ==== Repository interfaces ====
-keep interface com.s2i.domain.repository.** { *; }

# ==== Annotation metadata for DI, Gson, serialization, etc ====
-keepattributes *Annotation*

# ==== Fix: Type a.a is defined multiple times (shrink error) ====
-keep class kotlin.Metadata { *; }
-dontwarn java.lang.invoke.StringConcatFactory