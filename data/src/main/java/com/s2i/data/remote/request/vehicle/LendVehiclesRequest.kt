package com.s2i.data.remote.request.vehicle

import com.google.gson.annotations.SerializedName

data class LendVehiclesRequest(
    @SerializedName("to_account_number")
    val toAccountNumber: String,
    @SerializedName("expired_at")
    val expiredAt: String,
)
