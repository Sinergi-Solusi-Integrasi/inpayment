package com.s2i.data.model.vehicles

import com.google.gson.annotations.SerializedName
import com.s2i.domain.entity.model.vehicle.AgencyCardModel

data class VehiclesData(
    @SerializedName("id")
    val vehicleId: String,
    @SerializedName("owner_user_id")
    val ownserUserId: String,
    @SerializedName("borrower_user_id")
    val borrowerUserId: String? = null,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("varian")
    val varian: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("group")
    val group: Int,
    @SerializedName("rfid")
    val rfid: String,
    @SerializedName("priority")
    val priority: Int,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("registration_certificate_image")
    val certificateImage: String,
    @SerializedName("loan_expired_at")
    val loanExpiredAt: String? = null,
    @SerializedName("loaned_at")
    val loanedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_owner")
    val isOwner: Boolean,
    @SerializedName("is_loaned")
    val isLoaned: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("agency_card")
    val agencyCard: AgencyCardData? = null
)
