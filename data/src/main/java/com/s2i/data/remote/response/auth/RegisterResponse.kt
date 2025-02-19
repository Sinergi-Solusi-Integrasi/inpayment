package com.s2i.data.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.users.UsersData

data class RegisterResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: UsersData

)
