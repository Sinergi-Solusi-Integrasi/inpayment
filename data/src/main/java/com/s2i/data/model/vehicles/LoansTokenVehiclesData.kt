package com.s2i.data.model.vehicles

import com.google.gson.annotations.SerializedName

data class LoansTokenVehiclesData(
    @SerializedName("token")
    val token: String,
    @SerializedName("expired_at")
    val expiredAt: String
)
