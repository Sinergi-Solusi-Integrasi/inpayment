package com.s2i.data.remote.response.notification.balance

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.balance.HistoryBalanceData

data class NotificationTrx(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: HistoryBalanceData? = null
)
