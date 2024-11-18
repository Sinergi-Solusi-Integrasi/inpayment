package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class TopUpData(
    @SerializedName("transaction_id")
    val trxId: String,
    @SerializedName("issuer_pan")
    val issuerPan: String,
    @SerializedName("issuer_name")
    val issuerName: String,
    @SerializedName("customer_pan")
    val customerPan: String,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("external_transaction_id")
    val externTrxId: String
)
