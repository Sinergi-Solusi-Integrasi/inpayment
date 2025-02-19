package com.s2i.domain.entity.model.users

import com.google.gson.annotations.SerializedName

data class UsersProfileModel(
    val code: Int,
    val message: String,
    val data: ProfileModel
)
