package com.s2i.inpayment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.domain.entity.model.wallet.QrisCreateModel
import com.s2i.domain.usecase.wallet.CreateQrisUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QrisViewModel(
    private val qrisUseCase: CreateQrisUseCase
) : ViewModel() {

    private val _qrisState = MutableStateFlow<QrisCreateModel?>(null)
    val qrisState : MutableStateFlow<QrisCreateModel?> = _qrisState

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

}