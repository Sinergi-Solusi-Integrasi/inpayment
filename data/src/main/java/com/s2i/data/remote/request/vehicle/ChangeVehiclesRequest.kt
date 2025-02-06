package com.s2i.data.remote.request.vehicle

import com.google.gson.annotations.SerializedName

data class ChangeVehiclesRequest(
    @SerializedName("vehicle_id")
    val vehicleId: String,
)
