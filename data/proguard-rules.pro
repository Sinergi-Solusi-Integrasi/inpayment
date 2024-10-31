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

# Keep classes related to Retrofit services, repositories, and session management
-keep class com.s2i.data.local.auth.SessionManager { *; }
-keep class com.s2i.data.remote.client.ApiServices { *; }
-keep class com.s2i.data.repository.auth.AuthRepositoryImpl { *; }

# Keep Retrofit model classes
-keep class com.s2i.data.model.auth.AuthData { *; }
-keep class com.s2i.data.model.users.UsersData { *; }

# Keep Retrofit annotations
-keepattributes *Annotation*

# Keep Gson annotations
-keep class com.google.gson.annotations.SerializedName { *; }

# Suppress warnings for specific classes
-dontwarn com.s2i.domain.entity.model.auth.AuthModel
-dontwarn com.s2i.domain.entity.model.users.UsersModel
-dontwarn java.lang.invoke.StringConcatFactory
