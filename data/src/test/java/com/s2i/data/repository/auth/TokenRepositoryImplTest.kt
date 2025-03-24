package com.s2i.data.repository.auth

import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.auth.RefreshTokenResponse
import com.s2i.data.model.auth.TokenData
import com.s2i.domain.repository.auth.TokenRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import timber.log.Timber
import org.mockito.kotlin.any

class TokenRepositoryImplTest {

    private lateinit var tokenRepository: TokenRepository

    @Mock
    private lateinit var apiServices: ApiServices

    @Mock
    private lateinit var sessionManager: SessionManager

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Ensures mocks are initialized
        tokenRepository = TokenRepositoryImpl(apiServices, sessionManager) // Now correctly initialized

        if (Timber.forest().size == 0) {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Biarkan kosong agar tidak menyebabkan error
                }
            })
        }

    }

    @Test
    fun `refreshAccessToken should fail when user is logged out`() = runBlocking {
        whenever(sessionManager.isLoggedOut).thenReturn(true)

        val result = tokenRepository.refreshAccessToken()

        assertTrue(result.isFailure)
        assertEquals("User already logged out", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshAccessToken should fail when refresh token is null`() = runBlocking {
        whenever(sessionManager.isLoggedOut).thenReturn(false)
        whenever(sessionManager.refreshToken).thenReturn(null)

        val result = tokenRepository.refreshAccessToken()

        verify(sessionManager).logout()
        assertTrue(result.isFailure)
        assertEquals("Refresh token is null or empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshAccessToken should fail when refresh token is empty`() = runBlocking {
        whenever(sessionManager.isLoggedOut).thenReturn(false)
        whenever(sessionManager.refreshToken).thenReturn("")

        val result = tokenRepository.refreshAccessToken()

        verify(sessionManager).logout()
        assertTrue(result.isFailure)
        assertEquals("Refresh token is null or empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshAccessToken should succeed when API returns valid token`() = runBlocking {
        whenever(sessionManager.isLoggedOut).thenReturn(false)
        whenever(sessionManager.refreshToken).thenReturn("valid_refresh_token")

        val mockResponse = RefreshTokenResponse(
            code = 0,
            message = "success",
            data = TokenData(
                username = "neko_test",
                accessToken = "new_access_token",
                expiredAt = "2024-10-29T21:09:27+07:00"
            )
        )
        whenever(apiServices.refreshAccessToken(mapOf("refresh_token" to "valid_refresh_token")))
            .thenReturn(mockResponse)

        val result = tokenRepository.refreshAccessToken()

        verify(sessionManager).updateAccessToken("new_access_token", "2024-10-29T21:09:27+07:00")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `refreshAccessToken should fail when API returns error`() = runBlocking {
        // Simulasi bahwa user tidak dalam keadaan logout
        whenever(sessionManager.isLoggedOut).thenReturn(false)
        whenever(sessionManager.refreshToken).thenReturn("valid_refresh_token")

        // Mock response dari API yang mengembalikan error
        val mockResponse = RefreshTokenResponse(
            code = 1, // Status error
            message = "error",
            data = TokenData(
                username = "neko_test",
                accessToken = "", // Token kosong, menandakan error
                expiredAt = ""
            )
        )

        whenever(apiServices.refreshAccessToken(any())) // Gunakan `any()` untuk parameter Map<String, String>
            .thenReturn(mockResponse)

        val result = tokenRepository.refreshAccessToken()

        // Pastikan sessionManager.logout() dipanggil jika refresh token gagal
        verify(sessionManager).logout()

        // Pastikan hasil adalah kegagalan
        assertTrue(result.isFailure)
        assertEquals("Failed to refresh access token", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshAccessToken should fail when exception is thrown`() = runBlocking {
        // Simulasi bahwa user tidak dalam keadaan logout
        whenever(sessionManager.isLoggedOut).thenReturn(false)
        whenever(sessionManager.refreshToken).thenReturn("valid_refresh_token")

        // Simulasi bahwa API melempar exception ketika dipanggil
        whenever(apiServices.refreshAccessToken(any()))
            .thenThrow(RuntimeException("Network error"))

        val result = tokenRepository.refreshAccessToken()

        // Pastikan sessionManager.logout() dipanggil jika terjadi exception
        verify(sessionManager).logout()

        // Pastikan hasil adalah kegagalan
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `isAccessTokenExpired should return sessionManager value`() {
        whenever(sessionManager.isAccessTokenExpired()).thenReturn(true)

        val result = tokenRepository.isAccessTokenExpired()

        assertTrue(result)
    }

    @Test
    fun `isRefreshTokenExpired should return sessionManager value`() {
        whenever(sessionManager.isRefreshTokenExpired()).thenReturn(false)

        val result = tokenRepository.isRefreshTokenExpired()

        assertFalse(result)
    }
}
