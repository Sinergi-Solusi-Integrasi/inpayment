package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class InOutBalanceData(
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("history_count")
    val historyCount: Int,
    @SerializedName("history")
    val history: List<HistoryBalanceData>,
)
