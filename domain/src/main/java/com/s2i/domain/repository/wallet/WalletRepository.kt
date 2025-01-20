package com.s2i.domain.repository.wallet

import com.s2i.domain.entity.model.wallet.OrderQrisModel
import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.entity.model.wallet.TopupQris

interface WalletRepository {

    suspend fun qrisCreate(
        mid: String,
        tid: String,
        trxid: String,
        amount: String,
        waktu: String,
        signature: String,
        clientid: String
    ): QrisCreateModel

    suspend fun orderQuerys(
        trxId: String
    ): OrderQrisModel

    suspend fun topUp(
        userId: String,
        referenceId: String,
        amount: Int,
        feeAmount: Int,
        paymentMethod: String
    ): TopupQris
}