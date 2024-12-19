package com.s2i.data.model.notifications.services.firebase

import com.google.gson.annotations.SerializedName

data class TollPaymentPayload(
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("payment_methods")
    val paymentMethod: String,
    @SerializedName("plate_detected")
    val plateDetected: String,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("receipt_number")
    val receiptNumber: String,
    @SerializedName("reference_id")
    val refId: String,
    @SerializedName("rfid")
    val rfid: String,
    @SerializedName("rfid_detected")
    val rfidDetected: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("toll_gate")
    val tollGate: String,
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
    @SerializedName("vehicle_id")
    val vehiclesId: String,

    )
