import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.BalanceResponse
import com.s2i.data.model.balance.BalanceData
import com.s2i.data.repository.balance.BalanceRepositoryImpl
import com.s2i.domain.entity.model.balance.BalanceModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BalanceRepositoryImplTest {

    private lateinit var balanceRepository: BalanceRepositoryImpl
    private val apiServices: ApiServices = mockk()

    @BeforeEach
    fun setUp() {
        balanceRepository = BalanceRepositoryImpl(apiServices)
    }

    @Test
    fun `getBalance should return BalanceModel when API call is successful`() = runBlocking {
        // Given
        val balanceData = BalanceData(
            vehicleUserId = "12345",
            plateNumber = "B 1234 ABC",
            rfid = "RFID123",
            accountNumber = "ACC12345",
            balance = 100000,
            updatedAt = "2023-03-14T10:00:00Z"
        )
        val balanceResponse = BalanceResponse(
            code = 200,
            message = "Success",
            data = balanceData
        )
        coEvery { apiServices.balance() } returns balanceResponse

        // When
        val result = balanceRepository.getBalance()

        // Then
        val expected = BalanceModel(
            vehicleUserId = "12345",
            plateNumber = "B 1234 ABC",
            rfid = "RFID123",
            accountNumber = "ACC12345",
            balance = 100000,
            updatedAt = "2023-03-14T10:00:00Z"
        )
        assertEquals(expected, result)
    }
}
