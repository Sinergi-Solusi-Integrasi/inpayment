package com.s2i.data.model.auth

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expired_at")
    val expiredAt: String,
    @SerializedName("username")
    val username: String
)
