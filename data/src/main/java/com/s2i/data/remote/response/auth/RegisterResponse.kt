package com.s2i.data.remote.response.auth

import com.s2i.data.model.users.UsersData

data class RegisterResponse(
    val code: Int,
    val message: String,
    val data: List<UsersData>

)
