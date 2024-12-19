package com.s2i.data.model.auth

import com.google.gson.annotations.SerializedName

data class LogoutData(
    @SerializedName("logout_at")
    val logoutAt: String,
)
