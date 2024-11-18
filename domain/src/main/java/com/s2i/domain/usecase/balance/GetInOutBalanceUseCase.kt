package com.s2i.domain.usecase.balance

import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.repository.balance.HistoryBalanceRepository
import com.s2i.domain.repository.balance.InOutBalanceRepository

class GetInOutBalanceUseCase(
    private val trilastRepository: InOutBalanceRepository
) {
    suspend operator fun invoke() : InOutBalanceModel {
        return trilastRepository.getInOutBalance()
    }

}