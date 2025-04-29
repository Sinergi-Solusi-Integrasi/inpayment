import com.s2i.data.model.balance.HistoryBalanceData
import com.s2i.data.model.balance.TopUpData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.HistoryBalanceByIdResponse
import com.s2i.data.repository.balance.DetailTrxRepositoryImpl
import com.s2i.domain.repository.balance.DetailTrxRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.any

class DetailTrxRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices
    @InjectMocks
    private lateinit var detailTrxRepository: DetailTrxRepository

    @BeforeEach
    fun setUp() {
        apiServices = mock(ApiServices::class.java)
        detailTrxRepository = DetailTrxRepositoryImpl(apiServices)
    }

    @Test
    fun `getDetailTrx should return DetailTrxModel when API call is successful`() = runBlocking {
        // Mock response data
        val transactionId = "trx123"
        val mockTopUpData = TopUpData(
            trxId = "topup123",
            issuerPan = "issuerPan123",
            issuerName = "Bank A",
            customerPan = "customerPan123",
            customerName = "John Doe",
            externTrxId = "extTrx123"
        )

        val mockApiResponse = HistoryBalanceByIdResponse(
            code = 200,
            message = "Success",
            data = HistoryBalanceData(
                transactionId = transactionId,
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
                topUp = mockTopUpData, // GUNAKAN TopUpData BUKAN TopUpModel
                tollPayment = null
            )
        )

        // Mock API response
        runBlocking {
            Mockito.doReturn(mockApiResponse).`when`(apiServices).transactionsById(any())
        }

        // Call repository function
        val result = detailTrxRepository.getDetailTrx(transactionId)

        // Assert the results
        assertEquals(mockApiResponse.code, result.code)
        assertEquals(mockApiResponse.message, result.message)
        assertEquals(mockApiResponse.data.transactionId, result.data.transactionId)
        assertEquals(mockApiResponse.data.amount, result.data.amount)
        assertEquals(mockApiResponse.data.topUp?.trxId, result.data.topUp?.trxId)
    }
}
