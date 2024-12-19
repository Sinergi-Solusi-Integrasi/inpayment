package com.s2i.data.model.notifications.services

import com.google.gson.annotations.SerializedName

data class RegisterDevicesData(
    @SerializedName("device_id")
    val devicesId: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("os")
    val osType: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("sdk_version")
    val sdkVersion: String,
    @SerializedName("created_at")
    val createdAt: String,

)
