package com.s2i.domain.usecase.auth

import android.graphics.Bitmap
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.users.UsersModel
import com.s2i.domain.repository.auth.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        username: String,
        password: String,
        email: String,
        mobileNumber: String,
        address: String,
        identityNumber: String,
        identityImage: BlobImageModel,
    ): Result<UsersModel> {
        return authRepository.register(
            name,
            username,
            password,
            email,
            mobileNumber,
            address,
            identityNumber,
            identityImage
        )
    }
}