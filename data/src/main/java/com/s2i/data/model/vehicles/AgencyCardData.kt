package com.s2i.data.model.vehicles

import com.google.gson.annotations.SerializedName

data class AgencyCardData(
    @SerializedName("card_number")
    val cardNumber: String,
    @SerializedName("emboss_id")
    val embossId: String,
    @SerializedName("expired_at")
    val expiredAt: String,
)
