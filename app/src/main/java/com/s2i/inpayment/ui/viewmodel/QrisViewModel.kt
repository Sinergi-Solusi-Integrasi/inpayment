package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.domain.entity.model.wallet.OrderQrisModel
import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.usecase.wallet.CreateQrisUseCase
import com.s2i.domain.usecase.wallet.OrderQueryQrisUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QrisViewModel(
    private val qrisUseCase: CreateQrisUseCase,
    private val orderQrisUseCase: OrderQueryQrisUseCase
) : ViewModel() {

    private val _qrisState = MutableStateFlow<QrisCreateModel?>(null)
    val qrisState : MutableStateFlow<QrisCreateModel?> = _qrisState

    private val _orderQrisState = MutableStateFlow<OrderQrisModel?>(null)
    val orderQrisState : MutableStateFlow<OrderQrisModel?> = _orderQrisState

    private val _error = MutableStateFlow<String?>(null)
    val error : MutableStateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading : MutableStateFlow<Boolean> = _loading


    fun sendQris(
        mid:String,
        tid:String,
        trxid:String,
        amount:String,
        waktu:String,
        signature:String,
        clientid:String
    ) {

        viewModelScope.launch {
            _loading.value = true
            try {
                val qris = qrisUseCase(mid,tid,trxid,amount,waktu,signature,clientid)
                _qrisState.value = qris
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun orderQuery(
        trxId: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val orderQris = orderQrisUseCase(trxId)
                // Perbarui state hanya jika data berubah
                // Buat instance baru untuk memicu pembaruan StateFlow
                val newOrderQrisState = OrderQrisModel(
                    rCode = orderQris.rCode,
                    message = orderQris.message,
                    trxId = orderQris.trxId
                )
                _orderQrisState.value = newOrderQrisState
                Log.d("QrisViewModel", "State diperbarui: $newOrderQrisState")
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }

    }

}