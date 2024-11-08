package com.s2i.data.remote.client

import com.s2i.data.model.balance.InOutBalanceData
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.data.remote.response.auth.RefreshTokenResponse
import com.s2i.data.remote.response.auth.RegisterResponse
import com.s2i.data.remote.response.balance.BalanceResponse
import com.s2i.data.remote.response.balance.InOutBalanceResponse
import com.s2i.domain.entity.model.auth.AuthModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServices {

    // Login
    @POST("auth/login")
    fun login(
        @Body loginData: Map<String, String>
    ): Call<LoginResponse>

    // Register
    @POST("auth/register")
    fun register(
        @Body registerData: Map<String, String>
    ): Call<RegisterResponse>

    // RefreshToken
    @POST("auth/token")
    fun refreshAccessToken(
        @Body refreshTokenBody: Map<String, String>
    ): RefreshTokenResponse

    // Get Balance
    @GET("accounts/balance")
    suspend fun balance(
    ): BalanceResponse

    // Get 3 last Transaction
    @GET("transactions")
    suspend fun transactions(
    ): InOutBalanceResponse

}