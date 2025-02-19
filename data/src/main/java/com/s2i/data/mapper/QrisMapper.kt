package com.s2i.data.mapper

import com.s2i.data.remote.response.wallet.QrisResponse
import com.s2i.domain.entity.model.wallet.QrisCreateModel

fun QrisResponse.toDomainQrisModel(): QrisCreateModel {
    return QrisCreateModel(
        rCode = rCode,
        message = message,
        trxId = trxId,
        qrisCode = qrisCode,
        reqMsgId = reqMsgId
    )
}