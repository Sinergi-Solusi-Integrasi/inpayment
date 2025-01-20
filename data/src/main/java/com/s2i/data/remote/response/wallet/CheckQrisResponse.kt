package com.s2i.data.remote.response.wallet

import com.google.gson.annotations.SerializedName

data class CheckQrisResponse(
    @SerializedName("rcode")
    val rCode: String?,
    @SerializedName("message")
    val message: String,
    @SerializedName("trxid")
    val trxId: String?
)
