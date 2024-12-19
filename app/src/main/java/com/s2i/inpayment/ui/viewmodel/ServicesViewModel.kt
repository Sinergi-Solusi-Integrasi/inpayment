package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.notification.services.BindingModel
import com.s2i.domain.usecase.notifications.services.ServicesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val servicesUseCase: ServicesUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _bindingState = MutableStateFlow<BindingModel?>(null)
    val bindingState: StateFlow<BindingModel?> get() = _bindingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> get() = _errorState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun bindAccount(deviceId: String) {
        Log.d("ServicesViewModel", "Starting bindAccount for deviceId: $deviceId")
        viewModelScope.launch {
            _loading.value = true
            try {
                val bindingModel = servicesUseCase(deviceId)
                Log.d("ServicesViewModel", "Account binding successful: ${bindingModel.message}")
                _bindingState.value = bindingModel
            } catch (e: Exception) {
                Log.e("ServicesViewModel", "Error during binding: ${e.message}")
                _errorState.value = e.message ?: "An error occurred during binding"
            } finally {
                _loading.value = false
            }
        }
    }
}
