package com.s2i.data.repository.auth

import android.util.Log
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.repository.auth.TokenRepository

class TokenRepositoryImpl(
    private val apiServices: ApiServices,
    private val sessionManager: SessionManager
) : TokenRepository {

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val refreshToken = sessionManager.refreshToken
            Log.d("ToeknRepository", "Refresh token: $refreshToken")
            if (refreshToken.isNullOrEmpty()) {
                Log.e("TokenRepository", "Refresh token is null or empty.")
                return Result.failure(Exception("Refresh token is null or empty"))
            }

            val response = apiServices.refreshAccessToken(mapOf("refresh_token" to refreshToken))
            if (response.code == 0) {
                val newTokenData = response.data
                sessionManager.updateAccessToken(newTokenData.accessToken, newTokenData.expiresAt)
                Log.d("TokenRepository", "Access token refreshed successfully.")
                Result.success(Unit)
            } else {
                Log.e("TokenRepository", "Failed to refresh access token. Logging out.")
                sessionManager.logout()
                Result.failure(Exception("Failed to refresh access token"))
            }
        } catch (e: Exception) {
            Log.e("TokenRepository", "Error refreshing access token: ${e.message}")
            Result.failure(e)
        }
    }

    override fun isAccessTokenExpired(): Boolean = sessionManager.isAccessTokenExpired()
    override fun isRefreshTokenExpired(): Boolean = sessionManager.isRefreshTokenExpired()
}
