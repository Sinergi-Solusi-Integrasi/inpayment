package com.s2i.domain.usecase.auth

import android.graphics.Bitmap
import com.s2i.domain.entity.model.users.UsersModel
import com.s2i.domain.repository.auth.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        username: String,
        password: String,
        email: String,
        mobileNumber: String,
        identityNumber: String,
        address: String,
        identityBitmap: Bitmap,
        imageFormat: Bitmap.CompressFormat
    ): Result<UsersModel> {
        return authRepository.register(
            name,
            username,
            password,
            email,
            mobileNumber,
            identityNumber,
            address,
            identityBitmap,
            imageFormat
        )
    }
}