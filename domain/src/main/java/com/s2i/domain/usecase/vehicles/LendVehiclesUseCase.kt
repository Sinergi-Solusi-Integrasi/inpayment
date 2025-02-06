package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.LendVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class LendVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {

    suspend operator fun invoke(
        vehicleId: String,
        toAccountNumber: String,
        expiredAt: String
    ): LendVehiclesModel {
        return vehiclesRepository.lendVehicles(
            vehicleId,
            toAccountNumber,
            expiredAt
        )
    }
}