package com.s2i.data.repository.auth

import android.util.Log
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.response.auth.LoginResponse
import com.s2i.data.remote.response.auth.RegisterResponse
import com.s2i.data.model.auth.AuthData
import com.s2i.data.model.auth.LogoutData
import com.s2i.data.model.users.UsersData
import com.s2i.data.remote.response.auth.LogoutResponse
import com.s2i.domain.entity.model.users.BlobImageModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.robolectric.shadows.ShadowLog
import timber.log.Timber



@ExtendWith(MockitoExtension::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices

    @Mock
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(apiServices, sessionManager)

        if (Timber.forest().size == 0) {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Biarkan kosong agar tidak menyebabkan error
                }
            })
        }

        // ✅ Mock Log agar tidak menyebabkan error dalam unit test
        val mockLog = Mockito.mockStatic(Log::class.java)
        mockLog.use {
            it.`when`<Any> { Log.d(any(), any()) }.thenReturn(0)
            it.`when`<Any> { Log.i(any(), any()) }.thenReturn(0)
            it.`when`<Any> { Log.e(any(), any()) }.thenReturn(0)
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

                val mockCall: Call<LoginResponse> = Mockito.mock()
                whenever(apiServices.login(any())).thenReturn(mockCall)

                whenever(mockCall.enqueue(any())).thenAnswer {
                    val callback = it.getArgument<Callback<LoginResponse>>(0)
                    callback.onResponse(mockCall, Response.success(loginResponse))
                }

                val result = authRepository.login("johndoe", "password123")

                // ✅ Verifikasi hasil login
                assert(result.isSuccess)
                val authModel = result.getOrNull()
                assertNotNull(authModel)
                assertEquals("johndoe", authModel?.username)

                // ✅ Verifikasi bahwa metode API login dipanggil dengan benar
                Mockito.verify(apiServices).login(any())
                Mockito.verify(mockCall).enqueue(any())
            }
        }
    }

    // ✅ REGISTER TEST
    @Test
    fun `register success`() = runTest {
        val registerData = UsersData(
            username = "testuser",
            name = "Test User"
        )

        val registerResponse = RegisterResponse(
            code = 0, // ✅ Sesuai dengan kode sukses di API
            message = "Success",
            data = registerData
        )

        whenever(apiServices.register(any())).thenReturn(registerResponse)

        // ✅ Jalankan fungsi register()
        val result = authRepository.register(
            name = "Test User",
            username = "testuser",
            password = "password",
            email = "test@example.com",
            mobileNumber = "08123456789",
            address = "Test Address",
            identityNumber = "123456789",
            identityImage = BlobImageModel("imageBase64Data", "jpg", "image/jpeg")
        )

        // ✅ Pastikan hasil sukses
        assert(result.isSuccess)

        val userModel = result.getOrNull()
        assertNotNull(userModel)

        // ✅ Periksa semua atribut utama
        assertEquals("testuser", userModel?.username)
        assertEquals("Test User", userModel?.name)

        // ✅ Verifikasi bahwa API register() dipanggil sekali
        Mockito.verify(apiServices).register(any())
    }

    // ✅ LOGOUT TEST
    @Test
    fun `logout success`() = runBlocking {
        val logoutResponse = LogoutResponse(
            code = 0,
            message = "Logout successful",
            data = LogoutData(
                logoutAt = "2025-12-31T23:59:59"
            )
        )

        whenever(apiServices.logout(any())).thenReturn(logoutResponse)

        val result = authRepository.logout(deviceId = "device123")

        assertEquals(0, result.code)
        assertEquals("Logout successful", result.message)
        assertEquals("2025-12-31T23:59:59", result.data?.logoutAt)
    }
}
