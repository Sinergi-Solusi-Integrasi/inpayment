package com.s2i.domain.usecase.balance

import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.domain.repository.balance.BalanceRepository

class GetBalanceUseCase(
    private val balanceRepository: BalanceRepository
){
    suspend operator fun invoke(token: String): BalanceModel{
        return balanceRepository.getBalance(token)
    }
}