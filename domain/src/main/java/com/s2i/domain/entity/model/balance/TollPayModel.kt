package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class TollPayModel(
    val trxId: String,
    val vehiclesId: String,
    val branchId: String,
    val gateId: String,
    val stationId: String,
    val shift: String,
    val period: String,
    val tollCollectorId: String,
    val shiftLeaderId: String,
    val receiptNumber: String,
    val plateNumber: String,
    val rfid: String,
    val rfidDetected: Boolean,
    val plateDetected: Boolean
)

