package com.s2i.data.model.auth

import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("access_token_expired_at")
    val accessTokenExpiredAt: String,
    @SerializedName("refresh_token_expired_at")
    val refreshTokenExpiredAt: String
)
