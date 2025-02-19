package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class ExpenseTrx(
    @SerializedName("transaction_id")
    val trxId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("reference_id")
    val refId: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("fee_amount")
    val feeAmount: Int,
    @SerializedName("cashflow")
    val cashflow: String,
    @SerializedName("transaction_type")
    val trxType: String,
    @SerializedName("payment_methods")
    val paymentMethods: String,
    @SerializedName("beginning_balance")
    val beginningBalance: Int,
    @SerializedName("ending_balance")
    val endingBalance: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("transaction_status")
    val trxStatus: String,
    @SerializedName("transaction_datetime")
    val trxDates: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
