package com.s2i.data.remote.request.auth

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.users.BlobImageData

data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email_address")
    val email: String,
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("identity_number")
    val identityNumber: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("identity_image")
    val identityImage: BlobImageData

)
