package com.s2i.data.model.notifications.services.firebase

import com.google.gson.annotations.SerializedName
import com.s2i.domain.entity.model.notification.services.TopupPayloadModel
import java.util.StringTokenizer

data class ServicePayloadTopUpData(
    @SerializedName("token")
    val token: String,
    @SerializedName("data")
    val notification: TopupPayloadModel

)

data class ServicePayloadTollPayData(
    @SerializedName("token")
    val token: String,
    @SerializedName("data")
    val notification: TollPaymentPayload

)
