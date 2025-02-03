package com.s2i.domain.entity.model.vehicle

import com.google.gson.annotations.SerializedName

data class SelectedVehicleModel(
    val vehicleId: String,
    val status: String,
)
