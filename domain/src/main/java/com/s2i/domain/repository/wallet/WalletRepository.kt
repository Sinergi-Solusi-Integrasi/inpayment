package com.s2i.domain.repository.wallet

import com.google.gson.annotations.SerializedName
import com.s2i.domain.entity.model.wallet.OrderQrisModel
import com.s2i.domain.entity.model.wallet.QrisCreateModel

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
}