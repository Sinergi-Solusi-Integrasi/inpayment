package com.s2i.data.remote.client

import com.s2i.data.model.balance.InOutBalanceData
import com.s2i.data.remote.request.auth.LogoutRequest
import com.s2i.data.remote.request.auth.RegisterRequest
import com.s2i.data.remote.request.services.DevicesTokenRequest
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.data.remote.response.auth.LogoutResponse
import com.s2i.data.remote.response.auth.RefreshTokenResponse
import com.s2i.data.remote.response.auth.RegisterResponse
import com.s2i.data.remote.response.balance.BalanceResponse
import com.s2i.data.remote.response.balance.HistoryBalanceByIdResponse
import com.s2i.data.remote.response.balance.HistoryBalanceResponse
import com.s2i.data.remote.response.balance.InOutBalanceResponse
import com.s2i.data.remote.response.balance.IncomeExpenseResponse
import com.s2i.data.remote.response.notification.balance.NotificationTrx
import com.s2i.data.remote.response.notification.services.BindingAccountResponse
import com.s2i.data.remote.response.notification.services.RegisterDevicesResponse
import com.s2i.data.remote.response.users.ProfileResponse
import com.s2i.data.remote.response.users.UsersResponse
import com.s2i.data.remote.response.vehicles.SelectedVehiclesResponse
import com.s2i.data.remote.response.vehicles.VehiclesResponse
import com.s2i.domain.entity.model.auth.AuthModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

   /* Auth*/

    // Login
    @POST("auth/login")
    fun login(
        @Body loginData: Map<String, String>
    ): Call<LoginResponse>

    // Register
    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    // RefreshToken
    @POST("auth/token")
    fun refreshAccessToken(
        @Body refreshTokenBody: Map<String, String>
    ): RefreshTokenResponse

    // Logout
    @POST("auth/logout")
    suspend fun logout(
        @Body logoutData: LogoutRequest
    ): LogoutResponse

    // Get Profile
    @GET("accounts/profile")
    suspend fun profile(
    ): UsersResponse


    //Vehicles

    // Get Vehicles
    @GET("vehicles")
    suspend fun vehicles(
    ): VehiclesResponse

    // Get Vehicles selected
    @GET("vehicles/selected")
    suspend fun vehiclesSelected(
    ): VehiclesResponse

    // Get Vehicles selected
    @PUT("vehicles/selected")
    suspend fun vehiclesSwitchSelected(
    ): VehiclesResponse

    // PUT Vehicles disable
    @PATCH("vehicles/{vehicle_id}/disable")
    suspend fun vehiclesDisable(
        @Path("vehicle_id") vehicleId: String
    ): SelectedVehiclesResponse

    // PUT Vehicles disable
    @PATCH("vehicles/{vehicle_id}/enable")
    suspend fun vehiclesEnable(
        @Path("vehicle_id") vehicleId: String
    ): SelectedVehiclesResponse

    // Get Balance
    @GET("accounts/balance")
    suspend fun balance(
    ): BalanceResponse

    // Get 3 last Transaction
    @GET("transactions/history")
    suspend fun transactions(
    ): InOutBalanceResponse

    @GET("transactions/{transaction_id}")
    suspend fun transactionsById(
        @Path("transaction_id") transactionId: String
    ): HistoryBalanceByIdResponse

    // Get Income and Expense
    @GET("transactions/cashflow")
    suspend fun incomeExpense(
    ): IncomeExpenseResponse

    // Get Notification
    @GET("notifications/payments/pull")
    suspend fun notifTrx(
    ): NotificationTrx

    // Send Token devices to server
    @POST("devices/register")
    suspend fun sendTokenDevice(
        @Body tokenDeviceRequest: DevicesTokenRequest
    ): RegisterDevicesResponse


    // binding account
    @PATCH("devices/{device_id}/accounts/bind")
    suspend fun bindingAccounts(
        @Path("device_id") deviceId: String
    ): BindingAccountResponse

}