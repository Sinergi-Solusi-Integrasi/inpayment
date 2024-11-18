package com.s2i.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email_address")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("identity_number")
    val identityNumber: String,
    @SerializedName("address")
    val address: String,
//    @SerializedName("identity_image")
//    val identityImage: BlobImageData
)
