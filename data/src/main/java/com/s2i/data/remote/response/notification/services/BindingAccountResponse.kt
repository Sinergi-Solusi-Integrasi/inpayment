package com.s2i.data.remote.response.notification.services

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.notifications.services.BindingAccountData

data class BindingAccountResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: BindingAccountData
)
