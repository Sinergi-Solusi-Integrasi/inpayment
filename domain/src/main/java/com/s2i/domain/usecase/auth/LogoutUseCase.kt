package com.s2i.domain.usecase.auth

import com.s2i.domain.entity.model.auth.AuthLogoutModel
import com.s2i.domain.repository.auth.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(deviceId: String? = null): AuthLogoutModel {
        return authRepository.logout(deviceId)
    }
}