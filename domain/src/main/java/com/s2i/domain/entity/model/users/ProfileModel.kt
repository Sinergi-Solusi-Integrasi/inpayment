package com.s2i.domain.entity.model.users

import com.s2i.domain.entity.model.vehicle.VehicleModel

data class ProfileModel(
    val userId: String,
    val username: String,
    val name: String,
    val accountNumber: String,
    val email: String,
    val mobileNumber: String,
    val address: String,
    val selectVehicle: VehicleModel? = null
)
