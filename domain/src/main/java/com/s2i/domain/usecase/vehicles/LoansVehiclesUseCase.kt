package com.s2i.domain.usecase.vehicles

import com.s2i.domain.entity.model.vehicle.LoansVehiclesModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class LoansVehiclesUseCase(
    private val vehiclesRepository: VehiclesRepository
) {

    suspend operator fun invoke(token: String): LoansVehiclesModel {
        return vehiclesRepository.loansVehicles(token)
    }

}