package com.s2i.data.repository.users

import android.util.Log
import com.s2i.data.remote.client.ApiServices
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.domain.entity.model.users.UsersProfileModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.repository.users.UsersProfileRepository

class UsersProfileRepositoryImpl(
    private val apiServices: ApiServices

) : UsersProfileRepository {

    override suspend fun getUsersProfile(): UsersProfileModel {
        val response = apiServices.profile()
        Log.d("API Response", response.toString())
        val responseData = response.data

        return UsersProfileModel(
            code = response.code,
            message = response.message,
            data = ProfileModel(
                userId = responseData.userId,
                username = responseData.username,
                name = responseData.name,
                accountNumber = responseData.accountNumber,
                email = responseData.email,
                mobileNumber = responseData.mobileNumber,
                address = responseData.address,
                selectVehicle = responseData.selectVehicle?.let { vehicles ->
                    VehicleModel(
                        vehicleId = vehicles.vehicleId,
                        ownserUserId = vehicles.ownserUserId,
                        borrowerUserId = vehicles.borrowerUserId,
                        brand = vehicles.brand,
                        model = vehicles.model,
                        varian = vehicles.varian,
                        color = vehicles.color,
                        plateNumber = vehicles.plateNumber,
                        group = vehicles.group,
                        rfid = vehicles.rfid,
                        priority = vehicles.priority,
                        loanExpiredAt = vehicles.loanExpiredAt,
                        loanedAt = vehicles.loanedAt,
                        images = vehicles.images,
                        isLoaned = vehicles.isLoaned,
                        isOwner = vehicles.isOwner,
                        status = vehicles.status,
                        createdAt = vehicles.createdAt,
                        updatedAt = vehicles.updatedAt,
                    )
                } ?: VehicleModel(
                    vehicleId = "",
                    ownserUserId = "",
                    borrowerUserId = null,
                    brand = "",
                    model = "",
                    varian = "",
                    color = "",
                    plateNumber = "",
                    group = 0,
                    rfid = "",
                    priority = 0,
                    images = emptyList(),
                    certificateImage = null,
                    loanExpiredAt = null,
                    loanedAt = null,
                    createdAt = "",
                    updatedAt = "",
                    isLoaned = false,
                    isOwner = false,
                    status = "",
                    agencyCard = null
                )
            )

        )
    }
}