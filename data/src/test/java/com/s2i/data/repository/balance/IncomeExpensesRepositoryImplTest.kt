package com.s2i.data.repository.balance

import com.s2i.data.model.balance.ExpenseTrx
import com.s2i.data.model.balance.IncomeExpenseData
import com.s2i.data.model.balance.IncomeTrx
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.response.balance.IncomeExpenseResponse
import com.s2i.domain.entity.model.balance.IncomeExpenseModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class IncomeExpensesRepositoryImplTest {

    @Mock
    private lateinit var apiServices: ApiServices

    @InjectMocks
    private lateinit var incomeExpenseRepository: IncomeExpensesRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Tidak perlu initMocks karena menggunakan @ExtendWith(MockitoExtension::class)
    }

    @Test
    fun `getIncomeExpenses should return IncomeExpenseModel when API call is successful`() {
        runBlocking {
            // Mock API response
            val mockIncomeTrx = IncomeTrx(
                trxId = "income123",
                userId = "user123",
                accountNumber = "acc123",
                refId = "ref123",
                amount = 50000,
                feeAmount = 100,
                cashflow = "IN",
                trxType = "SALARY",
                paymentMethods = "BANK_TRANSFER",
                beginningBalance = 100000,
                endingBalance = 150000,
                title = "Monthly Salary",
                trxStatus = "SUCCESS",
                trxDates = "2023-01-01",
                createdAt = "2023-01-01T10:00:00Z",
                updatedAt = "2023-01-01T10:10:00Z"
            )

            val mockExpenseTrx = ExpenseTrx(
                trxId = "expense123",
                userId = "user123",
                accountNumber = "acc123",
                refId = "ref456",
                amount = 20000,
                feeAmount = 50,
                cashflow = "OUT",
                trxType = "GROCERIES",
                paymentMethods = "DEBIT_CARD",
                beginningBalance = 150000,
                endingBalance = 130000,
                title = "Grocery Shopping",
                trxStatus = "SUCCESS",
                trxDates = "2023-01-02",
                createdAt = "2023-01-02T12:00:00Z",
                updatedAt = "2023-01-02T12:15:00Z"
            )

            val mockApiResponse = IncomeExpenseResponse(
                code = 200,
                message = "Success",
                data = IncomeExpenseData(
                    incomeTrx = mockIncomeTrx,
                    expenseTrx = mockExpenseTrx
                )
            )

            // Pastikan pemanggilan suspend function ada dalam coroutine
            whenever(apiServices.incomeExpense()).thenReturn(mockApiResponse)

            // Jalankan suspend function dalam coroutine
            val result: IncomeExpenseModel = incomeExpenseRepository.getIncomeExpenses()

            // Debugging jika masih error
            println("Mock API Response: $mockApiResponse")
            println("Repository Result: $result")

            // Pastikan `result.data` tidak null sebelum mengakses properti lainnya
            assertNotNull(result.data, "result.data seharusnya tidak null")
            result.data?.let { data ->
                assertNotNull(data.incomeTrx, "IncomeTrxModel tidak boleh null")
                assertNotNull(data.expenseTrx, "ExpenseTrxModel tidak boleh null")

                // Assertions untuk membandingkan data dari API response dengan hasil repository
                assertEquals(mockIncomeTrx.trxId, data.incomeTrx?.trxId)
                assertEquals(mockExpenseTrx.trxId, data.expenseTrx?.trxId)
            }
        }
    }
}
