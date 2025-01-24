package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.RegisVehiclesModel
import com.s2i.domain.entity.model.vehicle.SelectedVehicleModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.usecase.vehicles.EnableStatusUseCase
import com.s2i.domain.usecase.vehicles.GetDisableStatusUseCase
import com.s2i.domain.usecase.vehicles.GetVehiclesUseCase
import com.s2i.domain.usecase.vehicles.RegistVehiclesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehiclesViewModel(
    private val registUseCase: RegistVehiclesUseCase,
    private val vehiclesUseCase: GetVehiclesUseCase,
    private val enableUseCase: EnableStatusUseCase,
    private val disableUseCase: GetDisableStatusUseCase,
) : ViewModel() {

    private val _getVehiclesState = MutableStateFlow<List<VehicleModel>>(emptyList())
    val getVehiclesState: MutableStateFlow<List<VehicleModel>> = _getVehiclesState

    private val _registVehicles = MutableStateFlow<RegisVehiclesModel?>(null)
    val registVehicles: MutableStateFlow<RegisVehiclesModel?> = _registVehicles

    private val _enableVehiclesState = MutableStateFlow<SelectedVehicleModel?>(null)
    val enableVehiclesState: MutableStateFlow<SelectedVehicleModel?> = _enableVehiclesState

    private val _disableVehiclesState = MutableStateFlow<SelectedVehicleModel?>(null)
    val disableVehiclesState: MutableStateFlow<SelectedVehicleModel?> = _disableVehiclesState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

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
        documentImage: BlobImageModel,
        vehicleImages: List<BlobImageModel>
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
//                val result = registUseCase()
//                _registVehicles.value = result.data
//                Log.d("VehiclesViewModel", "Fetched Vehicles: $result")
//                Log.d("VehiclesViewModel", "Fetched Vehicles: ${result.data}")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("VehiclesViewModel", "Error fetching vehicles: ${e.message}")
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
}


