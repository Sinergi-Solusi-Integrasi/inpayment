package com.s2i.domain.entity.model.notification

import com.s2i.domain.entity.model.balance.HistoryBalanceModel

data class NotitificationsTrxModel(
    val code: Int,
    val message: String,
    val data: HistoryBalanceModel? = null
)
