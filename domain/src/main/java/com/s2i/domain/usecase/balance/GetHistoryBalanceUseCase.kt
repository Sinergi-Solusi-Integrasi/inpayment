package com.s2i.domain.usecase.balance

import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.repository.balance.HistoryBalanceRepository

class GetHistoryBalanceUseCase(
    private val historyRepository: HistoryBalanceRepository
) {
    suspend operator fun invoke() : InOutBalanceModel {
        return historyRepository.getHistoryBalance()
    }
}