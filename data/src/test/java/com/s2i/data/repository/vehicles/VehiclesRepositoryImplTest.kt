import android.util.Log
import com.s2i.data.model.vehicles.VehiclesData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.vehicles.AddVehiclesResponse
import com.s2i.data.remote.response.vehicles.VehiclesResponse
import com.s2i.data.repository.vehicles.VehiclesRepositoryImpl
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.domain.repository.vehicles.VehiclesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VehiclesRepositoryImplTest {

    private lateinit var repository: VehiclesRepository
    private val apiServices: ApiServices = mockk()

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @BeforeEach
    fun setUp() {
        repository = VehiclesRepositoryImpl(apiServices)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `getVehicles should return list of vehicles`() = runTest(testDispatcher) {
        // Arrange
        val mockVehicleResponse = listOf(
            VehiclesData(
                vehicleId = "1",
                ownerUserId = "owner1",
                borrowerUserId = "borrower1",
                brand = "Toyota",
                model = "Corolla",
                varian = "XLI",
                nameVehicles = "Toyota Corolla XLI",
                color = "White",
                plateNumber = "AB123CD",
                group = 10,
                rfid = "RFID123",
                priority = 1,
                images = emptyList(),
                certificateImage = "cert1",
                loanExpiredAt = "2024-12-31",
                loanedAt = "2024-01-01",
                createdAt = "2024-01-01",
                updatedAt = "2024-01-02",
                isOwner = true,
                isLoaned = false,
                status = "active",
                agencyCard = null
            )
        )

        coEvery { apiServices.vehicles() } returns VehiclesResponse(
            code = 200,
            message = "Success",
            data = mockVehicleResponse
        )

        // Act
        val result = repository.getVehicles()

        // Debugging
        println("🔹 Expected Vehicles: $mockVehicleResponse")
        println("🔹 Actual Vehicles: ${result.data}")

        // Assert
        assertEquals(200, result.code)
        assertEquals("Success", result.message)
        assertEquals(1, result.data.size)
        assertEquals("Toyota", result.data[0].brand)

        coVerify { apiServices.vehicles() }
    }


    @Test
    fun `registVehicles should return registered vehicle`() = runTest(testDispatcher) {
        // Arrange
        val expectedVehicleData = VehiclesData(
            vehicleId = "2",
            ownerUserId = "owner2",
            borrowerUserId = null,
            brand = "Honda",
            model = "Civic",
            varian = "Turbo",
            nameVehicles = "Honda Civic Turbo",
            color = "Black",
            plateNumber = "XYZ123",
            group = 5,
            rfid = "RFID456",
            priority = 2,
            images = emptyList(),
            certificateImage = "cert2",
            loanExpiredAt = null,
            loanedAt = null,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-02",
            isOwner = true,
            isLoaned = false,
            status = "active",
            agencyCard = null
        )

        val expectedResponse = AddVehiclesResponse(
            code = 200,
            message = "Registered",
            vehiclesData = expectedVehicleData
        )

        coEvery { apiServices.addVehicles(any()) } returns expectedResponse

        // Act
        val result = repository.registVehicles(
            brand = "Honda",
            model = "Civic",
            varian = "Turbo",
            color = "Black",
            type = "Sedan",
            plateNumber = "XYZ123",
            documentImage = BlobImageModel("jpg", "image/jpeg", "base64data"),
            vehicleImages = listOf(BlobImageModel("jpg", "image/jpeg", "base64data"))
        )


        // Debugging output
        println("🔹 Expected Response: $expectedResponse")
        println("🔹 Actual Response from Repository: ${result.vehiclesData}")

        // Assert
        assertEquals(200, result.code)
        assertEquals("Registered", result.message)

        // Handle null safety untuk menghindari error assertion pada unit test
        val actualVehicleData = result.vehiclesData
        assertEquals(expectedVehicleData.brand, actualVehicleData.brand ?: "NULL")
        assertEquals(expectedVehicleData.model, actualVehicleData.model ?: "NULL")
        assertEquals(expectedVehicleData.varian, actualVehicleData.varian ?: "NULL")
        assertEquals(expectedVehicleData.color, actualVehicleData.color ?: "NULL")
        assertEquals(expectedVehicleData.plateNumber, actualVehicleData.plateNumber ?: "NULL")

        coVerify { apiServices.addVehicles(any()) }
    }

    @Test
    fun `pullVehiclesLoans should return pulled vehicle data`() = runTest(testDispatcher) {
        // Arrange
        val vehicleId = "1"
        val vehicleData = VehiclesData(
            vehicleId = vehicleId,
            ownerUserId = "owner1",
            borrowerUserId = "borrower1",
            brand = "Toyota",
            model = "Corolla",
            varian = "XLI",
            nameVehicles = "Toyota Corolla XLI",
            color = "White",
            plateNumber = "AB123CD",
            group = 10,
            rfid = "RFID123",
            priority = 1,
            images = emptyList(),
            certificateImage = "cert1",
            loanExpiredAt = "2024-12-31",
            loanedAt = "2024-01-01",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-02",
            isOwner = true,
            isLoaned = false,
            status = "active",
            agencyCard = null
        )
        val expectedResponse = com.s2i.data.remote.response.vehicles.LoansVehiclesResponse(
            code = 200,
            message = "Pulled",
            data = vehicleData
        )

        coEvery { apiServices.pullLoans(vehicleId) } returns expectedResponse

        // Act
        val result = repository.pullVehiclesLoans(vehicleId)

        // Assert
        assertEquals(200, result.code)
        assertEquals("Pulled", result.message)
        assertEquals(vehicleId, result.data.vehicleId)

        coVerify { apiServices.pullLoans(vehicleId) }
    }

    @Test
    fun `loansVehicles should return vehicle data based on token`() = runTest(testDispatcher) {
        // Arrange
        val token = "vehicle_token_abc"
        val vehicleData = VehiclesData(
            vehicleId = "1",
            ownerUserId = "owner1",
            borrowerUserId = "borrower1",
            brand = "Toyota",
            model = "Corolla",
            varian = "XLI",
            nameVehicles = "Toyota Corolla XLI",
            color = "White",
            plateNumber = "AB123CD",
            group = 10,
            rfid = "RFID123",
            priority = 1,
            images = emptyList(),
            certificateImage = "cert1",
            loanExpiredAt = "2024-12-31",
            loanedAt = "2024-01-01",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-02",
            isOwner = true,
            isLoaned = true,
            status = "loaned",
            agencyCard = null
        )
        val expectedResponse = com.s2i.data.remote.response.vehicles.LoansVehiclesResponse(
            code = 200,
            message = "Loan success",
            data = vehicleData
        )

        coEvery { apiServices.loansVehicles(any()) } returns expectedResponse

        // Act
        val result = repository.loansVehicles(token)

        // Assert
        assertEquals(200, result.code)
        assertEquals("Loan success", result.message)
        assertEquals("1", result.data.vehicleId)

        coVerify { apiServices.loansVehicles(any()) }
    }

    @Test
    fun `getStatusEnableVehicles should return vehicle status enabled`() = runTest(testDispatcher) {
        // Arrange
        val vehicleId = "1"
        val expectedData = com.s2i.data.model.vehicles.StatusVehiclesData(
            vehicleId = vehicleId,
            status = "enabled"
        )
        val expectedResponse = com.s2i.data.remote.response.vehicles.SelectedVehiclesResponse(
            code = 200,
            message = "Enabled",
            data = expectedData
        )

        coEvery { apiServices.vehiclesEnable(vehicleId) } returns expectedResponse

        // Act
        val result = repository.getStatusEnableVehicles(vehicleId)

        // Assert
        assertEquals(200, result.code)
        assertEquals("Enabled", result.message)
        assertEquals("enabled", result.data.status)

        coVerify { apiServices.vehiclesEnable(vehicleId) }
    }

    @Test
    fun `getStatusDisableVehicles should return vehicle status disabled`() = runTest(testDispatcher) {
        // Arrange
        val vehicleId = "1"
        val expectedData = com.s2i.data.model.vehicles.StatusVehiclesData(
            vehicleId = vehicleId,
            status = "disabled"
        )
        val expectedResponse = com.s2i.data.remote.response.vehicles.SelectedVehiclesResponse(
            code = 200,
            message = "Disabled",
            data = expectedData
        )

        coEvery { apiServices.vehiclesDisable(vehicleId) } returns expectedResponse

        // Act
        val result = repository.getStatusDisableVehicles(vehicleId)

        // Assert
        assertEquals(200, result.code)
        assertEquals("Disabled", result.message)
        assertEquals("disabled", result.data.status)

        coVerify { apiServices.vehiclesDisable(vehicleId) }
    }

}
