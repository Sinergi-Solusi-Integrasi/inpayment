package com.s2i.data.remote.request.vehicle

import com.google.gson.annotations.SerializedName
import com.s2i.data.model.users.BlobImageData

data class VehiclesAddRequest(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("varian")
    val varian: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("vehicle_registration_certificate_image")
    val documentImage: BlobImageData,
    @SerializedName("vehicle_images")
    val vehicleImages: List<BlobImageData>
)
