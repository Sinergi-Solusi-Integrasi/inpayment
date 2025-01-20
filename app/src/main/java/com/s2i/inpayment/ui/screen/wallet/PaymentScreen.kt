package com.s2i.inpayment.ui.screen.wallet

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.data.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.QrisViewModel
import com.s2i.inpayment.utils.helper.generateCurrentTime
import com.s2i.inpayment.utils.helper.generateSignature
import com.s2i.inpayment.utils.helper.generateTrxId
import org.koin.androidx.compose.koinViewModel
import java.math.BigInteger

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    qrisViewModel: QrisViewModel = koinViewModel(),
    navController: NavController
) {
    var rawAmount by remember { mutableStateOf(("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var isValidAmount by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val qrisState by qrisViewModel.qrisState.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val balanceState by balanceViewModel.balance.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                delay(2000)
                balanceViewModel.fetchBalance()
                isRefreshing = false
            }
        }
    )

    // Simulate loading on startup
    LaunchedEffect(Unit) {
        amount = TextFieldValue("")
        isValidAmount = false
        errorMessage = null
        coroutineScope.launch {
            delay(500)
            balanceViewModel.fetchBalance()
            isStartupLoading = false
        }
    }

    val mid = BuildConfig.MID
    val tid = BuildConfig.TID
    val clientid = BuildConfig.CLIENT_ID
    val clientsecret = BuildConfig.CLIENT_SECRETE

    val trxid = generateTrxId()
    val waktu = generateCurrentTime()
    val signature = generateSignature(mid, tid, waktu, clientsecret)
    Log.d("MID", "MID: $mid")
    Log.d("TID", "TID: $tid")
    Log.d("clientid", "Client_ID: $clientid")
    Log.d("clientsecret", "CLIENT_SECRETE: $clientsecret")
    Log.d("TxnDateDebug", "Generated Date: $waktu")
    Log.d("SignatureDebug", "Generated Signature: $signature")

    LaunchedEffect(qrisState) {
        if (qrisState?.qrisCode != null) {
            navController.navigate("qris_screen/${qrisState?.qrisCode}/${qrisState?.trxId}")
        }
    }

    Scaffold(
        topBar = {
            // Header
            TopAppBar(
                title = {
                    Text(
                        text = "Payment Top Up",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White  // Header putih
                )
            )
        },
        bottomBar = {
            // Bottom Bar (Tombol Lanjut)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .navigationBarsPadding()  // Hindari tertutup oleh system bar
            ) {
                Button(
                    onClick = {
                    /* Aksi ketika tombol lanjut ditekan */
                        val txnAmount = rawAmount.toLongOrNull()
                        if (txnAmount != null && txnAmount > 0) {
//                            val formattedAmount = txnAmount.toString()
                            val formattedAmount = "${txnAmount}00"
                            Log.d("AmountDebug", "Formatted Amount: $formattedAmount")

                            qrisViewModel.sendQris(
                                mid = mid,
                                tid = tid,
                                trxid = trxid,
                                amount = formattedAmount,
                                waktu = waktu,
                                signature = signature,
                                clientid = clientid
                            )
                        } else {
                            errorMessage = "Masukkan jumlah yang valid"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValidAmount,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isValidAmount) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                ) {
                    Text("Lanjut")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F0F0))  // Latar luar berwarna abu-abu
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                if (isStartupLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))

                    // QRIS Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.qris_logo),
                                    contentDescription = "QRIS",
                                    tint = Color.Unspecified,  // Ikon QRIS warna asli
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "QRIS",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
//                                    if (newValue.text.all { it.isDigit() }) {
//                                        amount = newValue
//                                        val inputAmount = newValue.text.toLongOrNull() ?: 0
//                                        if (inputAmount in 10000..20000000) {
//                                            isValidAmount = true
//                                            errorMessage = null
//                                        } else {
//                                            isValidAmount = false
//                                            errorMessage = "Minimal Rp 10.000 dan Maksimal Rp 20.000.000"
//                                        }
//                                    } else if (newValue.text.isEmpty()) {
//                                        isValidAmount = false
//                                        errorMessage = null
//                                        amount = newValue
//                                    }

                                    val unformattedInput = newValue.text.replace("[^\\d]".toRegex(), "") // Hapus semua titik
                                    if (unformattedInput.all { it.isDigit() }) { // Validasi hanya angka
                                        rawAmount = unformattedInput // Simpan nilai mentah
                                        val inputAmount = unformattedInput.toBigIntegerOrNull()?.times(100.toBigInteger()) ?: BigInteger.ZERO
                                        val formattedText = if (inputAmount > BigInteger.ZERO) {
                                            RupiahFormatter.formatToRupiah(inputAmount / 100.toBigInteger()) // Format ke Rupiah tanpa "00" di belakang
                                        } else {
                                            ""
                                        }

                                        // Hitung posisi kursor baru berdasarkan panjang teks sebelum dan sesudah
                                        val cursorPosition = formattedText.length - (unformattedInput.length - newValue.selection.end)

                                        amount = TextFieldValue(
                                            text = formattedText,
                                            selection = TextRange(cursorPosition.coerceIn(0, formattedText.length)) // Tetapkan posisi kursor baru
                                        )

                                        // Validasi jumlah
                                        isValidAmount = inputAmount in BigInteger("100")..BigInteger("2000000000")
                                        errorMessage = if (isValidAmount) null else "Minimal Rp 10.000 dan Maksimal Rp 20.000.000"
                                    } else if (newValue.text.isEmpty()) { // Jika teks kosong
                                        rawAmount = ""
                                        amount = newValue
                                        isValidAmount = false
                                        errorMessage = null
                                    }
                                },
                                placeholder = { Text("Rp. 0", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                isError = errorMessage != null
                            )

                            errorMessage?.let {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Saldo Tersedia : ${balanceState?.let { RupiahFormatter.formatToRupiah(it.balance)}}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = "Maks. Saldo BABLAS Rp 20.000.000",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

