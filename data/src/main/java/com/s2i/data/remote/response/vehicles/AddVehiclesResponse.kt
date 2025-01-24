package com.s2i.data.remote.response.vehicles

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.vehicles.VehiclesData

data class AddVehiclesResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val vehiclesData: VehiclesData
)
