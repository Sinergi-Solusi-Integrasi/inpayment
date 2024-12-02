package com.s2i.domain.repository.vehicles

import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel

interface VehiclesRepository {
    suspend fun getVehicles(): GetVehiclesModel
}