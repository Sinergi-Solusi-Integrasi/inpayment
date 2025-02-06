package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.LoansVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class PullLoansVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {
    suspend operator fun invoke(vehicleId: String): LoansVehiclesModel {
        return vehiclesRepository.returnVehiclesLoans(vehicleId)
    }

}