package com.s2i.domain.usecase.notifications.services

import com.s2i.domain.entity.model.notification.services.DevicesTokenModel
import com.s2i.domain.repository.notifications.services.ServicesRepository

class DevicesTokenUseCase(private val repository: ServicesRepository) {

    suspend operator fun invoke(
        brand: String,
        model: String,
        osType: String,
        platform: String,
        sdkVersion: String,
        tokenFirebase: String
    ): DevicesTokenModel {
        return repository.devicesToken(
            brand = brand,
            model = model,
            osType = osType,
            platform = platform,
            sdkVersion = sdkVersion,
            tokenFirebase = tokenFirebase
        )
    }
}
