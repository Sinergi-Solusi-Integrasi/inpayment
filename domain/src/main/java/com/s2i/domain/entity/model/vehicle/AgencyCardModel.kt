package com.s2i.domain.entity.model.vehicle

import com.google.gson.annotations.SerializedName

data class AgencyCardModel(
    val cardNumber: String,
    val embossId: String,
    val expiredAt: String,
)
