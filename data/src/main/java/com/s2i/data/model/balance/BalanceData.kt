package com.s2i.data.model.balance

import com.google.gson.annotations.SerializedName

data class BalanceData(
    @SerializedName("vehicle_user_id")
    val vehicleUserId: String,
    @SerializedName("PlateNumber")
    val plateNumber: String,
    @SerializedName("RFID")
    val rfid: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)
