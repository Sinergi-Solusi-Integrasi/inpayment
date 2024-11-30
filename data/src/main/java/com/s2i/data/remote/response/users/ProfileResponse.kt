package com.s2i.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
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
)
