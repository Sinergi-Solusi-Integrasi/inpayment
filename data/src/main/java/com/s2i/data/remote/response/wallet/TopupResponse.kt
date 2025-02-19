package com.s2i.data.remote.response.wallet

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.wallet.TopupData

data class TopupResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: TopupData

)
