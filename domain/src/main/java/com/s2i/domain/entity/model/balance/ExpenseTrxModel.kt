package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class ExpenseTrxModel(
    val trxId: String,
    val userId: String,
    val accountNumber: String,
    val refId: String,
    val amount: Int,
    val feeAmount: Int,
    val cashflow: String,
    val trxType: String,
    val paymentMethods: String,
    val beginningBalance: Int,
    val endingBalance: Int,
    val title: String,
    val trxStatus: String,
    val trxDates: String,
    val createdAt: String,
    val updatedAt: String
)
