package com.s2i.data.remote.response.users

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.users.UsersProfileData
import com.s2i.data.model.vehicles.VehiclesData

data class UsersResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: UsersProfileData
)
