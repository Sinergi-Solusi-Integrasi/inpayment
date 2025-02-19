package com.s2i.data.remote.request.wallet

import com.google.gson.annotations.SerializedName

data class TopupRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("fee_amount")
    val feeAmount: Int,
    @SerializedName("payment_method")
    val paymentMethod: String,
)
