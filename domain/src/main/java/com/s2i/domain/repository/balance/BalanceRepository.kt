package com.s2i.domain.repository.balance

import com.s2i.domain.entity.model.balance.BalanceModel

interface BalanceRepository {
    suspend fun getBalance(): BalanceModel
}