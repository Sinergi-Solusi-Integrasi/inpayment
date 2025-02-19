package com.s2i.domain.entity.model.notification.services

data class TollPayPayloadModel(
    val accountNumber: String,
    val body: String,
    val paymentMethod: String,
    val plateDetected: String,
    val plateNumber: String,
    val receiptNumber: String,
    val refId: String,
    val rfid: String,
    val rfidDetected: String,
    val title: String,
    val tollGate: String,
    val trxAmount: Int,
    val trxDate: String,
    val transactionId: String,
    val status: String,
    val trxType: String,
    val vehiclesId: String,
)
