package com.s2i.data.repository.balance

import com.s2i.data.model.balance.HistoryBalanceData
import com.s2i.data.model.balance.InOutBalanceData
import com.s2i.data.model.balance.TollPayData
import com.s2i.data.model.balance.TopUpData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.InOutBalanceResponse
import com.s2i.domain.entity.model.balance.InOutBalanceModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension::class)
class InOutBalanceRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices

    @InjectMocks
    private lateinit var inOutBalanceRepository: InOutBalanceRepositoryImpl

    private lateinit var mockApiResponse: InOutBalanceResponse

    @BeforeEach
    fun setUp() {
        runBlocking {
            // Mock TopUp data
            val mockTopUp = TopUpData(
                trxId = "topup123",
                issuerPan = "issuerPan123",
                issuerName = "Bank A",
                customerPan = "customerPan123",
                customerName = "John Doe",
                externTrxId = "extTrx123"
            )

            // Mock Toll Payment data
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

            // Mock History Data
            val mockHistoryList = (1..5).map {
                HistoryBalanceData(
                    transactionId = "trx$it",
                    userId = "user$it",
                    accountNumber = "acc$it",
                    refId = "ref$it",
                    amount = 1000 * it,
                    fee = 10 * it,
                    cashFlow = if (it % 2 == 0) "OUT" else "IN",
                    trxType = "PAYMENT",
                    paymentMethod = "CREDIT_CARD",
                    startingBalance = 50000 - (it * 1000),
                    endingBalance = 50000 - ((it - 1) * 1000),
                    title = "Transaction $it",
                    status = "SUCCESS",
                    trxDate = "2023-01-0$it",
                    createdAt = "2023-01-0${it}T10:00:00Z",
                    updatedAt = "2023-01-0${it}T10:10:00Z",
                    topUp = if (it % 2 == 0) null else mockTopUp,
                    tollPayment = if (it % 2 == 0) mockTollPayment else null
                )
            }

            // Mock API Response
            mockApiResponse = InOutBalanceResponse(
                code = 200,
                message = "Success",
                data = InOutBalanceData(
                    accountNumber = "acc_main",
                    historyCount = mockHistoryList.size,
                    history = mockHistoryList
                )
            )

            // Setup mock API response
            whenever(apiServices.transactions()).thenReturn(mockApiResponse)
        }
    }

    @Test
    fun `getInOutBalance should return InOutBalanceModel with mapped data`() {
        runBlocking {
            // Call repository function
            val result: InOutBalanceModel = inOutBalanceRepository.getInOutBalance()

            // Debugging jika masih error
            println("Mock API Response: $mockApiResponse")
            println("Repository Result: $result")

            // Pastikan `result` tidak null
            assertNotNull(result, "Hasil tidak boleh null")
            assertNotNull(result.history, "History tidak boleh null")

            // Cek apakah hanya mengambil 3 transaksi pertama
            assertEquals(3, result.history.size, "History harus berisi hanya 3 transaksi pertama")

            // Cek apakah data di-retrieve dengan benar
            result.history.forEachIndexed { index, historyItem ->
                val expectedHistory = mockApiResponse.data.history[index]
                assertEquals(expectedHistory.transactionId, historyItem.transactionId)
                assertEquals(expectedHistory.amount, historyItem.amount)
                assertEquals(expectedHistory.cashFlow, historyItem.cashFlow)
                assertEquals(expectedHistory.trxType, historyItem.trxType)

                // Cek TopUpModel
                if (expectedHistory.topUp != null) {
                    assertNotNull(historyItem.topUp, "TopUpModel tidak boleh null jika API mengembalikan data")
                    assertEquals(expectedHistory.topUp!!.trxId, historyItem.topUp?.trxId)
                }

                // Cek TollPayModel
                if (expectedHistory.tollPayment != null) {
                    assertNotNull(historyItem.tollPayment, "TollPayModel tidak boleh null jika API mengembalikan data")
                    assertEquals(expectedHistory.tollPayment!!.trxId, historyItem.tollPayment?.trxId)
                }
            }
        }
    }
}
