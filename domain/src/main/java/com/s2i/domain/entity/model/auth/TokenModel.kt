package com.s2i.domain.entity.model.auth

data class TokenModel(
    val accessToken: String,
    val expiresAt: String,
    val username: String
)
