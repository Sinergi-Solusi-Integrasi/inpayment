package com.s2i.domain.entity.model.vehicle

import com.google.gson.annotations.SerializedName

data class VehicleModel(
    val vehicleId: String? = null,
    val statusVehiclesId: String? = null,
    val ownserUserId: String? = null,
    val borrowerUserId: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val varian: String? = null,
    val color: String? = null,
    val nameVehicles: String? = null,
    val plateNumber: String? = null,
    val group: Int? = null,
    val rfid: String? = null,
    val priority: Int? = null,
    val images: List<String>? = null,
    val certificateImage: String? = null,
    val loanExpiredAt: String? = null,
    val loanedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isOwner: Boolean? = null,
    val isLoaned: Boolean? = null,
    val status: String? = null,
    val agencyCard: AgencyCardModel? = null

)
