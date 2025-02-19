package com.s2i.data.remote.request.wallet

import com.google.gson.annotations.SerializedName

data class QrisRequest(
    @SerializedName("mid")
    val mid: String,
    @SerializedName("tid")
    val tid: String,
    @SerializedName("trxid")
    val trxId: String,
    @SerializedName("amount")
    val txnAmount: Double,
    @SerializedName("waktu")
    val txnDate: String,
    @SerializedName("signature")
    val signatures: String,
    @SerializedName("clientid")
    val clientId: String

)
