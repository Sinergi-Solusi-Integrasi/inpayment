package com.s2i.domain.repository.notifications.services

import com.s2i.domain.entity.model.notification.services.BindingModel
import com.s2i.domain.entity.model.notification.services.DevicesTokenModel
import com.s2i.domain.entity.model.notification.services.TollPayPayloadModel
import com.s2i.domain.entity.model.notification.services.TopupPayloadModel

interface ServicesRepository {

    suspend fun devicesToken(
        brand: String,
        model: String,
        osType: String,
        platform: String,
        sdkVersion: String,
        tokenFirebase: String
    ): DevicesTokenModel

    suspend fun bindingAccount(
        deviceId: String
    ) : BindingModel
}