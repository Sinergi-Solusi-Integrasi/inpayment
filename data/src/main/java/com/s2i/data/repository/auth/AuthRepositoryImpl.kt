package com.s2i.data.repository.auth

import android.graphics.Bitmap
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.repository.auth.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.util.Log
import com.s2i.common.utils.convert.bitmapToBase64
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.data.BuildConfig
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.model.users.BlobImageData
import com.s2i.data.remote.request.auth.RegisterRequest
import com.s2i.domain.entity.model.users.UsersModel

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl(
    private val apiServices: ApiServices,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthModel> {
        val loginData = mapOf("username" to username, "password" to password)

        return suspendCancellableCoroutine { continuation ->
            apiServices.login(loginData).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.d("AuthRepository", "Attempting to login with $username")

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val authData = responseBody.data
                            Log.d("AuthRepository", "Login successful: ${authData.username}")

                            // Log tokens for debugging
                            if (BuildConfig.DEBUG) {
                                Log.d("AuthRepository", "Saving tokens: AccessToken=${authData.accessToken}, RefreshToken=${authData.refreshToken}")
                            }
                            sessionManager.createLoginSession(
                                accessToken = authData.accessToken,
                                refreshToken = authData.refreshToken,
                                accessTokenExpiry = authData.accessTokenExpiredAt,
                                refreshTokenExpiry = authData.refreshTokenExpiredAt,
                                username = authData.username
                            )
                            continuation.resume(Result.success(
                                AuthModel(
                                    name = authData.name,
                                    username = authData.username,
                                    accessToken = authData.accessToken,
                                    refreshToken = authData.refreshToken,
                                    accessTokenExpiredAt = authData.accessTokenExpiredAt,
                                    refreshTokenExpiredAt = authData.refreshTokenExpiredAt
                                )
                            ))
                        } else {
                            // Empty or null data
                            Log.e("AuthRepository", "Login failed: No data in response")
                            continuation.resume(Result.failure(Exception("Login failed: No data available")))
                        }
                    } else {
                        // Handle HTTP error codes without throwing exceptions
                        val errorMessage = when (response.code()) {
                            401 -> "Unauthorized"
                            403 -> "Forbidden"
                            404 -> "Not Found"
                            502 -> "Bad Gateway"
                            500 -> "Internal Server Error"
                            else -> "Unknown Error: ${response.code()}"
                        }
                        Log.e("AuthRepository", "Login failed: $errorMessage")
                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("AuthRepository", "Login error: ${t.message}", t)
                    // Handle network or unexpected errors
                    continuation.resume(Result.failure(Exception("Network error: ${t.message}")))
                }
            })
        }
    }

    override suspend fun register(
        name: String,
        username: String,
        password: String,
        email: String,
        address: String,
        identityNumber: String,
        mobileNumber: String,
        identityBitmap: Bitmap,
        imageFormat: Bitmap.CompressFormat
    ): Result<UsersModel> {
        Log.d("AuthRepository", "Attempting to register with $username")

        //convert image to base64 dan mime
        val (base64Data, ext, mimeType) = bitmapToBase64WithFormat(identityBitmap, imageFormat)
        val registerRequest = RegisterRequest(
            name = name,
            username =  username,
            password =  password,
            email = email,
            mobileNumber =  mobileNumber,
            identityNumber = identityNumber,
            address = address,
            identityImage = BlobImageData(
                ext = ext,
                mimeType = mimeType,
                data = base64Data
            )
        )

        return try {
            val response = apiServices.register(registerRequest)
            val userData = response.data ?: return Result.failure(Exception("Invalid response data"))
            if (response.code != 0) {
                val errorMessage = response.message ?: "Unknown error occurred"
                Log.e("AuthRepository", "Registration failed: $errorMessage")
                Result.failure(Exception("Registration failed: $errorMessage"))
            } else {
                Log.d("AuthRepository", "Registration successful for user: ${userData.username}, ${userData.name}")
                Result.success(
                    UsersModel(
                        name = userData.name,
                        username = userData.username
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error: ${e.message}", e)
            Result.failure(Exception("Registration error: ${e.message}"))
        }
    }
}


