package com.s2i.data.repository.wallet

import android.util.Log
import com.s2i.data.mapper.toDomainQrisModel
import com.s2i.data.remote.client.WalletServices
import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.repository.wallet.WalletRepository

class QrisRepositoryImpl(
    private val qrisApiService: WalletServices
) : WalletRepository {

    override suspend fun qrisCreate(
        mid: String,
        tid: String,
        trxid: String,
        amount: String,
        waktu: String,
        signature: String,
        clientid: String
    ): QrisCreateModel {
        return try {
            val response = qrisApiService.createQRIS(
                mid = mid,
                tid = tid,
                trxid = trxid,
                amount = amount,
                waktu = waktu,
                signature = signature,
                clientid = clientid
            )
            response.toDomainQrisModel()
        } catch (e: Exception) {
            Log.e("QrisRepositoryImpl", "Error while creating QRIS: ${e.message}")
            throw Exception ("Error while creating QRIS: ${e.message}")
        }
    }
}