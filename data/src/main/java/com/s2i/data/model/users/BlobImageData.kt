package com.s2i.data.model.users

import com.google.gson.annotations.SerializedName

data class BlobImageData(
    @SerializedName("ext")
    val ext: String,
    @SerializedName("mimeType")
    val mimeType: String,
    @SerializedName("data")
    val data: String
)
