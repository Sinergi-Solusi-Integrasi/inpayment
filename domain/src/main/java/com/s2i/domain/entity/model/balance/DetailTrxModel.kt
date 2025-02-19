package com.s2i.domain.entity.model.balance

data class DetailTrxModel(
    val code: Int,
    val message: String,
    val data: HistoryBalanceModel
)
