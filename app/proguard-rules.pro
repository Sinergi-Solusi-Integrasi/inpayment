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

# Keep the AuthRepository and related classes
-keep class com.s2i.domain.repository.auth.AuthRepository { *; }
-keep class com.s2i.data.repository.auth.AuthRepositoryImpl { *; }

# Keep the SessionManager class
-keep class com.s2i.data.local.auth.SessionManager { *; }

# Keep the ApiServices class
-keep class com.s2i.data.remote.client.ApiServices { *; }

# Keep the UseCases
-keep class com.s2i.domain.usecase.auth.LoginUseCase { *; }
-keep class com.s2i.domain.usecase.auth.RegisterUseCase { *; }

# Keep Transaction model
-keep class com.s2i.core.model.transaction.Transaction { *; }

# Keep all ViewModels
-keep class com.s2i.inpayment.ui.viewmodel.** { *; }

# Keep Gson annotations (if you use Gson for serialization)
-keep class com.google.gson.annotations.SerializedName { *; }

# Keep Koin injection classes
-keep class org.koin.** { *; }


# Suppress warnings for specific classes
-dontwarn com.s2i.domain.entity.model.auth.AuthModel
-dontwarn com.s2i.domain.entity.model.users.UsersModel
-dontwarn java.lang.invoke.StringConcatFactory