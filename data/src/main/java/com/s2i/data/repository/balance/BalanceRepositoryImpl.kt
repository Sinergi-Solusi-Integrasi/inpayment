package com.s2i.data.repository.balance

import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.BalanceResponse
import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.domain.repository.balance.BalanceRepository

class BalanceRepositoryImpl(
    private val apiServices: ApiServices,
) : BalanceRepository {
    override suspend fun getBalance(token: String): BalanceModel{
        val response = apiServices.balance(token)
        return BalanceModel(
            accountNumber = response.data.accountNumber,
            balance = response.data.balance,
            updatedAt = response.data.updatedAt
        )
    }
}