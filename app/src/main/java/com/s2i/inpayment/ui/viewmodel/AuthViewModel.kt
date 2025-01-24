package com.s2i.inpayment.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.auth.AuthLogoutModel
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.entity.model.auth.LogoutModel
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.entity.model.users.UsersModel
import com.s2i.domain.usecase.auth.LoginUseCase
import com.s2i.domain.usecase.auth.LogoutUseCase
import com.s2i.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _loginState = MutableStateFlow<Result<AuthModel>?>(null)
    val loginState: StateFlow<Result<AuthModel>?> = _loginState

    private val _registerState = MutableStateFlow<Result<UsersModel>?>(null)
    val registerState: MutableStateFlow<Result<UsersModel>?> = _registerState

    private val _logoutState = MutableStateFlow<AuthLogoutModel?>(null)
    val logoutState: MutableStateFlow<AuthLogoutModel?> = _logoutState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

//    private val _tokenState =
//        MutableStateFlow<TokenViewModel.TokenState>(TokenViewModel.TokenState.Valid)
//    val tokenState: StateFlow<TokenViewModel.TokenState> = _tokenState

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
        viewModelScope.launch {
            _loadingState.value = true // Start loading
            val result = loginUseCase(username, password)
            _loginState.value = result
            result.fold(
                onSuccess = { authModel ->
                    Log.d("AuthViewModel", "Login successful: $username")
                    sessionManager.createLoginSession(
                        accessToken = authModel.accessToken,
                        refreshToken = authModel.refreshToken,
                        accessTokenExpiry = authModel.accessTokenExpiredAt,
                        refreshTokenExpiry = authModel.refreshTokenExpiredAt,
                        username = authModel.username,
                        userId = authModel.userId
                    )
                    Log.d("AuthViewModel", "Session updated. isLogin: ${sessionManager.isLogin}")
                },
                onFailure = { throwable ->
                    _loginState.value = Result.failure(throwable)
                    _errorMessage.value = throwable.message ?: "Unknown error"
                }
            )
            _loadingState.value = false
        }
    }


    fun register(
        name: String,
        username: String,
        password: String,
        email: String,
        mobileNumber: String,
        address: String,
        identityNumber: String,
        identityBitmap: Bitmap,
        imageFormat: Bitmap.CompressFormat
    ) {
        viewModelScope.launch {
            _loadingState.value = true
            // convert bitmap to blobimagemodel
            val (base64, ext, mimeType) = bitmapToBase64WithFormat(identityBitmap, imageFormat)
            val identityModel = BlobImageModel(
                data = base64,
                ext = ext,
                mimeType = mimeType
            )
            try {
                val result = registerUseCase(
                    name = name,
                    username = username,
                    password = password,
                    email = email,
                    mobileNumber = mobileNumber,
                    address = address,
                    identityNumber = identityNumber,
                    identityImage = identityModel,
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

    // Logout
    fun logout() {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                val devicesId = sessionManager.getFromPreference(SessionManager.KEY_DEVICE_ID)
                Log.d("LogoutViewModel", "Devices ID: $devicesId")

                // Panggil logout API
                val result = logoutUseCase(devicesId)
                Log.d("AuthViewModel", "Logout API successful: $result")

                // Hapus semua data sesi
                sessionManager.logout()

                // Set isLoggedOut menjadi true agar LaunchEffect tidak memicu navigasi otomatis
                sessionManager.isLoggedOut = true

                Log.d("AuthViewModel", "All session data cleared after successful logout.")
                _logoutState.value = result
            } catch(e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
                Log.e("LogoutViewModel", "Logout error: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }


    fun resetLoginState() {
        _loginState.value = null // Reset setelah proses selesai
        _logoutState.value = null
    }


    // Function to check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sessionManager.isLogin
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

//    fun logout() {
//        sessionManager.logout()
////        _tokenState.value = TokenViewModel.TokenState.Expired
//    }
}