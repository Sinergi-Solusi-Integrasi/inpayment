package com.s2i.data.repository.balance

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.entity.model.balance.TollPayModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.repository.balance.InOutBalanceRepository

class InOutBalanceRepositoryImpl(
    private val apiServices: ApiServices,
) : InOutBalanceRepository {
    override suspend fun getInOutBalance(): InOutBalanceModel {
        val response = apiServices.transactions()

        // Maping history data
        val historyModels = response.data.history.take(3).map{ historyData ->
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
                } ?: TopUpModel("", "", "", "", "", ""),
                tollPayment = historyData?.tollPayment?.let {
                    TollPayModel(
                        trxId = it.trxId,
                        vehiclesId = it.vehiclesId,
                        branchId = it.branchId,
                        gateId = it.gateId,
                        stationId = it.stationId,
                        shift = it.shift,
                        period = it.period,
                        tollCollectorId = it.tollCollectorId,
                        shiftLeaderId = it.shiftLeaderId,
                        receiptNumber = it.receiptNumber,
                        plateNumber = it.plateNumber,
                        rfid = it.rfid,
                        rfidDetected = it.rfidDetected,
                        plateDetected = it.plateDetected

                    )

                }
            )
        }
        return InOutBalanceModel(
            accountNumber = response.data.accountNumber,
            historyCount = response.data.historyCount,
            history = historyModels
        )
    }

}