package com.s2i.data.repository.notifications

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.TollPayModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.entity.model.notification.NotitificationsTrxModel
import com.s2i.domain.repository.notifications.NotificationsRepository

class NotificationsRepositoryImpl(
    private val apiServices: ApiServices
): NotificationsRepository {

    override suspend fun getNotifyTrx(): NotitificationsTrxModel {
        val response = apiServices.notifTrx()
        val responseData = response.data

        return responseData?.let {
            val notify = HistoryBalanceModel(
                transactionId = responseData.transactionId,
                userId = responseData.userId,
                accountNumber = responseData.accountNumber,
                refId = responseData.refId,
                amount = responseData.amount,
                fee = responseData.fee,
                cashFlow = responseData.cashFlow,
                trxType = responseData.trxType,
                paymentMethod = responseData.paymentMethod,
                startingBalance = responseData.startingBalance,
                endingBalance = responseData.endingBalance,
                title = responseData.title,
                status = responseData.status,
                trxDate = responseData.trxDate,
                createdAt = responseData.createdAt,
                updatedAt = responseData.updatedAt,
                topUp = responseData.topUp?.let {
                    TopUpModel(
                        trxId = it.trxId ,
                        issuerPan = it.issuerPan,
                        issuerName = it.issuerName,
                        customerPan = it.customerPan,
                        customerName = it.customerName,
                        externTrxId = it.externTrxId
                    )
                } ?: TopUpModel("", "", "", "", "", ""),
                tollPayment = responseData.tollPayment?.let {
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
            NotitificationsTrxModel(
                code = response.code,
                message = response.message,
                data = notify
            )
        }!!

    }
}