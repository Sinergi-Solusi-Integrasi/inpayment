package com.s2i.domain.entity.model.notification.services

data class RegisterDevicesTokenModel(
    val devicesId: String,
    val model: String,
    val osType: String,
    val platform: String,
    val sdkVersion: String,
    val tokenFirebase: String,
    val createdAt: String ? = null
)
