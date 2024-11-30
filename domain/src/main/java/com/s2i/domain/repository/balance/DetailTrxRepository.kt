package com.s2i.domain.repository.balance

import com.s2i.domain.entity.model.balance.DetailTrxModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel

interface DetailTrxRepository {
    suspend fun getDetailTrx(transactionId: String): DetailTrxModel
}