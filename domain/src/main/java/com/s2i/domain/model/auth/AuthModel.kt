package com.s2i.domain.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel (
    @SerializedName("name")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("access_token_expired_at")
    val expiredAccess: String,
    @SerializedName("refresh_token_expired_at")
    val expiredRefresh: String


)