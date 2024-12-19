package com.s2i.data.model.notifications.services

import com.google.gson.annotations.SerializedName

data class BindingAccountData(
    @SerializedName("devices_id")
    val devicesId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("binded_at")
    val bindingAt: String
)
