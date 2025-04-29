package com.s2i.inpayment.ui.viewmodel

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.common.utils.convert.ImageCompressor
import com.s2i.common.utils.convert.bitmapToBase64
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.compressBitmap
import com.s2i.data.model.vehicles.LoansTokenVehiclesData
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.ChangeVehiclesModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.LendVehiclesModel
import com.s2i.domain.entity.model.vehicle.LoansVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.SelectedVehicleModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.usecase.vehicles.ChangeVehiclesUseCase
import com.s2i.domain.usecase.vehicles.EnableStatusUseCase
import com.s2i.domain.usecase.vehicles.GetDisableStatusUseCase
import com.s2i.domain.usecase.vehicles.GetVehiclesUseCase
import com.s2i.domain.usecase.vehicles.LendVehiclesUseCase
import com.s2i.domain.usecase.vehicles.LoansVehiclesUseCase
import com.s2i.domain.usecase.vehicles.PullLoansVehiclesUseCase
import com.s2i.domain.usecase.vehicles.RegistVehiclesUseCase
import com.s2i.domain.usecase.vehicles.ReturnLoansVehiclesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

class VehiclesViewModel(
    private val registUseCase: RegistVehiclesUseCase,
    private val vehiclesUseCase: GetVehiclesUseCase,
    private val enableUseCase: EnableStatusUseCase,
    private val disableUseCase: GetDisableStatusUseCase,
    private val changeUseCase: ChangeVehiclesUseCase,
    private val lendUseCase: LendVehiclesUseCase,
    private val loansUseCase: LoansVehiclesUseCase,
    private val pullUseCase: PullLoansVehiclesUseCase,
    private val returnUseCase: ReturnLoansVehiclesUseCase

    ) : ViewModel() {

    private val _getVehiclesState = MutableStateFlow<List<VehicleModel>>(emptyList())
    val getVehiclesState: MutableStateFlow<List<VehicleModel>> = _getVehiclesState

    private val _registVehicles = MutableStateFlow<RegisVehiclesModel?>(null)
    val registVehicles: MutableStateFlow<RegisVehiclesModel?> = _registVehicles

    private val _enableVehiclesState = MutableStateFlow<SelectedVehicleModel?>(null)
    val enableVehiclesState: MutableStateFlow<SelectedVehicleModel?> = _enableVehiclesState

    private val _disableVehiclesState = MutableStateFlow<SelectedVehicleModel?>(null)
    val disableVehiclesState: MutableStateFlow<SelectedVehicleModel?> = _disableVehiclesState

    private val _changeVehiclesState = MutableStateFlow<ChangeVehiclesModel?>(null)
    val changeVehiclesState: MutableStateFlow<ChangeVehiclesModel?> = _changeVehiclesState

    private val _lendVehiclesState = MutableStateFlow<LendVehiclesModel?>(null)
    val lendVehiclesState: MutableStateFlow<LendVehiclesModel?> = _lendVehiclesState

    private val _loansVehiclesState = MutableStateFlow<LoansVehiclesModel?>(null)
    val loansVehiclesState: MutableStateFlow<LoansVehiclesModel?> = _loansVehiclesState

    private val _returnLoansVehiclesState = MutableStateFlow<LoansVehiclesModel?>(null)
    val returnLoansVehiclesState: MutableStateFlow<LoansVehiclesModel?> = _returnLoansVehiclesState

    private val _pullLoansVehiclesState = MutableStateFlow<LoansVehiclesModel?>(null)
    val pullLoansVehiclesState: MutableStateFlow<LoansVehiclesModel?> = _pullLoansVehiclesState

    private val _docImageVehiclesState = MutableStateFlow<BlobImageModel?>(null)
    val docImageVehiclesState: MutableStateFlow<BlobImageModel?> = _docImageVehiclesState

    private val _vehicleOCRState = MutableStateFlow<Map<String, String>>(emptyMap())
    val vehicleOCRState: StateFlow<Map<String, String>> = _vehicleOCRState

    private val _vehicleImageVehiclesState = MutableStateFlow<List<BlobImageModel>?>(emptyList())
    val vehicleImageVehiclesState: MutableStateFlow<List<BlobImageModel>?> = _vehicleImageVehiclesState

    private val _vehicleUrisState = MutableStateFlow<List<String>>(emptyList()) // Tambahkan untuk menyimpan URI
    val vehicleUrisState: MutableStateFlow<List<String>> = _vehicleUrisState

    private val _vehiclesTokenMap = mutableMapOf<String, String>()
    // Keep track of token expiration times
    private val tokenExpirationTimes = mutableMapOf<String, String>()
    private var lastVehiclesId: String? = null
    private fun saveTokenForVehicles(vehicleId: String, token: String, expiredAt: String?){
        _vehiclesTokenMap[vehicleId] = token
        if (expiredAt != null){
            tokenExpirationTimes[vehicleId] = expiredAt
        }
        Log.d("VehiclesViewModel", "Token saved for vehicle $vehicleId: $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTokenExpired(expiryTimeStr: String?): Boolean {
        if (expiryTimeStr == null) return true

        return try {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val expiryTime = OffsetDateTime.parse(expiryTimeStr, formatter)
            OffsetDateTime.now().isAfter(expiryTime)
        } catch (e: Exception) {
            true
        }
    }

    // Check if token for a specific vehicle is expired
    @RequiresApi(Build.VERSION_CODES.O)
    fun isTokenForVehicleExpired(vehicleId: String): Boolean {
        val expiryTimeStr = tokenExpirationTimes[vehicleId] ?: return true
        return isTokenExpired(expiryTimeStr)
    }

    fun getTokenForVehicles(vehicleId: String): String?{
        return _vehiclesTokenMap[vehicleId]
    }
    // Get expiration time for a vehicle token
    fun getTokenExpirationTime(vehicleId: String): String? {
        return tokenExpirationTimes[vehicleId]
    }

    fun hasTokenForVehicles(vehicleId: String): Boolean{
        return _vehiclesTokenMap.containsKey(vehicleId)
    }

    fun handleVehiclesChange(newVehicleId: String) {
        if (lastVehiclesId != null && lastVehiclesId != newVehicleId) {
            // We're switching to a different vehicle, clear the state for the new vehicle ID
            // Notice we're NOT clearing the token here, just resetting the UI state
            _lendVehiclesState.value = null
            _error.value = null
        }

        // Update the last vehicle ID
        lastVehiclesId = newVehicleId
    }

    fun clearTokenForVehicles(vehicleId: String) {
        _vehiclesTokenMap.remove(vehicleId)
        tokenExpirationTimes.remove(vehicleId)

        // Reset lending state
        _lendVehiclesState.value = null
        _error.value = null
    }

    fun clearAllTokens(){
        _vehiclesTokenMap.clear()
        Log.d("VehiclesViewModel", "All tokens cleared")
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // doc image
    fun setDocImage(docImage: BlobImageModel) {
        _docImageVehiclesState.value = docImage
    }

    fun setVehicleOCRData(plateNumber: String, brand: String, type: String, model: String, color: String) {
        val ocrData = mapOf(
            "plateNumber" to plateNumber,
            "brand" to brand,
            "type" to type,
            "model" to model,
            "color" to color
        )
        _vehicleOCRState.value = ocrData
        Log.d("VehiclesViewModel", "✅ OCR Data Saved: $ocrData")
    }

    fun setVehicleImage(vehicleImage: List<BlobImageModel>) {
        _vehicleImageVehiclesState.value = vehicleImage.take(4) // Batasi hanya 4 gambar

        Log.d("VehiclesViewModel", "✅ setVehicleImage called: ${vehicleImage.size} images received")
        Log.d("VehiclesViewModel", "🔍 Updated vehicle images state: ${_vehicleImageVehiclesState.value?.size} images")
    }

    fun fetchVehicleImages() {
        try {
            // Ambil data gambar kendaraan dan dokumen
            val vehicleImages = _vehicleImageVehiclesState.value?.take(4) ?: emptyList()
            val docImage = _docImageVehiclesState.value

            // Perbarui state dengan data terbaru
            _vehicleImageVehiclesState.value = vehicleImages
            _docImageVehiclesState.value = docImage

            Log.d("VehiclesViewModel", "✅ fetchVehicleImages() berhasil dieksekusi")
            Log.d("VehiclesViewModel", "📷 Total gambar kendaraan: ${vehicleImages.size}")
            Log.d("VehiclesViewModel", "📄 Dokumen kendaraan tersedia: ${docImage != null}")

        } catch (e: Exception) {
            Log.e("VehiclesViewModel", "❌ fetchVehicleImages() gagal: ${e.message}", e)
        }
    }


    fun clearVehicleData() {
        _docImageVehiclesState.value = null
        _vehicleImageVehiclesState.value = emptyList()
        _vehicleUrisState.value = emptyList()
        _vehicleOCRState.value = emptyMap()

        Log.d("VehiclesViewModel", "🧹 Data kendaraan berhasil dihapus setelah submit.")
    }


    // ✅ Simpan Gambar Kendaraan dalam format Base64 (List)
    fun addVehicleImage(bitmap: Bitmap, mimeType: String, ext: String) {
        val base64Data = bitmapToBase64(bitmap)
        val newImage = BlobImageModel(
            data = base64Data,
            mimeType = mimeType,
            ext = ext
        )
        val updatedList = (_vehicleImageVehiclesState.value?.plus(newImage))?.take(4)
        _vehicleImageVehiclesState.value = updatedList
//        val updatedList = (_vehicleImageVehiclesState.value ?: emptyList()) + vehicleImage
//        _vehicleImageVehiclesState.value = updatedList.take(4)

    }

    fun setVehicleUri(imageUris: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val filteredUris = imageUris.take(4)
                _vehicleUrisState.emit(filteredUris) // Gunakan emit untuk update state dengan benar

                Log.d("VehiclesViewModel", "✅ URIs set: ${_vehicleUrisState.value}")
            } catch (e: Exception) {
                Log.e("VehiclesViewModel", "❌ Error setting URIs: ${e.message}")

            } finally {
                _loading.value = false
            }
        }
    }

    // get vehicles
    fun fetchVehicles() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = vehiclesUseCase()
                _getVehiclesState.value = result.data
                Log.d("VehiclesViewModel", "Fetched Vehicles: $result")
                Log.d("VehiclesViewModel", "Fetched Vehicles: ${result.data}")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModel", "Error fetching vehicles: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // regist vehicles

    fun registVehicles(
        brand: String,
        model: String,
        varian: String,
        color: String,
        type: String,
        plateNumber: String,
        documentBitmap: Bitmap,
        vehicleBitmaps: List<Bitmap>,
        imageFormat: Bitmap.CompressFormat
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {

//                val (docBase64, docExt, docMimeType) = bitmapToBase64WithFormat(documentBitmap, imageFormat)
//                val documentImage = BlobImageModel(
//                    data = docBase64,  // Tambahkan format data URI
//                    ext = docExt,
//                    mimeType = docMimeType
//                )
//
//                // Konversi gambar kendaraan ke Base64 dan format yang sesuai
//                val vehicleImages = vehicleBitmaps.map { bitmap ->
//                    val (base64, ext, mimeType) = bitmapToBase64WithFormat(bitmap, imageFormat)
//                    BlobImageModel(
//                        data = base64,  // Pastikan format mengikuti aturan repository
//                        ext = ext,
//                        mimeType = mimeType
//                    )
//                }

                val compressedDocument = compressBitmap(documentBitmap, imageFormat, 75)
                val (docBase64, docExt, docMimeType) = bitmapToBase64WithFormat(compressedDocument, imageFormat)
                val documentImage = BlobImageModel(
                    data = docBase64,
                    ext = docExt,
                    mimeType = docMimeType
                )

                // Kompresi gambar kendaraan
                val vehicleImages = vehicleBitmaps.map { bitmap ->
                    val compressedBitmap = compressBitmap(bitmap, imageFormat, 75)
                    val (base64, ext, mimeType) = bitmapToBase64WithFormat(compressedBitmap, imageFormat)
                    BlobImageModel(
                        data = base64,
                        ext = ext,
                        mimeType = mimeType
                    )
                }

                Log.d("VehiclesViewModel", "📄 Document size: ${docBase64.length / 1024} KB")
                vehicleImages.forEachIndexed { index, image ->
                    Log.d("VehiclesViewModel", "📸 Vehicle Image $index size: ${image.data.length / 1024} KB")
                }

                Log.d("VehiclesViewModel", "📄 Document converted: ${documentImage.ext}, ${documentImage.mimeType}")
                Log.d("VehiclesViewModel", "📸 Total vehicle images converted: ${vehicleImages.size}")

                // Kirim data ke repository
                val result = registUseCase(
                    brand = brand,
                    model = model,
                    varian = varian,
                    color = color,
                    type = type,
                    plateNumber = plateNumber,
                    documentImage = documentImage,
                    vehicleImages = vehicleImages
                )

                _registVehicles.value = result

                Log.d("VehiclesViewModel", "✅ Vehicles registered successfully: $result")
                clearVehicleData() // Bersihkan data setelah registrasi berhasil
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModel", "❌ Error registering vehicles: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    //Lend Vehicles
    fun lendVehicles(
        vehicleId: String,
        toAccountNumber: String,
        expiredAt: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = lendUseCase(
                    vehicleId = vehicleId,
                    toAccountNumber = toAccountNumber,
                    expiredAt = expiredAt
                )
                Log.d("VehiclesViewModel", "Lend vehicles success: ${result.message}")
                // Save token with vehiclesid if success
                result.data?.token?.token?.let { token ->
                    saveTokenForVehicles(vehicleId, token, expiredAt)
                }

                _lendVehiclesState.value = result
            } catch (e: Exception) {
                Log.e("VehiclesViewModel", "Error lend vehicles: ${e.message}")
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetLendState() {
        _lendVehiclesState.value = null
        _error.value = null
    }

    // loans vehicles
    fun loansVehicles(
        token: String
    ) {
        viewModelScope.launch{
            _loading.value = true
            try {
                val result = loansUseCase(
                    token = token
                )
                Log.d("VehiclesViewModel", "Loans vehicles success: ${result.message}")
                _loansVehiclesState.value = result
            } catch (e: Exception) {
                Log.e("VehiclesViewModel", "Error loans vehicles: ${e.message}")
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // return loans
    fun returnsLoans(
        vehicleId: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = returnUseCase(
                    vehicleId = vehicleId
                )
                Log.d("VehiclesViewModel", "Return loans success: ${result.message}")
                _pullLoansVehiclesState.value = result
            } catch (e: Exception) {
                Log.e("VehiclesViewModel", "Error return loans: ${e.message}")
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // pull loans
    fun pullsLoans(
        vehicleId: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = pullUseCase(
                    vehicleId = vehicleId
                )
                Log.d("VehiclesViewModel", "Pulls loans success: ${result.message}")
                _pullLoansVehiclesState.value = result
            } catch (e: Exception) {
                Log.e("VehiclesViewModel", "Error pull loans: ${e.message}")
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Change Vehicles
    fun changeVehicles(
        vehicleId: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = changeUseCase(vehicleId)
                _changeVehiclesState.value = result
                Log.d("VehiclesViewModel", "Vehicle: $result changed successfully")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModel", "Error Changes vehicles: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // Update status kendaraan setelah berhasil enable/disable
    private fun updateVehicleStatus(vehicleId: String, status: String) {
        _getVehiclesState.value = _getVehiclesState.value.map { vehicle ->
            if (vehicle.vehicleId == vehicleId) {
                vehicle.copy(status = status)
            } else {
                vehicle
            }
        }
    }


    // put enable vehicles
    fun enableVehicles(vehicleId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = enableUseCase(vehicleId)
                _enableVehiclesState.value = result.data
                updateVehicleStatus(vehicleId, "ACTIVE")
                Log.d("VehiclesViewModelEnable", "Fetched Vehicles: $result")
                Log.d("VehiclesViewModelEnable", "Fetched Vehicles: ${result.data}")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModelEnable", "Error fetching vehicles: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // put disable vehicles
    fun disableVehicles(vehicleId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = disableUseCase(vehicleId)
                _disableVehiclesState.value = result.data
                updateVehicleStatus(vehicleId, "INACTIVE")
                Log.d("VehiclesViewModelDisable", "Fetched Vehicles: $result")
                Log.d("VehiclesViewModelDisable", "Fetched Vehicles: ${result.data}")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModelDisable", "Error fetching vehicles: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}


