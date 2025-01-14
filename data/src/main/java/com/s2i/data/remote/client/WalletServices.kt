package com.s2i.data.remote.client

import com.s2i.data.remote.response.wallet.QrisResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WalletServices {

    // create QRIS
    @FormUrlEncoded
    @POST("DanaApi/public/createqris")
    suspend fun createQRIS(
        @Field("mid") mid: String,
        @Field("tid") tid: String,
        @Field("trxid") trxid: String,
        @Field("amount") amount: String,
        @Field("waktu") waktu: String,
        @Field("signature") signature: String,
        @Field("clientid") clientid: String
    ): QrisResponse

    //Order Query

    @FormUrlEncoded
    @POST("orderQuery/TRXM95YISU61NY")
    suspend fun orderQuery(

    ): QrisResponse
}