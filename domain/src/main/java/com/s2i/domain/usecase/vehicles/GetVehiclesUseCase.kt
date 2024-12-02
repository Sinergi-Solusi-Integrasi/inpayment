package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class GetVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {

    suspend operator fun invoke() : GetVehiclesModel {
        return vehiclesRepository.getVehicles()
    }
}