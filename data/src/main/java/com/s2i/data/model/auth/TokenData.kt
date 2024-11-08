package com.s2i.data.model.auth

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("username")
    val username: String
)
