package com.s2i.domain.entity.model.notification.services

data class TopupPayloadModel(
    val accountNumber: String,
    val body: String,
    val customerName: String,
    val customerPan: String,
    val issuerName: String,
    val issuerPan: String,
    val paymentMethod: String,
    val refId: String,
    val title: String,
    val trxAmount: Int,
    val trxDate: String,
    val transactionId: String,
    val status: String,
    val trxType: String,
)
