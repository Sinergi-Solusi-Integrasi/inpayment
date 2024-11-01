package com.s2i.data.local.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences

class SessionManager(context: Context) {
    private var pref: SharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
        EncryptedSharedPreferences
            .create(
                context,
                "Session",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    } else {
        SecurePreferences(context, "loveyous2iintracs", "session")
    }

    private var editor: SharedPreferences.Editor = pref.edit()

    // Create session with access token, refresh token and username
    fun createLoginSession(
        accessToken: String, refreshToken: String,
        accessTokenExpiry: String, refreshTokenExpiry: String,
        username: String
    ) {
        editor.putBoolean(KEY_LOGIN, true)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_ACCESS_TOKEN_EXPIRY, accessTokenExpiry)
            .putString(KEY_REFRESH_TOKEN_EXPIRY, refreshTokenExpiry)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    // Logout user by clearing the session data
    fun logout() {
        editor.clear()
        editor.commit()
    }

    // Check if user is logged in
    val isLogin: Boolean
        get() = pref.getBoolean(KEY_LOGIN, false)

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
    val accessTokenExpiry: String?
        get() = pref.getString(KEY_ACCESS_TOKEN_EXPIRY, null)

    val refreshTokenExpiry: String?
        get() = pref.getString(KEY_REFRESH_TOKEN_EXPIRY, null)

    companion object {
        const val KEY_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCESS_TOKEN_EXPIRY = "access_token_expiry"
        const val KEY_REFRESH_TOKEN_EXPIRY = "refresh_token_expiry"
    }

    // Check if the user is logged in by checking if the access token exists
    fun isUserLogin(): Boolean {
        return !accessToken.isNullOrEmpty()
    }
}
