package com.s2i.data.model.vehicles

import com.google.gson.annotations.SerializedName

data class StatusVehiclesData(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("status")
    val status: String,
)
