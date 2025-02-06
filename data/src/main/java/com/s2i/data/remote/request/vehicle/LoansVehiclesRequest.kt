package com.s2i.data.remote.request.vehicle

import com.google.gson.annotations.SerializedName

data class LoansVehiclesRequest(
    @SerializedName("token")
    val token: String
)
