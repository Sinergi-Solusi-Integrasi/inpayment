package com.s2i.data.remote.response.auth

import com.s2i.data.model.auth.AuthData

data class LoginResponse(
    val code: Int,
    val message: String,
    val data: AuthData
)
