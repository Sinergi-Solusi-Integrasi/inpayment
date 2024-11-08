package com.s2i.inpayment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.domain.entity.model.auth.TokenModel
import com.s2i.domain.usecase.auth.TokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TokenViewModel(
    private val tokenUseCase: TokenUseCase
) : ViewModel()  {

    // StateFlow untuk mengamati status token
    private val _tokenState = MutableStateFlow<TokenState>(TokenState.Valid)
    val tokenState: StateFlow<TokenState> = _tokenState

    init {
        // Mengecek token setiap kali ViewModel dibuat
        checkAndRefreshToken()
    }

    private fun checkAndRefreshToken(){
        viewModelScope.launch {
            val result = tokenUseCase.refreshAccessTokenIfNeeded()
            if (result.isSuccess){
                _tokenState.value = TokenState.Valid
            }else{
                _tokenState.value = TokenState.Expired
            }
        }
    }

    sealed class TokenState {
        object Valid : TokenState()
        object Expired : TokenState()
    }

}