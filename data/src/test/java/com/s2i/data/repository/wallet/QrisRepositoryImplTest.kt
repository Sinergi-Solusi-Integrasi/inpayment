package com.s2i.data.repository.wallet

import com.s2i.data.model.wallet.TopupData
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.client.WalletServices
import com.s2i.data.remote.request.wallet.TopupRequest
import com.s2i.data.remote.response.wallet.CheckQrisResponse
import com.s2i.data.remote.response.wallet.QrisResponse
import com.s2i.data.remote.response.wallet.TopupResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class QrisRepositoryImplTest {

    private lateinit var walletServices: WalletServices
    private lateinit var apiServices: ApiServices
    private lateinit var repository: QrisRepositoryImpl

    @BeforeEach
    fun setup() {
        walletServices = mock(WalletServices::class.java)
        apiServices = mock(ApiServices::class.java)
        repository = QrisRepositoryImpl(walletServices, apiServices)
    }

    @Test
    fun `qrisCreate returns expected model`() = runTest {
        val qrisResponse = QrisResponse(
            rCode = "00",
            message = "Success",
            trxId = "trx123",
            qrisCode = "QR12345",
            reqMsgId = "req123"
        )
        `when`(
            walletServices.createQRIS(
                "mid", "tid", "trx123", "10000", "20230323", "sig123", "client123"
            )
        ).thenReturn(qrisResponse)

        val result = repository.qrisCreate(
            mid = "mid",
            tid = "tid",
            trxid = "trx123",
            amount = "10000",
            waktu = "20230323",
            signature = "sig123",
            clientid = "client123"
        )

        assertEquals("00", result.rCode)
        assertEquals("Success", result.message)
        assertEquals("QR12345", result.qrisCode)
    }

    @Test
    fun `orderQuerys returns expected OrderQrisModel`() = runTest {
        val checkQrisResponse = CheckQrisResponse(
            rCode = "00",
            message = "Success",
            trxId = "trx456"
        )
        `when`(walletServices.orderQuery("trx456")).thenReturn(checkQrisResponse)

        val result = repository.orderQuerys("trx456")

        assertEquals("00", result.rCode)
        assertEquals("Success", result.message)
        assertEquals("trx456", result.trxId)
    }

    @Test
    fun `topUp returns expected TopupQris`() = runTest {
        val topupData = TopupData(
            transactionId = "top123",
            userId = "user1",
            userName = "John Doe",
            referenceId = "ref123",
            amount = 10000,
            feeAmount = 500,
            paymentMethod = "VA",
            status = "PENDING",
            datetime = "2025-03-23T10:00:00"
        )
        val topupResponse = TopupResponse(
            code = 200,
            message = "Topup created",
            data = topupData
        )
        val expectedRequest = TopupRequest(
            userId = "user1",
            referenceId = "ref123",
            amount = 10000,
            feeAmount = 500,
            paymentMethod = "VA"
        )

        `when`(apiServices.topup(expectedRequest)).thenReturn(topupResponse)

        val result = repository.topUp(
            userId = "user1",
            referenceId = "ref123",
            amount = 10000,
            feeAmount = 500,
            paymentMethod = "VA"
        )

        assertEquals(200, result.code)
        assertEquals("Topup created", result.message)
        assertEquals("top123", result.data.transactionId)
    }
}
