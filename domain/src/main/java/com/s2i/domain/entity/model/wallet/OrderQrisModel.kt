package com.s2i.domain.entity.model.wallet

import com.google.gson.annotations.SerializedName

data class OrderQrisModel(
    val rCode: String?,
    val message: String,
    val trxId: String?
)
