package com.s2i.domain.entity.model.users

import com.google.gson.annotations.SerializedName

data class BlobImageModel(
    val ext: String,
    val mimeType: String,
    val data: String
)
