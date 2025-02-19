package com.s2i.data.model.notifications.services.firebase

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.balance.TollPayData
import com.s2i.data.model.balance.TopUpData

data class TopUpPayload(
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("customer_pan")
    val customerPan: String,
    @SerializedName("issuer_name")
    val issuerName: String,
    @SerializedName("issuer_pan")
    val issuerPan: String,
    @SerializedName("payment_methods")
    val paymentMethod: String,
    @SerializedName("reference_id")
    val refId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("transaction_amount")
    val trxAmount: Int,
    @SerializedName("transaction_datetime")
    val trxDate: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("transaction_status")
    val status: String,
    @SerializedName("transaction_type")
    val trxType: String,
)
