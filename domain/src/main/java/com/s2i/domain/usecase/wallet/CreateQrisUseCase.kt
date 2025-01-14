package com.s2i.domain.usecase.wallet

import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.repository.wallet.WalletRepository

class CreateQrisUseCase(
    private val qrisRepository: WalletRepository
) {

    suspend operator fun invoke(
        mid: String,
        tid: String,
        trxid: String,
        amount: String,
        waktu: String,
        signature: String,
        clientid: String
    ) : QrisCreateModel {
        return qrisRepository.qrisCreate(
            mid = mid,
            tid = tid,
            trxid = trxid,
            amount = amount,
            waktu = waktu,
            signature = signature,
            clientid = clientid
        )
    }
}