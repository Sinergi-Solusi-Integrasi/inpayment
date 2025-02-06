package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.ChangeVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class ChangeVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {

    suspend operator fun invoke(vehicleId: String): ChangeVehiclesModel {
        return vehiclesRepository.changeVehicles(vehicleId)
    }

}