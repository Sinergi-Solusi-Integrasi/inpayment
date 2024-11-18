package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class HistoryBalanceModel(
    val transactionId: String,
    val userId: String,
    val accountNumber: String,
    val refId: String,
    val amount: Int,
    val fee: Int,
    val cashFlow: String,
    val trxType: String,
    val paymentMethod: String,
    val startingBalance: Int,
    val endingBalance: Int,
    val title: String,
    val status: String,
    val trxDate: String,
    val createdAt: String,
    val updatedAt: String,
    val topUp: TopUpModel?
)
