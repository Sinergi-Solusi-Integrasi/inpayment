package com.s2i.data.remote.request.auth

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("device_id")
    val deviceId: String ? = null,
)
