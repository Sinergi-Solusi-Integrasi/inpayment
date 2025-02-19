package com.s2i.domain.repository.balance

import com.s2i.domain.entity.model.balance.InOutBalanceModel

interface InOutBalanceRepository {
    suspend fun getInOutBalance() : InOutBalanceModel
}