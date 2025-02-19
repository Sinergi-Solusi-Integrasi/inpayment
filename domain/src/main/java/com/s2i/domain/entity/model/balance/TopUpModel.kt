package com.s2i.domain.entity.model.balance

data class TopUpModel(
    val trxId: String,
    val issuerPan: String,
    val issuerName: String,
    val customerPan: String,
    val customerName: String,
    val externTrxId: String
)
