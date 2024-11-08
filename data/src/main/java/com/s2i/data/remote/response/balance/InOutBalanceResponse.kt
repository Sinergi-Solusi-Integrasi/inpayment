package com.s2i.data.remote.response.balance

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.balance.InOutBalanceData

data class InOutBalanceResponse (
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: InOutBalanceData
)