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
-dontwarn kotlinx.**
-dontwarn kotlin.Unit
# Application classes that will be serialized/deserialized over Gson
-keep class com.s2i.data.model.** { <fields>; }


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

-keepattributes InnerClasses, EnclosingMethod, Signature, SourceFile, LineNumberTable
-keepattributes KotlinMetadata

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

# Jangan obfuscate Retrofit API
-keep interface * {
    @retrofit2.http.* <methods>;
}


##---------------Begin: proguard configuration for Retrofit ----------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
@retrofit2.http.* <methods>;
}

#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

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


# Keep classes related to Retrofit services, repositories, and session management
-keep class com.s2i.data.** { *; }
-keep class com.s2i.data.local.auth.SessionManager { *; }
-keep class com.s2i.data.remote.** { *; }
-keep class com.s2i.data.remote.client.ApiServices { *; }
-keep class com.s2i.data.remote.client.WalletServices { *; }
-keep class com.s2i.data.repository.** { *; }

# Keep Retrofit model classes
-keep class com.s2i.data.model.auth.AuthData { *; }
-keep class com.s2i.data.model.users.UsersData { *; }

# Model antar modul (domain - entity)
-keep class com.s2i.domain.entity.model.** { *; }
-dontwarn com.s2i.domain.entity.model.**

# Keep Gson annotations
-keep class com.google.gson.annotations.SerializedName { *; }

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# Kotlin + Compose Metadata
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.internal.MainDispatcherLoader
# Hindari minify nama class di package data.model
-keeppackagenames com.s2i.data.model.**
-keeppackagenames com.s2i.data.remote.**


# Suppress warnings for specific classes
-dontwarn com.s2i.domain.entity.model.auth.AuthModel
-dontwarn com.s2i.domain.entity.model.users.UsersModel
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn kotlinx.coroutines.internal.MainDispatcherLoader
-dontwarn com.s2i.data.remote.client.WalletServices


