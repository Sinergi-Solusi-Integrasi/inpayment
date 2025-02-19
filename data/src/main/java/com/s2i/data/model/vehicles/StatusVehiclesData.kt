package com.s2i.data.model.vehicles

import com.google.gson.annotations.SerializedName

data class StatusVehiclesData(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("loan_account_number")
    val loanAccountNumber: String? = null,
    @SerializedName("loan_user_id")
    val loanUserId: String ? = null,
    @SerializedName("loan_expired_at")
    val loanExpiredAt: String? = null,
    @SerializedName("token")
    val token: LoansTokenVehiclesData? = null

)
