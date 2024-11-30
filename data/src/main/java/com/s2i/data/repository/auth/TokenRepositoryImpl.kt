//    package com.s2i.data.repository.auth
//
//    import android.util.Log
//    import com.s2i.data.local.auth.SessionManager
//    import com.s2i.data.remote.client.ApiServices
//    import com.s2i.domain.repository.auth.TokenRepository
//
//    class TokenRepositoryImpl(
//        private val apiServices: ApiServices,
//        private val sessionManager: SessionManager
//    ) : TokenRepository {
//
//        override suspend fun refreshAccessToken(): Result<Unit> {
//            return try {
//                if (sessionManager.isLoggedOut) {
//                    Log.d("TokenRepository", "Logout already triggered. Skipping refresh.")
//                    return Result.failure(Exception("User already logged out"))
//                }
//
//                val refreshToken = sessionManager.refreshToken
//                if (refreshToken.isNullOrEmpty()) {
//                    sessionManager.logout()
//                    return Result.failure(Exception("Refresh token is null or empty"))
//                }
//
//                val response = apiServices.refreshAccessToken(mapOf("refresh_token" to refreshToken))
//                if (response.code == 0) {
//                    val newTokenData = response.data
//                    sessionManager.updateAccessToken(newTokenData.accessToken, newTokenData.expiredAt)
//                    Result.success(Unit)
//                } else {
//                    sessionManager.logout()
//                    Result.failure(Exception("Failed to refresh access token"))
//                }
//            } catch (e: Exception) {
//                sessionManager.logout()
//                Result.failure(e)
//            }
//        }
//
//
//        override fun isAccessTokenExpired(): Boolean = sessionManager.isAccessTokenExpired()
//        override fun isRefreshTokenExpired(): Boolean = sessionManager.isRefreshTokenExpired()
//    }
