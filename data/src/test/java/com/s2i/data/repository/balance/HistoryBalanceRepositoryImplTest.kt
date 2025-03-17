package com.s2i.data.repository.balance

import com.s2i.data.model.balance.HistoryBalanceData
import com.s2i.data.model.balance.InOutBalanceData
import com.s2i.data.model.balance.TollPayData
import com.s2i.data.model.balance.TopUpData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.HistoryBalanceResponse
import com.s2i.data.remote.response.balance.InOutBalanceResponse
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class HistoryBalanceRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices

    @InjectMocks
    private lateinit var historyBalanceRepository: HistoryBalanceRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Tidak perlu initMocks karena menggunakan @ExtendWith(MockitoExtension::class)
    }

    @Test
    fun `getHistoryBalance should return InOutBalanceModel when API call is successful`() = runBlocking {
        // Mock API response
        val mockTopUp = TopUpData(
            trxId = "topup123",
            issuerPan = "issuerPan123",
            issuerName = "Bank A",
            customerPan = "customerPan123",
            customerName = "John Doe",
            externTrxId = "extTrx123"
        )

        val mockTollPayment = TollPayData(
            trxId = "toll123",
            vehiclesId = "vehicle1",
            branchId = "branch1",
            gateId = "gate1",
            stationId = "station1",
            shift = "shift1",
            period = "period1",
            tollCollectorId = "collector1",
            shiftLeaderId = "leader1",
            receiptNumber = "receipt123",
            plateNumber = "AB123CD",
            rfid = "rfid123",
            rfidDetected = true,
            plateDetected = true,
            vehicleCaptures = listOf("img1", "img2")
        )

        val mockHistoryItem = HistoryBalanceData(
            transactionId = "trx123",
            userId = "user123",
            accountNumber = "acc123",
            refId = "ref123",
            amount = 10000,
            fee = 100,
            cashFlow = "IN",
            trxType = "TOPUP",
            paymentMethod = "CREDIT_CARD",
            startingBalance = 50000,
            endingBalance = 60000,
            title = "Top Up",
            status = "SUCCESS",
            trxDate = "2023-01-01",
            createdAt = "2023-01-01T10:00:00Z",
            updatedAt = "2023-01-01T10:10:00Z",
            topUp = mockTopUp,
            tollPayment = mockTollPayment
        )

        val mockApiResponse = InOutBalanceResponse(
            code = 200,
            message = "Success",
            data = InOutBalanceData(
                accountNumber = "acc123",
                historyCount = 1,
                history = listOf(mockHistoryItem)
            )
        )

        // Pastikan `transactions()` adalah suspend function, gunakan `whenever`
        whenever(apiServices.transactions()).thenReturn(mockApiResponse)

        // Call repository function
        val result: InOutBalanceModel = historyBalanceRepository.getHistoryBalance()

        // Debugging jika ada error
        println("Mock API Response: $mockApiResponse")
        println("Repository Result: $result")

        // Assert the results
        assertEquals(mockApiResponse.data.accountNumber, result.accountNumber)
        assertEquals(mockApiResponse.data.historyCount, result.historyCount)
        assertEquals(mockApiResponse.data.history.size, result.history.size)
        assertEquals(mockApiResponse.data.history[0].transactionId, result.history[0].transactionId)
        assertEquals(mockApiResponse.data.history[0].amount, result.history[0].amount)
        assertEquals(mockApiResponse.data.history[0].topUp?.trxId, result.history[0].topUp?.trxId)
        assertEquals(mockApiResponse.data.history[0].tollPayment?.trxId, result.history[0].tollPayment?.trxId)
    }
}
