package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.domain.entity.model.vehicle.GetVehiclesModel
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.domain.usecase.vehicles.GetVehiclesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehiclesViewModel(
    private val vehiclesUseCase: GetVehiclesUseCase,
) : ViewModel() {

    private val _getVehiclesState = MutableStateFlow<List<VehicleModel>>(emptyList())
    val getVehiclesState: MutableStateFlow<List<VehicleModel>> = _getVehiclesState

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
}


