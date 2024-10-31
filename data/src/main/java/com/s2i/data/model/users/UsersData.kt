package com.s2i.data.model.users

import com.google.gson.annotations.SerializedName

data class UsersData(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
//    @SerializedName("password")
//    val password: String,
//    @SerializedName("email")
//    val email: String,
//    @SerializedName("mobile_number")
//    val mobileNumber: String,
//    @SerializedName("identity_number")
//    val identityNumber: String,
//    @SerializedName("address")
//    val address: String
)
