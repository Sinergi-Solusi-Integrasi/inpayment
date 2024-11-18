package com.s2i.domain.repository.balance

import com.s2i.domain.entity.model.balance.InOutBalanceModel

interface HistoryBalanceRepository {
    suspend fun getHistoryBalance() : InOutBalanceModel
}