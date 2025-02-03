package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class RegistVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {

    suspend operator fun invoke(
        brand: String,
        model: String,
        varian: String,
        color: String,
        type: String,
        plateNumber: String,
        documentImage: BlobImageModel,
        vehicleImages: List<BlobImageModel>
    ): RegisVehiclesModel {
        return vehiclesRepository.registVehicles(
            brand,
            model,
            varian,
            color,
            type,
            plateNumber,
            documentImage,
            vehicleImages
        )
    }
}