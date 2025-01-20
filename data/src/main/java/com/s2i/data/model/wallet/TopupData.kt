package com.s2i.data.model.wallet

import com.google.gson.annotations.SerializedName

data class TopupData(
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("amount")
    val amout: Int,
    @SerializedName("fee_amount")
    val feeAmount: Int,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("datetime")
    val datetime: String,
)
