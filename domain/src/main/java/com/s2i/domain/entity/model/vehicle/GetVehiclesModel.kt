package com.s2i.domain.entity.model.vehicle

data class GetVehiclesModel(
    val code: Int,
    val message: String,
    val data: List<VehicleModel>

)
