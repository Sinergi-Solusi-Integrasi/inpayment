package com.s2i.data.repository.vehicles

import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.vehicle.AgencyCardModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class VehiclesRepositoryImpl(
    private val apiServices: ApiServices
) : VehiclesRepository {

    override suspend fun getVehicles(): GetVehiclesModel {
        val response = apiServices.vehicles()
        val responseData = response.data

        // vehicles data
        val vehicles = responseData.map { vehicleData ->
            VehicleModel(
                vehicleId = vehicleData.vehicleId,
                ownserUserId = vehicleData.ownserUserId,
                borrowerUserId = vehicleData.borrowerUserId,
                brand = vehicleData.brand,
                model = vehicleData.model,
                varian = vehicleData.varian,
                color = vehicleData.color,
                plateNumber = vehicleData.plateNumber,
                group = vehicleData.group,
                rfid = vehicleData.rfid,
                priority = vehicleData.priority,
                images = vehicleData.images,
                certificateImage = vehicleData.certificateImage,
                loanExpiredAt = vehicleData.loanExpiredAt,
                loanedAt = vehicleData.loanedAt,
                createdAt = vehicleData.createdAt,
                updatedAt = vehicleData.updatedAt,
                isOwner = vehicleData.isOwner,
                isLoaned = vehicleData.isLoaned,
                status = vehicleData.status,
                agencyCard = vehicleData.agencyCard?.let { agencyCardData ->
                    AgencyCardModel(
                        cardNumber = agencyCardData.cardNumber,
                        embossId = agencyCardData.embossId,
                        expiredAt = agencyCardData.expiredAt
                    )
                }?: AgencyCardModel("", "", "")
            )
        }


        return GetVehiclesModel(
            code = response.code,
            message = response.message,
            data = vehicles
        )

    }
}