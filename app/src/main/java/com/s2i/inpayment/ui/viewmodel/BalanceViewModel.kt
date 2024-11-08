package com.s2i.inpayment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import com.s2i.domain.usecase.balance.GetBalanceUseCase
import com.s2i.domain.usecase.balance.GetInOutBalanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BalanceViewModel(
    private val balanceUseCase: GetBalanceUseCase,
    private val inOutBalanceUseCase: GetInOutBalanceUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _balance = MutableStateFlow<BalanceModel?>(null)
    val balance: StateFlow<BalanceModel?> = _balance

    private val _triLastTransaction = MutableStateFlow<List<HistoryBalanceModel>>(emptyList())
    val triLastTransaction: StateFlow<List<HistoryBalanceModel>> = _triLastTransaction

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchBalance() {
        viewModelScope.launch {
            _loading.value = true
//            val token = sessionManager.accessToken ?: return@launch
            try {
                _balance.value = balanceUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchTriLastTransaction() {
        viewModelScope.launch {
            _loading.value = true

            try{
                val inOutBalance = inOutBalanceUseCase()
                _triLastTransaction.value = inOutBalance.history.takeLast(3)
            } catch (e: Exception){
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}