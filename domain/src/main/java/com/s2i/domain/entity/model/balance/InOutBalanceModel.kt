package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class InOutBalanceModel(
    val accountNumber: String,
    val historyCount: Int,
    val history: List<HistoryBalanceModel>,
)
