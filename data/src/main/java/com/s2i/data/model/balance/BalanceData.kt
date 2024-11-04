package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class BalanceData(
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)
