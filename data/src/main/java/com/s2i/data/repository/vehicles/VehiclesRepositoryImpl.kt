package com.s2i.data.repository.vehicles

import android.util.Log
import com.s2i.data.model.users.BlobImageData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.request.vehicle.ChangeVehiclesRequest
import com.s2i.data.remote.request.vehicle.LendVehiclesRequest
import com.s2i.data.remote.request.vehicle.LoansVehiclesRequest
import com.s2i.data.remote.request.vehicle.VehiclesAddRequest
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.TopUpModel
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.AgencyCardModel
import com.s2i.domain.entity.model.vehicle.ChangeVehiclesModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.LendVehiclesModel
import com.s2i.domain.entity.model.vehicle.LoansVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.SelectedVehicleModel
import com.s2i.domain.entity.model.vehicle.StatusVehiclesModel
import com.s2i.domain.entity.model.vehicle.TokenVehiclesModel
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
                ownerUserId = vehicleData.ownerUserId,
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
            ext = documentImage.ext,
            mimeType = documentImage.mimeType,
            data = "data:${documentImage.mimeType};base64,${documentImage.data}"
        )

        // Vehicle Images
        val vehicleImageDataList = vehicleImages.map { vehicleImage ->
            BlobImageData(
                ext = vehicleImage.ext,
                mimeType = vehicleImage.mimeType,
                data = "data:${vehicleImage.mimeType};base64,${vehicleImage.data}"
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

    override suspend fun lendVehicles(
        vehicleId: String,
        toAccountNumber: String,
        expiredAt: String
    ) : LendVehiclesModel {

        val lendVehicles = LendVehiclesRequest(
            toAccountNumber = toAccountNumber,
            expiredAt = expiredAt
        )

        return try {
            val response = apiServices.vehiclesLend(vehicleId, lendVehicles)
            val responseData = response.data
            LendVehiclesModel(
                code = response.code,
                message = response.message,
                data = SelectedVehicleModel(
                    vehicleId = responseData.vehicleId,
                    loanAccountNumber = responseData.loanAccountNumber,
                    loanUserId = responseData.loanUserId,
                    loanExpiredAt = responseData.loanExpiredAt,
                    token = responseData.token?.let { tokenData ->
                        TokenVehiclesModel(
                            token = tokenData.token,
                            expiredAt = tokenData.expiredAt
                        )
                    }

                )
            )

        } catch (e: Exception) {
            Log.e("VehiclesRepositoryImpl", "Error while regist: ${e.message}")
            throw Exception("Error while regist: ${e.message}")
        }
    }

    override suspend fun returnVehiclesLoans(vehicleId: String): LoansVehiclesModel {
        val response = apiServices.returnLoans(vehicleId)
        val responseData = response.data

        return responseData.let {
            val returnVehicles = VehicleModel(
                vehicleId = responseData.vehicleId,
                ownerUserId = responseData.ownerUserId,
                borrowerUserId = responseData.borrowerUserId,
                brand = responseData.brand,
                model = responseData.model,
                varian = responseData.varian,
                color = responseData.color,
                plateNumber = responseData.plateNumber,
                group = responseData.group,
                rfid = responseData.rfid,
                priority = responseData.priority,
                images = responseData.images,
                certificateImage = responseData.certificateImage,
                loanExpiredAt = responseData.loanExpiredAt,
                loanedAt = responseData.loanedAt,
                createdAt = responseData.createdAt,
                updatedAt = responseData.updatedAt,
                isOwner = responseData.isOwner,
                isLoaned = responseData.isLoaned,
                status = responseData.status,
            )

            LoansVehiclesModel(
                code = response.code,
                message = response.message,
                data = returnVehicles
            )
        }
    }

    override suspend fun pullVehiclesLoans(vehicleId: String): LoansVehiclesModel {
        val response = apiServices.pullLoans(vehicleId)
        val responseData = response.data

        return responseData.let {
            val returnVehicles = VehicleModel(
                vehicleId = responseData.vehicleId,
                ownerUserId = responseData.ownerUserId,
                borrowerUserId = responseData.borrowerUserId,
                brand = responseData.brand,
                model = responseData.model,
                varian = responseData.varian,
                color = responseData.color,
                plateNumber = responseData.plateNumber,
                group = responseData.group,
                rfid = responseData.rfid,
                priority = responseData.priority,
                images = responseData.images,
                certificateImage = responseData.certificateImage,
                loanExpiredAt = responseData.loanExpiredAt,
                loanedAt = responseData.loanedAt,
                createdAt = responseData.createdAt,
                updatedAt = responseData.updatedAt,
                isOwner = responseData.isOwner,
                isLoaned = responseData.isLoaned,
                status = responseData.status,
            )

            LoansVehiclesModel(
                code = response.code,
                message = response.message,
                data = returnVehicles
            )
        }
    }

    override suspend fun loansVehicles(
        token: String
    ) : LoansVehiclesModel {

        val loansVehicles = LoansVehiclesRequest(
            token = token
        )

        return try {
            val response = apiServices.loansVehicles(loansVehicles)
            val responseData = response.data
            LoansVehiclesModel(
                code = response.code,
                message = response.message,
                data = VehicleModel(
                    vehicleId = responseData.vehicleId,
                    ownerUserId = responseData.ownerUserId,
                    borrowerUserId = responseData.borrowerUserId,
                    brand = responseData.brand,
                    model = responseData.model,
                    varian = responseData.varian,
                    color = responseData.color,
                    plateNumber = responseData.plateNumber,
                    group = responseData.group,
                    rfid = responseData.rfid,
                    priority = responseData.priority,
                    images = responseData.images,
                    certificateImage = responseData.certificateImage,
                    loanExpiredAt = responseData.loanExpiredAt,
                    loanedAt = responseData.loanedAt,
                    createdAt = responseData.createdAt,
                    updatedAt = responseData.updatedAt,
                    isOwner = responseData.isOwner,
                    isLoaned = responseData.isLoaned,
                    status = responseData.status,

                )
            )

        } catch (e: Exception) {
            Log.e("VehiclesRepositoryImpl", "Error while regist: ${e.message}")
            throw Exception("Error while regist: ${e.message}")
        }
    }

    override suspend fun changeVehicles(vehicleId: String): ChangeVehiclesModel {

        val changesVehicles = ChangeVehiclesRequest(
            vehicleId = vehicleId
        )
//        val response = apiServices.vehiclesSwitchSelected(ChangeVehiclesRequest(vehicleId))

        return try {
            val response = apiServices.vehiclesSwitchSelected(changesVehicles)
            val responseData = response.data
            ChangeVehiclesModel(
                code = response.code,
                message = response.message,
                data = VehicleModel(
                    vehicleId = responseData.vehicleId,
                    ownerUserId = responseData.ownerUserId,
                    borrowerUserId = responseData.borrowerUserId,
                    brand = responseData.brand,
                    model = responseData.model,
                    varian = responseData.varian,
                    color = responseData.color,
                    plateNumber = responseData.plateNumber,
                    group = responseData.group,
                    rfid = responseData.rfid,
                    priority = responseData.priority,
                    images = responseData.images,
                    certificateImage = responseData.certificateImage,
                    loanExpiredAt = responseData.loanExpiredAt,
                    loanedAt = responseData.loanedAt,
                    createdAt = responseData.createdAt,
                    updatedAt = responseData.updatedAt,
                    isOwner = responseData.isOwner,
                    isLoaned = responseData.isLoaned,
                    status = responseData.status,
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