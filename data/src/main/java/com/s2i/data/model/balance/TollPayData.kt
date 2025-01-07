package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class TollPayData(
    @SerializedName("transaction_id")
    val trxId: String,
    @SerializedName("vehicle_id")
    val vehiclesId: String,
    @SerializedName("branch_id")
    val branchId: String,
    @SerializedName("gate_id")
    val gateId: String,
    @SerializedName("station_id")
    val stationId: String,
    @SerializedName("shift")
    val shift: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("toll_collector_id")
    val tollCollectorId: String,
    @SerializedName("shift_leader_id")
    val shiftLeaderId: String,
    @SerializedName("receipt_number")
    val receiptNumber: String,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("rfid")
    val rfid: String,
    @SerializedName("rfid_detected")
    val rfidDetected: Boolean,
    @SerializedName("plate_detected")
    val plateDetected: Boolean,
    @SerializedName("vehicle_captures")
    val vehicleCaptures: List<String>,
)
