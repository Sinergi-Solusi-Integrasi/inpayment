package com.s2i.domain.entity.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel (

    val name: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiredAt: String,
    val refreshTokenExpiredAt: String

)