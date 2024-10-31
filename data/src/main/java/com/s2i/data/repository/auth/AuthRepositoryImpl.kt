package com.s2i.data.repository.auth

import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.domain.entity.model.auth.AuthModel
import com.s2i.domain.repository.auth.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.util.Log
import com.s2i.data.local.auth.SessionManager
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
                        if (responseBody != null && responseBody.data.isNotEmpty()) {
                            val authData = responseBody.data[0]
                            Log.d("AuthRepository", "Login successful: ${authData.username}")
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
        mobileNumber: String
    ): Result<UsersModel> {
        Log.d("AuthRepository", "Attempting to register with $username")

        val registerData = mapOf(
            "name" to name,
            "username" to username,
            "password" to password,
            "email_address" to email,
            "mobile_number" to mobileNumber,
            "identity_number" to identityNumber,
            "address" to address
        )

        return try {
            val response = apiServices.register(registerData).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    val userData = it.data[0]
                    Result.success(
                        UsersModel(
                            name = userData.name,
                            username = userData.username,
                            password = password,
                            email = email,
                            mobileNumber = mobileNumber,
                            identityNumber = identityNumber,
                            address = address
                        )
                    )
                } ?: Result.failure(Exception("Register failed: No data available"))
            } else {
                // Handle HTTP error codes for registration
                val errorMessage = "Error: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Registration error: ${e.message}"))
        }
    }
}


