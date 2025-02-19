package com.s2i.data.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.auth.TokenData

data class RefreshTokenResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: TokenData
)
