package com.s2i.data.remote.response.balance

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.balance.BalanceData

data class BalanceResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: BalanceData
)
