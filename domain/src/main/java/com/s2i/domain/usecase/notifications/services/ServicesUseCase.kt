package com.s2i.domain.usecase.notifications.services

import com.s2i.domain.entity.model.notification.services.BindingModel
import com.s2i.domain.repository.notifications.services.ServicesRepository

class ServicesUseCase(
    private val servicesRepository: ServicesRepository
) {

    suspend operator fun invoke(deviceId: String): BindingModel {
        if (deviceId.isBlank()) {
            throw IllegalArgumentException("Device ID cannot be empty")
        }
        return servicesRepository.bindingAccount(deviceId)
    }
}