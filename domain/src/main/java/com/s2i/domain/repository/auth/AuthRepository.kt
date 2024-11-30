package com.s2i.domain.repository.auth

import android.graphics.Bitmap
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.users.UsersModel

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthModel>
    suspend fun register(
        name: String,
        username: String,
        password: String,
        email: String,
        address: String,
        identityNumber: String,
        mobileNumber: String,
        identityImage: BlobImageModel
    ): Result<UsersModel>
}