package com.s2i.domain.entity.model.wallet

data class TopupQris(
    val code: Int,
    val message: String,
    val data: TopupQrisModel
)
