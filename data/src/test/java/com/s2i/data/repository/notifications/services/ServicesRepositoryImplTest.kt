package com.s2i.data.repository.notifications.services

import com.s2i.data.model.notifications.services.BindingAccountData
import com.s2i.data.model.notifications.services.RegisterDevicesData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.notification.services.BindingAccountResponse
import com.s2i.data.remote.response.notification.services.RegisterDevicesResponse
import com.s2i.domain.entity.model.notification.services.DevicesTokenModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import timber.log.Timber

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension::class)
class ServicesRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices

    @InjectMocks
    private lateinit var servicesRepository: ServicesRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Pastikan Timber tidak menyebabkan error dalam unit test
        if (Timber.forest().isEmpty()) {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Log diabaikan untuk keperluan unit test
                }
            })
        }
    }

    @Test
    fun `devicesToken should return DevicesTokenModel when API call is successful`() = runBlocking {
        // Mock API Response
        val mockResponse = RegisterDevicesResponse(
            code = 0,
            message = "Success",
            data = RegisterDevicesData(
                devicesId = "device123",
                model = "Pixel 5",
                osType = "Android",
                platform = "Mobile",
                sdkVersion = "30",
                createdAt = "2025-03-17T10:00:00Z"
            )
        )

        // Mock API Call
        whenever(
            apiServices.sendTokenDevice(any())
        ).thenReturn(mockResponse)

        // Call function
        val result: DevicesTokenModel = servicesRepository.devicesToken(
            brand = "Google",
            model = "Pixel 5",
            osType = "Android",
            platform = "Mobile",
            sdkVersion = "30",
            tokenFirebase = "firebase_token"
        )

        // Assertions
        assertNotNull(result, "Result should not be null")
        assertEquals(mockResponse.code, result.code)
        assertEquals(mockResponse.message, result.message)
        assertEquals(mockResponse.data.devicesId, result.data.devicesId)
    }

    @Test
    fun `bindingAccount should return BindingModel when API call is successful`() = runBlocking {
        val mockResponse = BindingAccountResponse(
            code = 0,
            message = "Binding successful",
            data = BindingAccountData(
                devicesId = "device123",
                userId = "user456",
                bindingAt = "2025-03-17T10:05:00Z"
            )
        )

        whenever(apiServices.bindingAccounts("device123"))
            .thenReturn(mockResponse)

        // Debugging: Periksa apakah mock API call berhasil
        val apiResult = apiServices.bindingAccounts("device123")
        println("Debugging API Mock: $apiResult")  // Jika ini null, berarti mocking belum bekerja

        val result = servicesRepository.bindingAccount("device123")

        println("Debugging Repository Result: $result") // Jika ini null, cek apakah ada masalah dalam repository

        assertNotNull(result, "Result should not be null")
        assertEquals(mockResponse.code, result.code)
        assertEquals(mockResponse.message, result.message)
        assertEquals(mockResponse.data.devicesId, result.data.devicesId)
        assertEquals(mockResponse.data.userId, result.data.userId)
    }
}
