package com.s2i.data.repository.notifications.services

import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.request.services.DevicesTokenRequest
import com.s2i.domain.entity.model.notification.services.BindingAccountModel
import com.s2i.domain.entity.model.notification.services.BindingModel
import com.s2i.domain.entity.model.notification.services.DevicesTokenModel
import com.s2i.domain.entity.model.notification.services.RegisterDevicesTokenModel
import com.s2i.domain.repository.notifications.services.ServicesRepository
import timber.log.Timber

class ServicesRepositoryImpl(
    private val apiServices: ApiServices,
): ServicesRepository {

    override suspend fun devicesToken(
        brand: String,
        model: String,
        osType: String,
        platform: String,
        sdkVersion: String,
        tokenFirebase: String
    ): DevicesTokenModel {
        val requestBody = DevicesTokenRequest(
            brand = brand,
            model = model,
            osType = osType,
            platform = platform,
            sdkVersion = sdkVersion,
            tokenFirebase = tokenFirebase,
        )

        return try {
            // Panggil API dan dapatkan response
            val response = apiServices.sendTokenDevice(requestBody)
            if (response.code != 0) {
                val errorMessage = response.message
                Timber.e("RegisterDevicesRepositoryImpl: Registration failed: $errorMessage")
                throw Exception("Registration failed: $errorMessage")
            } else {
                Timber.d("RegisterDevicesRepositoryImpl: Registration successful")
                // Kembalikan DevicesTokenModel
                DevicesTokenModel(
                    code = response.code,
                    message = response.message,
                    data = RegisterDevicesTokenModel(
                        devicesId = response.data.devicesId,
                        model = response.data.model,
                        osType = response.data.osType,
                        platform = response.data.platform,
                        sdkVersion = response.data.sdkVersion,
                        tokenFirebase = tokenFirebase,
                        createdAt = response.data.createdAt
                    )
                )
            }
            // Pastikan response berisi data yang sesuai
        } catch (e: Exception) {
            // Tangani error dan kembalikan nilai default atau throw ulang exception
            Timber.e("RegisterDevicesRepositoryImpl: Error while sending device token: ${e.message}")
            throw Exception("Error while sending device token: ${e.message}")
        }
    }

    override suspend fun bindingAccount(deviceId: String): BindingModel {
        val response = apiServices.bindingAccounts(deviceId)

        return BindingModel(
            code = response.code,
            message = response.message,
            data = BindingAccountModel(
                devicesId = response.data.devicesId,
                userId = response.data.userId,
                bindingAt = response.data.bindingAt
            )
        )
    }
}