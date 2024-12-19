package com.s2i.data.local.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences
import java.text.SimpleDateFormat
import java.util.*

class SessionManager(context: Context) {
    private var pref: SharedPreferences = createPreferences(context)
    private var editor: SharedPreferences.Editor = pref.edit()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    // Properti isLoggedOut disimpan di SharedPreferences
    var isLoggedOut: Boolean
        get() = pref.getBoolean(KEY_IS_LOGGED_OUT, false)
        set(value) {
            editor.putBoolean(KEY_IS_LOGGED_OUT, value).apply()
        }

    private fun createPreferences(context: Context): SharedPreferences {
        return try {
                val spec = KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                    .build()
                val masterKey = MasterKey.Builder(context)
                    .setKeyGenParameterSpec(spec)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    "Session",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
        }catch (e: Exception) {
            Log.e("SessionManager", "Failed to initialize EncryptedSharedPreferences, falling back to regular SharedPreferences", e)
            context.getSharedPreferences("Session", Context.MODE_PRIVATE).also {
                it.edit().clear().apply() // Clear corrupted or unusable preferences
            }
        }
    }
    // Check if user is logged in
    val isLogin: Boolean
        get() = !isLoggedOut && pref.getBoolean(KEY_LOGIN, false)


    // Create session with access token, refresh token and username
    fun createLoginSession(
        accessToken: String, refreshToken: String,
        accessTokenExpiry: String, refreshTokenExpiry: String,
        username: String
    ) {
        editor.putBoolean(KEY_LOGIN, true)
            .putBoolean(KEY_IS_LOGGED_OUT, false)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_ACCESS_TOKEN_EXPIRY, accessTokenExpiry)
            .putString(KEY_REFRESH_TOKEN_EXPIRY, refreshTokenExpiry)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    // create device token and devices info
    fun createDeviceToken(
        devicesId: String,
        deviceToken: String, deviceBrand: String,
        deviceModel: String, osType: String,
        sdkVersion: String, devicePlatform: String,
//        deviceVersion: String
    ) {
        editor.putString(KEY_DEVICE_ID, devicesId)
            .putString(KEY_DEVICE_TOKEN, deviceToken)
            .putString(KEY_DEVICE_BRAND, deviceBrand)
            .putString(KEY_DEVICE_MODEL, deviceModel)
            .putString(KEY_DEVICE_OS, osType)
            .putString(KEY_DEVICE_SDK, sdkVersion)
//            .putString(KEY_DEVICE_VERSION, deviceVersion)
            .putString(KEY_DEVICE_PLATFORM, devicePlatform)
            .apply()

        Log.d("SessionManager", "Device ID saved: $devicesId")
        Log.d("SessionManager", "Stored deviceToken: $deviceToken")
    }

    // Check if the access token is expired
    // Check if the access token is expired
    fun isAccessTokenExpired(): Boolean {
        val expiry = accessTokenExpiry
        return expiry != null && isExpired(expiry)
    }

    // Check if the refresh token is expired
    fun isRefreshTokenExpired(): Boolean {
        val expiry = refreshTokenExpiry
        return expiry != null && isExpired(expiry)
    }

    private fun isExpired(expiryDate: String): Boolean {
        return try {
            val expiry = dateFormat.parse(expiryDate)
            expiry != null && Date().after(expiry)
        } catch (e: Exception) {
            Log.e("SessionManager", "Error parsing date: $expiryDate", e)
            true // Assume expired if parsing fails
        }
    }

    // Update the access token in the session
    fun updateAccessToken(newAccessToken: String, newExpiry: String) {
        editor.putString(KEY_ACCESS_TOKEN, newAccessToken)
            .putString(KEY_ACCESS_TOKEN_EXPIRY, newExpiry)
            .apply()
        Log.d("SessionManager", "Updated AccessToken and Expiry.")
    }

    // Logout user by clearing the session data
    fun logout() {
            editor.remove(KEY_LOGIN)
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_ACCESS_TOKEN_EXPIRY)
                .remove(KEY_REFRESH_TOKEN_EXPIRY)
                .remove(KEY_DEVICE_ID)
                .remove(KEY_DEVICE_TOKEN)
                .remove(KEY_DEVICE_BRAND)
                .remove(KEY_DEVICE_MODEL)
                .remove(KEY_DEVICE_OS)
                .remove(KEY_DEVICE_SDK)
//                .remove(KEY_DEVICE_VERSION)
                .remove(KEY_DEVICE_PLATFORM)
                .putBoolean(KEY_LOGIN, false)
                .putBoolean(KEY_IS_LOGGED_OUT, true)
                .clear()
                .apply()
        Log.d("SessionManager", "User logged out successfully. Session data cleared.")
    }


    // Save additional key-value pairs in preferences
    fun saveToPreference(key: String, value: String) = editor.putString(key, value).commit()

    // Retrieve values from preferences
    fun getFromPreference(key: String) = pref.getString(key, "")

    // Access Token and Refresh Token Getters
    val accessToken: String?
        get() = pref.getString(KEY_ACCESS_TOKEN, null)

    val refreshToken: String?
        get() = pref.getString(KEY_REFRESH_TOKEN, null)

    // Expiry times for both tokens

    private val accessTokenExpiry: String?
        get() {
            val expiry = pref.getString(KEY_ACCESS_TOKEN_EXPIRY, null)
            Log.d("SessionManager", "Retrieved accessTokenExpiry: $expiry")
            return expiry
        }

    private val refreshTokenExpiry: String?
        get() {
            val expiry = pref.getString(KEY_REFRESH_TOKEN_EXPIRY, null)
            Log.d("SessionManager", "Retrieved refreshTokenExpiry: $expiry")
            return expiry
        }

    companion object {
        private const val KEY_IS_LOGGED_OUT = "isLoggedOut"
        const val KEY_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCESS_TOKEN_EXPIRY = "access_token_expiry"
        const val KEY_REFRESH_TOKEN_EXPIRY = "refresh_token_expiry"
        const val KEY_DEVICE_BRAND = "brand"
        const val KEY_DEVICE_MODEL = "model"
        const val KEY_DEVICE_OS = "os"
//        const val KEY_DEVICE_VERSION = "version"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_DEVICE_PLATFORM = "device_name"
        const val KEY_DEVICE_SDK = "sdk_version"
        const val KEY_DEVICE_TOKEN = "firebase_token"
    }

    // Check if the user is logged in by checking if the access token exists
    fun isUserLogin(): Boolean {
        return !accessToken.isNullOrEmpty()
    }
}
