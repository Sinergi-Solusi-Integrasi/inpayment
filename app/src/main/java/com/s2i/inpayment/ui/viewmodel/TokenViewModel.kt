package com.s2i.inpayment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.auth.TokenModel
import com.s2i.domain.usecase.auth.TokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TokenViewModel(
    private val tokenUseCase: TokenUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    // StateFlow untuk memantau token
    private val _tokenState = MutableStateFlow<TokenState>(TokenState.Valid)
    val tokenState: StateFlow<TokenState> = _tokenState

    init {
        // Mulai validasi session hanya jika user sudah login
        viewModelScope.launch {
            if (sessionManager.isLogin && sessionManager.accessToken != null) {
                validateSession()
            } else {
                Log.d("TokenViewModel", "Session not valid at startup. Logging out user.")
                sessionManager.logout()
                _tokenState.value = TokenState.Expired
            }
        }
    }

    private suspend fun validateSession() {
        Log.d("TokenViewModel", "Validating session. isLogin=${sessionManager.isLogin}")
        if (!sessionManager.isLogin) {
            _tokenState.value = TokenState.Expired
            Log.d("TokenViewModel", "User is not logged in. Setting token state to Expired.")
            return
        }
        if (sessionManager.isAccessTokenExpired()) {
            Log.d("TokenViewModel", "AccessToken expired. Attempting to refresh token.")
            checkAndRefreshToken()
            return
        }

        if (!sessionManager.accessToken.isNullOrEmpty()){
            _tokenState.value = TokenState.Valid
        } else{
            _tokenState.value = TokenState.Expired
        }
    }

    private suspend fun checkAndRefreshToken() {
        if (sessionManager.isLoggedOut) return // Skip jika sudah logout
        _tokenState.value = TokenState.Refreshing
        try {
            val result = tokenUseCase.refreshAccessTokenIfNeeded()
            if (result.isSuccess) {
                _tokenState.value = TokenState.Valid
            } else {
                sessionManager.logout()
                _tokenState.value = TokenState.Expired
            }
        } catch (e: Exception) {
            sessionManager.logout()
            _tokenState.value = TokenState.Expired
        }
    }

    sealed class TokenState {
        object Valid : TokenState()
        object Expired : TokenState()
        object Refreshing : TokenState()
        object Error : TokenState() // Untuk error sementara
    }
}

