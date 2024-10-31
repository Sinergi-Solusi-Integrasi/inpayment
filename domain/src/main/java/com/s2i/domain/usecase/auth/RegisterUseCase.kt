package com.s2i.domain.usecase.auth

import com.s2i.domain.entity.model.users.UsersModel
import com.s2i.domain.repository.auth.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String,username: String, password: String, email: String, mobileNumber: String, identityNumber: String, address: String): Result<UsersModel> {
        return authRepository.register(name, username, password, email, mobileNumber, identityNumber, address)
    }
}