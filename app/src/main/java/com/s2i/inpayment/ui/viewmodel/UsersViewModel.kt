package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.balance.BalanceModel
import com.s2i.domain.entity.model.balance.DetailTrxModel
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.balance.IncomeExpenseModel
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.domain.entity.model.users.UsersProfileModel
import com.s2i.domain.usecase.users.GetUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val usersUseCase: GetUsersUseCase,
) : ViewModel() {

   private val _users = MutableStateFlow<ProfileModel?>(null)
    val users: StateFlow<ProfileModel?> = _users

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchUsers() {
        viewModelScope.launch {
            _loading.value = true
            try{
                val result = usersUseCase()
                _users.value = result.data
                Log.d("UsersViewModel Data", result.data.toString())
            } catch (e: Exception){
                _error.value = e.message
                Log.e("UsersViewModel Error", e.message ?: "Unknown error")
            } finally {
                _loading.value = false
            }

        }
    }
}