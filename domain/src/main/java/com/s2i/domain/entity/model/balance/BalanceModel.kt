package com.s2i.domain.entity.model.balance

import com.google.gson.annotations.SerializedName

data class BalanceModel(
    val vehicleUserId: String,
    val plateNumber: String,
    val rfid: String,
    val accountNumber: String,
    val balance: Int,
    val updatedAt: String
)
