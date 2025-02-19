package com.s2i.domain.entity.model.notification.services

data class DevicesTokenModel(
    val code: Int,
    val message: String,
    val data: RegisterDevicesTokenModel
)
