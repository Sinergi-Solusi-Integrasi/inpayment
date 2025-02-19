package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.StatusVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class GetDisableStatusUseCase(
    private val vehiclesRepository: VehiclesRepository
) {
    suspend operator fun invoke(vehicleId: String) : StatusVehiclesModel {
        return vehiclesRepository.getStatusDisableVehicles(vehicleId)
    }
}