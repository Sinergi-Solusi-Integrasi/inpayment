package com.s2i.data.repository.auth

import android.util.Log
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.data.model.auth.AuthData
import com.s2i.data.model.auth.LogoutData
import com.s2i.data.model.users.UsersData
import com.s2i.data.remote.response.auth.LogoutResponse
import com.s2i.data.remote.response.auth.RegisterResponse
import com.s2i.domain.entity.model.users.BlobImageModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExtendWith(MockitoExtension::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices
    @Mock
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepositoryImpl

    @BeforeEach // ✅ Ganti @Before dengan @BeforeEach (JUnit5)
    fun setUp() {
        MockitoAnnotations.openMocks(this) // ✅ Pastikan Mock diinisialisasi
        authRepository = AuthRepositoryImpl(apiServices, sessionManager)

        // Mock Log.d() agar tidak menyebabkan error
        Mockito.mockStatic(Log::class.java).use {
            Mockito.`when`(Log.d(any(), any())).thenReturn(0)
        }
    }

    // ✅ LOGIN TEST
    @Test
    fun `login success`() {
        Mockito.mockStatic(Log::class.java).use {
            `when`(Log.d(any(), any())).thenReturn(0)
        runTest {
            val authData = AuthData(
                userId = "123",
                name = "John Doe",
                username = "johndoe",
                accessToken = "access_token_123",
                refreshToken = "refresh_token_123",
                accessTokenExpiredAt = "2024-10-29T21:09:02+07:00",
                refreshTokenExpiredAt = "2024-11-05T18:09:02+07:00"
            )

            val loginResponse = LoginResponse(
                code = 200,
                message = "Success",
                data = authData
            )

            val mockCall: Call<LoginResponse> = mock()
            whenever(apiServices.login(any())).doReturn(mockCall)

            whenever(mockCall.enqueue(any())).thenAnswer {
                val callback = it.getArgument<Callback<LoginResponse>>(0)
                callback.onResponse(mockCall, Response.success(loginResponse))
            }

            val result = authRepository.login("johndoe", "password123")

            // Verifikasi hasil
            assert(result.isSuccess)
            val authModel = result.getOrNull()
            assert(authModel != null)
            assert(authModel?.username == "johndoe")
            Mockito.verify(apiServices).login(any())
            Mockito.verify(mockCall).enqueue(any())

        }
            }
    }

}

