package com.s2i.data.model.users

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.vehicles.VehiclesData

data class UsersProfileData(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("account_number")
    val accountNumber: String,
    @SerializedName("email_address")
    val email: String,
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("selected_vehicle")
    val selectVehicle: VehiclesData? = null
)
