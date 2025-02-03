package com.s2i.domain.repository.vehicles

import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.StatusVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel

interface VehiclesRepository {

    suspend fun registVehicles(
        brand: String,
        model: String,
        varian: String,
        color: String,
        type: String,
        plateNumber: String,
        documentImage: BlobImageModel,
        vehicleImages: List<BlobImageModel>
    ): RegisVehiclesModel
    suspend fun getVehicles(): GetVehiclesModel
    suspend fun getStatusEnableVehicles(vehicleId: String): StatusVehiclesModel
    suspend fun getStatusDisableVehicles(vehicleId: String): StatusVehiclesModel
}