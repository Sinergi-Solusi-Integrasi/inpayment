package com.s2i.domain.entity.model.wallet

import com.google.gson.annotations.SerializedName

data class TopupQrisModel(
    val transactionId: String,
    val userId: String,
    val userName: String,
    val referenceId: String,
    val amount: Int,
    val feeAmount: Int,
    val paymentMethod: String,
    val status: String,
    val datetime: String,
)
