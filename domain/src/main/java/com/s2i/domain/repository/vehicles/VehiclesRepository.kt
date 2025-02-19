package com.s2i.domain.repository.vehicles

import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.ChangeVehiclesModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.LendVehiclesModel
import com.s2i.domain.entity.model.vehicle.LoansVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.StatusVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel

interface VehiclesRepository {


    // register vehicles
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

    //fetch vehicles
    suspend fun getVehicles(): GetVehiclesModel

    //enable vehicles
    suspend fun getStatusEnableVehicles(vehicleId: String): StatusVehiclesModel

    //disable vehicles
    suspend fun getStatusDisableVehicles(vehicleId: String): StatusVehiclesModel

    // lends vehicles
    suspend fun lendVehicles(vehicleId: String, toAccountNumber: String, expiredAt: String): LendVehiclesModel

    // loans vehicles
    suspend fun loansVehicles(token: String): LoansVehiclesModel

    // change vehicles
    suspend fun changeVehicles(vehicleId: String): ChangeVehiclesModel

    // return vehicles loans
    suspend fun returnVehiclesLoans(vehicleId: String): LoansVehiclesModel
}