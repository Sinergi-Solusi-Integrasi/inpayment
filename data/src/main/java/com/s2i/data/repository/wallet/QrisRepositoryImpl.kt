package com.s2i.data.repository.wallet

import android.util.Log
import com.s2i.data.mapper.toDomainQrisModel
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.client.WalletServices
import com.s2i.data.remote.request.wallet.TopupRequest
import com.s2i.domain.entity.model.wallet.OrderQrisModel
import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.entity.model.wallet.TopupQris
import com.s2i.domain.entity.model.wallet.TopupQrisModel
import com.s2i.domain.repository.wallet.WalletRepository

class QrisRepositoryImpl(
    private val qrisApiService: WalletServices,
    private val apiServices: ApiServices
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

    override suspend fun orderQuerys(trxId: String): OrderQrisModel {
        return try {
            val response = qrisApiService.orderQuery(trxId)
            OrderQrisModel(
                rCode = response.rCode ?: "00", // Default ke "00" jika null
                message = response.message ?: "Pending",
                trxId = response.trxId ?: trxId // Gunakan trxId yang dikirim jika null
            )
        } catch (e: Exception) {
            Log.e("QrisRepositoryImpl", "Error while querying QRIS order: ${e.message}")
            throw Exception("Error while querying QRIS order: ${e.message}")
        }
    }

    override suspend fun topUp(
        userId: String,
        referenceId: String,
        amount: Int,
        feeAmount: Int,
        paymentMethod: String
    ): TopupQris {
        // Implementasi topup
        val topupRequest = TopupRequest(
            userId = userId,
            referenceId = referenceId,
            amount = amount,
            feeAmount = feeAmount,
            paymentMethod = paymentMethod
        )

        return try {
            val response = apiServices.topup(topupRequest)
            // Mapping data dari response API ke objek domain
            TopupQris(
                code = response.code,
                message = response.message,
                data = TopupQrisModel(
                    transactionId = response.data.transactionId,
                    userId = response.data.userId,
                    userName = response.data.userName,
                    referenceId = response.data.referenceId,
                    amount = response.data.amount,
                    feeAmount = response.data.feeAmount,
                    paymentMethod = response.data.paymentMethod,
                    status = response.data.status,
                    datetime = response.data.datetime,
                ) // Pastikan tipe ini sesuai dengan `TopupQris.data`
            )
        } catch (e: Exception) {
            Log.e("QrisRepositoryImpl", "Error while topup: ${e.message}")
            throw Exception("Error while topup: ${e.message}")
        }

    }


}