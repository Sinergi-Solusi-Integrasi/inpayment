package com.s2i.inpayment.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.entity.model.users.UsersModel
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _loginState = MutableLiveData<Result<AuthModel>?>(null)
    val loginState: MutableLiveData<Result<AuthModel>?> = _loginState

    private val _registerState = MutableStateFlow<Result<UsersModel>?>(null)
    val registerState: MutableStateFlow<Result<UsersModel>?> = _registerState

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> = _loadingState

    private val _tokenState =
        MutableStateFlow<TokenViewModel.TokenState>(TokenViewModel.TokenState.Valid)
    val tokenState: StateFlow<TokenViewModel.TokenState> = _tokenState

    // Tambahkan variabel StateFlow untuk base64Data
    private val _base64Data = MutableStateFlow<String?>(null)
    val base64Data: StateFlow<String?> = _base64Data

    private val _identityBitmap = MutableStateFlow<Bitmap?>(null)
    val identityBitmap: StateFlow<Bitmap?> = _identityBitmap

    private val _imageFormat = MutableStateFlow<Bitmap.CompressFormat?>(null)
    val imageFormat: StateFlow<Bitmap.CompressFormat?> = _imageFormat

    private val _mimeType = MutableStateFlow<String?>(null)
    val mimeType: StateFlow<String?> = _mimeType

    private val _ext = MutableStateFlow<String?>(null)
    val ext: StateFlow<String?> = _ext

    // New function to fetch data
    fun fetchBitmap(): Bitmap? {
        return _identityBitmap.value
    }

    fun fetchImageFormat(): Bitmap.CompressFormat? {
        return _imageFormat.value
    }

    fun fetchMimeType(): String? {
        return _mimeType.value
    }

    fun fetchExt(): String? {
        return _ext.value
    }


    fun updateIdentityData(base64Data: String, bitmap: Bitmap, format: Bitmap.CompressFormat, mimeType: String, ext: String) {
        _base64Data.value = base64Data
        _identityBitmap.value = bitmap
        _imageFormat.value = format
        _mimeType.value = mimeType
        _ext.value = ext
        Log.d("AuthViewModel", "identityBitmap updated: ${_identityBitmap.value != null}")
        Log.d("AuthViewModel", "Bitmap: $bitmap, Format: $format, MIME: $mimeType, Base64Data: $base64Data")
    }

    fun clearIdentityData() {
        _base64Data.value = null
        _identityBitmap.value = null
        _imageFormat.value = null
        _mimeType.value = null
        _ext.value = null
    }


    fun login(username: String, password: String) {
        _loadingState.value = true // Start loading
        viewModelScope.launch {
            val result = loginUseCase(username, password)
            _loginState.value = result
            _loadingState.value = false
            result.fold(
                onSuccess = { authModel ->
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
                    _errorMessage.value = throwable.message ?: "Unknown error"
                }
            )
        }
    }


    fun register(
        name: String,
        username: String,
        password: String,
        email: String,
        address: String,
        identityNumber: String,
        mobileNumber: String,
        identityBitmap: Bitmap,
        imageFormat: Bitmap.CompressFormat
    ) {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                val result = registerUseCase(
                    name = name,
                    username = username,
                    password = password,
                    email = email,
                    address = address,
                    identityNumber = identityNumber,
                    mobileNumber = mobileNumber,
                    identityBitmap = identityBitmap,
                    imageFormat = imageFormat
                )

                // Handle success
                val userModel = result.getOrThrow()
                Log.d("RegisterViewModel", "Registration successful: Name=${userModel.name}, Username=${userModel.username}")
                _registerState.value = Result.success(userModel)

            } catch (e: Exception) {
                // Handle error
                _errorMessage.value = e.message ?: "Unknown error occurred"
                _registerState.value = Result.failure(e)
                Log.e("RegisterViewModel", "Registration error: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Function to check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sessionManager.isLogin
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun logout() {
        sessionManager.logout()
        _tokenState.value = TokenViewModel.TokenState.Expired
    }
}