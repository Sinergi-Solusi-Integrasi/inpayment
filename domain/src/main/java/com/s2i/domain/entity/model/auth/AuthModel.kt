package com.s2i.domain.entity.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel (

    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("access_token_expired_at")
    val accessTokenExpiredAt: String,
    @SerializedName("refresh_token_expired_at")
    val refreshTokenExpiredAt: String

)