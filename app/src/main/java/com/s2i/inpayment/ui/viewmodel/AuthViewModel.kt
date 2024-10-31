package com.s2i.inpayment.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager
): ViewModel(){
    private val _loginState = MutableLiveData<Result<AuthModel>?>(null)
    val loginState: MutableLiveData<Result<AuthModel>?> = _loginState

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> = _loadingState

    fun login(username: String, password: String){
        _loadingState.value = true // Start loading
        viewModelScope.launch {
            val result = loginUseCase(username, password)
            _loginState.value = result
            _loadingState.value = false
            result.fold(
                onSuccess = {
                    authModel ->
                    sessionManager.createLoginSession(
                        accessToken = authModel.accessToken,
                        refreshToken = authModel.refreshToken,
                        accessTokenExpiry = authModel.accessTokenExpiredAt,
                        refreshTokenExpiry = authModel.refreshTokenExpiredAt,
                        username = authModel.username
                    )
                },
                onFailure = { throwable ->
                    _loginState.value = Result.failure(throwable)
                }
            )
        }
    }

    // Function to check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sessionManager.isLogin
    }

    fun logout() {
        sessionManager.logout()
    }
}