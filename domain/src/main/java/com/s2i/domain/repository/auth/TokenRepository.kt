package com.s2i.domain.repository.auth

interface TokenRepository {
    suspend fun refreshAccessToken(): Result<Unit>
    fun isAccessTokenExpired(): Boolean
    fun isRefreshTokenExpired(): Boolean
}