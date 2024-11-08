package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class HistoryBalanceData(
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("reference_id")
    val refId: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("fee_amount")
    val fee: Int,
    @SerializedName("cashflow")
    val cashFlow: String,
    @SerializedName("transaction_type")
    val trxType: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("beginning_balance")
    val startingBalance: Int,
    @SerializedName("ending_balance")
    val endingBalance: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("transaction_status")
    val status: String,
    @SerializedName("transaction_datetime")
    val trxDate: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
