package com.s2i.domain.entity.model.notification.services

import com.google.gson.annotations.SerializedName

data class BindingAccountModel(
    val devicesId: String,
    val userId: String,
    val bindingAt: String
)
