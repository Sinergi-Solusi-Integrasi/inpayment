package com.s2i.domain.entity.model.auth

data class AuthLogoutModel(
    val code: Int,
    val message: String,
    val data: LogoutModel
)
