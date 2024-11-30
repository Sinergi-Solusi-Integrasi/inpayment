package com.s2i.data.repository.balance

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.repository.balance.HistoryBalanceRepository

class HistoryBalanceRepositoryImpl(
    private val apiServices: ApiServices
): HistoryBalanceRepository {
    override suspend fun getHistoryBalance(): InOutBalanceModel {
        val response =  apiServices.transactions()

        return response.data.let{
            val historyDataModels = response.data.history.map { historyData ->
                HistoryBalanceModel(
                    transactionId = historyData.transactionId,
                    userId = historyData.userId,
                    accountNumber = historyData.accountNumber,
                    refId = historyData.refId,
                    amount = historyData.amount,
                    fee = historyData.fee,
                    cashFlow = historyData.cashFlow,
                    trxType = historyData.trxType,
                    paymentMethod = historyData.paymentMethod,
                    startingBalance = historyData.startingBalance,
                    endingBalance = historyData.endingBalance,
                    title = historyData.title,
                    status = historyData.status,
                    trxDate = historyData.trxDate,
                    createdAt = historyData.createdAt,
                    updatedAt = historyData.updatedAt,
                    topUp = historyData?.topUp?.let {
                        TopUpModel(
                            trxId = it.trxId ,
                            issuerPan = it.issuerPan,
                            issuerName = it.issuerName,
                            customerPan = it.customerPan,
                            customerName = it.customerName,
                            externTrxId = it.externTrxId
                        )
                    } ?: TopUpModel("", "", "", "", "", "")
                )
            }

            InOutBalanceModel(
                accountNumber = response.data.accountNumber,
                historyCount = response.data.historyCount,
                history = historyDataModels
            )
        }
    }
}