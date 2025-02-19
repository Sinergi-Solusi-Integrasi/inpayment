package com.s2i.domain.usecase.auth

import com.s2i.domain.repository.auth.TokenRepository

class TokenUseCase(
    private val tokenRepository: TokenRepository
) {
    suspend fun refreshAccessTokenIfNeeded(): Result<Unit> {
        return if (tokenRepository.isAccessTokenExpired()) {
            if (!tokenRepository.isRefreshTokenExpired()) {
                tokenRepository.refreshAccessToken()
            } else {
                Result.failure(Exception("Both access and refresh tokens are expired."))
            }
        } else {
            Result.success(Unit)
        }
    }
}