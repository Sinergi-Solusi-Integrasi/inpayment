package com.s2i.domain.usecase.balance

import com.s2i.domain.entity.model.balance.DetailTrxModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.repository.balance.DetailTrxRepository

class GetDetailTrxUseCase(
    private val detailTrxRepository: DetailTrxRepository

) {
    suspend operator fun invoke(transactionId: String): DetailTrxModel {
        return detailTrxRepository.getDetailTrx(transactionId)
    }
}