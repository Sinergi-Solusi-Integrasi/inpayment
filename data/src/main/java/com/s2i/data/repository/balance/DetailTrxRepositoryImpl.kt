package com.s2i.data.repository.balance

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.balance.DetailTrxModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.repository.balance.DetailTrxRepository

class DetailTrxRepositoryImpl(
    private val apiServices: ApiServices,
): DetailTrxRepository {

    override suspend fun getDetailTrx(transactionId: String): DetailTrxModel {
        val response = apiServices.transactionsById(transactionId)
        val responseData = response.data

        return responseData.let{
            val historyBalance = HistoryBalanceModel(
                    transactionId = responseData.transactionId,
                    userId = responseData.userId,
                    accountNumber = responseData.accountNumber,
                    refId = responseData.refId,
                    amount = responseData.amount,
                    fee = responseData.fee,
                    cashFlow =responseData.cashFlow,
                    trxType = responseData.trxType,
                    paymentMethod = responseData.paymentMethod,
                    startingBalance = responseData.startingBalance,
                    endingBalance = responseData.endingBalance,
                    title = responseData.title,
                    status = responseData.status,
                    trxDate = responseData.trxDate,
                    createdAt = responseData.createdAt,
                    updatedAt = responseData.updatedAt,
                    topUp = responseData?.topUp?.let {
                        TopUpModel(
                            trxId = it.trxId,
                            issuerPan = it.issuerPan,
                            issuerName = it.issuerName,
                            customerPan = it.customerPan,
                            customerName = it.customerName,
                            externTrxId = it.externTrxId
                        )
                    } ?: TopUpModel("", "", "", "", "", "")
                )

            DetailTrxModel(
                code = response.code,
                message = response.message,
                data = historyBalance
            )
        }
    }
}