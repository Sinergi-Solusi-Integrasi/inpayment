import android.util.Log
import com.s2i.data.model.users.UsersProfileData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.users.ProfileResponse
import com.s2i.data.remote.response.users.UsersResponse
import com.s2i.data.repository.users.UsersProfileRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UsersProfileRepositoryImplTest {

    private lateinit var usersProfileRepository: UsersProfileRepositoryImpl
    private val apiServices: ApiServices = mockk()

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every {Log.d(any(), any())} returns 0

        usersProfileRepository = UsersProfileRepositoryImpl(apiServices)
    }

    @Test
    fun `getUsersProfile should return correct user profile`() = runBlocking {
        // Mock response data
        val mockProfileResponse = ProfileResponse(
            userId = "123",
            username = "testuser",
            name = "Test User",
            accountNumber = "987654321",
            email = "test@example.com",
            mobileNumber = "08123456789",
            address = "Somewhere"
        )

        val mockUsersProfileData = UsersProfileData(
            userId = mockProfileResponse.userId,
            username = mockProfileResponse.username,
            name = mockProfileResponse.name,
            accountNumber = mockProfileResponse.accountNumber,
            email = mockProfileResponse.email,
            mobileNumber = mockProfileResponse.mobileNumber,
            address = mockProfileResponse.address,
            selectVehicle = null // No vehicle for now
        )

        val mockUsersProfileModel = UsersResponse(
            code = 200,
            message = "Success",
            data = mockUsersProfileData
        )

        // Mock API response
        coEvery { apiServices.profile() } returns mockUsersProfileModel

        // Call repository method
        val result = usersProfileRepository.getUsersProfile()

        // Assertions
        assertEquals(200, result.code)
        assertEquals("Success", result.message)
        assertEquals("123", result.data.userId)
        assertEquals("testuser", result.data.username)
        assertEquals("987654321", result.data.accountNumber)
        assertEquals("test@example.com", result.data.email)
        assertEquals("08123456789", result.data.mobileNumber)
        assertEquals("Somewhere", result.data.address)
    }
}
