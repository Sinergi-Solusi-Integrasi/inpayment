package com.s2i.domain.usecase.wallet

import com.s2i.domain.entity.model.wallet.OrderQrisModel
import com.s2i.domain.repository.wallet.WalletRepository

class OrderQueryQrisUseCase(
    private val qrisRepository: WalletRepository
) {

    suspend operator fun invoke(trxId: String): OrderQrisModel {
        return qrisRepository.orderQuerys(trxId)
    }

}