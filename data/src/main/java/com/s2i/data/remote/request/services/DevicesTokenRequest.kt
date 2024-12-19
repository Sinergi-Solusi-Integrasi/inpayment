package com.s2i.data.remote.request.services

import com.google.gson.annotations.SerializedName

data class DevicesTokenRequest(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("os")
    val osType: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("sdk_version")
    val sdkVersion: String,
    @SerializedName("firebase_token")
    val tokenFirebase: String
)
