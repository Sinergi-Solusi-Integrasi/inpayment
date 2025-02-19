package com.s2i.domain.usecase.wallet

import com.s2i.domain.entity.model.wallet.TopupQris
import com.s2i.domain.repository.wallet.WalletRepository

class TopupQrisUseCase(
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(
        userId: String,
        referenceId: String,
        amount: Int,
        feeAmount: Int,
        paymentMethod: String
    ) : TopupQris {
        return walletRepository.topUp(
            userId = userId,
            referenceId = referenceId,
            amount = amount,
            feeAmount = feeAmount,
            paymentMethod = paymentMethod
        )
    }
}