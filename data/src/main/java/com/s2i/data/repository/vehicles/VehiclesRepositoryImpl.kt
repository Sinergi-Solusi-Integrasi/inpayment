package com.s2i.data.repository.vehicles

import android.util.Log
import com.s2i.data.model.users.BlobImageData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.request.vehicle.VehiclesAddRequest
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.AgencyCardModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.SelectedVehicleModel
import com.s2i.domain.entity.model.vehicle.StatusVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.repository.vehicles.VehiclesRepository

class VehiclesRepositoryImpl(
    private val apiServices: ApiServices

) : VehiclesRepository {

    override suspend fun getVehicles(): GetVehiclesModel {
        val response = apiServices.vehicles()
        val responseData = response.data

        // vehicles data
        val vehicles = responseData.map { vehicleData ->
            VehicleModel(
                vehicleId = vehicleData.vehicleId,
                ownserUserId = vehicleData.ownserUserId,
                borrowerUserId = vehicleData.borrowerUserId,
                brand = vehicleData.brand,
                model = vehicleData.model,
                varian = vehicleData.varian,
                color = vehicleData.color,
                plateNumber = vehicleData.plateNumber,
                group = vehicleData.group,
                rfid = vehicleData.rfid,
                priority = vehicleData.priority,
                images = vehicleData.images,
                certificateImage = vehicleData.certificateImage,
                loanExpiredAt = vehicleData.loanExpiredAt,
                loanedAt = vehicleData.loanedAt,
                createdAt = vehicleData.createdAt,
                updatedAt = vehicleData.updatedAt,
                isOwner = vehicleData.isOwner,
                isLoaned = vehicleData.isLoaned,
                status = vehicleData.status,
                agencyCard = vehicleData.agencyCard?.let { agencyCardData ->
                    AgencyCardModel(
                        cardNumber = agencyCardData.cardNumber,
                        embossId = agencyCardData.embossId,
                        expiredAt = agencyCardData.expiredAt
                    )
                }?: AgencyCardModel("", "", "")
            )
        }


        return GetVehiclesModel(
            code = response.code,
            message = response.message,
            data = vehicles
        )

    }

    // Regist Vehicles
    override suspend fun registVehicles(
        brand: String,
        model: String,
        varian: String,
        color: String,
        type: String,
        plateNumber: String,
        documentImage: BlobImageModel,
        vehicleImages: List<BlobImageModel>
    ): RegisVehiclesModel {

        // Document Image
        val documentImageData = BlobImageData(
            data = "data:${documentImage.mimeType};base64,${documentImage.data}",
            ext = documentImage.ext,
            mimeType = documentImage.mimeType
        )

        // Vehicle Images
        val vehicleImageDataList = vehicleImages.map { vehicleImage ->
            BlobImageData(
                data = "data:${vehicleImage.mimeType};base64,${vehicleImage.data}",
                ext = vehicleImage.ext,
                mimeType = vehicleImage.mimeType
            )
        }

        val registVehicles = VehiclesAddRequest(
            brand = brand,
            model = model,
            varian = varian,
            color = color,
            type = type,
            plateNumber = plateNumber,
            documentImage = documentImageData,
            vehicleImages = vehicleImageDataList
        )

        return try {
            val response = apiServices.addVehicles(registVehicles)
            RegisVehiclesModel(
                code = response.code,
                message = response.message,
                vehiclesData = VehicleModel(
                    nameVehicles = response.vehiclesData.nameVehicles,
                    color = response.vehiclesData.color,
                    plateNumber = response.vehiclesData.plateNumber,
                    status = response.vehiclesData.status
                )

            )
        } catch (e: Exception) {
            Log.e("VehiclesRepositoryImpl", "Error while regist: ${e.message}")
            throw Exception("Error while regist: ${e.message}")

        }
    }

    override suspend fun getStatusEnableVehicles(vehicleId: String): StatusVehiclesModel {
        val response = apiServices.vehiclesEnable(vehicleId)
        val responseData = response.data


        return responseData.let{
            val vehicles = SelectedVehicleModel(
                vehicleId = responseData.vehicleId,
                status = responseData.status,
            )
            StatusVehiclesModel(
                code = response.code,
                message = response.message,
                data = vehicles
            )
        }
    }

    override suspend fun getStatusDisableVehicles(vehicleId: String): StatusVehiclesModel {
        val response = apiServices.vehiclesDisable(vehicleId)
        val responseData = response.data


        return responseData.let{
            val vehicles = SelectedVehicleModel(
                vehicleId = responseData.vehicleId,
                status = responseData.status,
            )
            StatusVehiclesModel(
                code = response.code,
                message = response.message,
                data = vehicles
            )
        }
    }
}