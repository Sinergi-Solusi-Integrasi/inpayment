package com.s2i.inpayment.ui.screen.wallet

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.data.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.theme.BrightTeal20
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
    val isNetworkAvailable by NetworkUtils.isNetworkAvailable.collectAsState()
    val canClick = rememberSingleClickHandler()
    var isInternetStable by remember { mutableStateOf(true) }
    var rawAmount by remember { mutableStateOf(("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var isValidAmount by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val qrisState by qrisViewModel.qrisState.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val balanceState by balanceViewModel.balance.collectAsState()

    BackHandler(enabled = true) {
        if (canClick()) {
            coroutineScope.launch {
                navController.navigateUp()
            }
        }
    }

    LaunchedEffect(isNetworkAvailable) {
        delay(300) // debounce untuk stabilisasi
        isInternetStable = isNetworkAvailable
    }

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
            // Amount tanpa 00 dikirim ke QrisScreen
            val amountWithoutDecimals = rawAmount.toLongOrNull() ?: 0
            navController.navigate("qris_screen/${qrisState?.qrisCode}/${qrisState?.trxId}/$amountWithoutDecimals")
        }
    }

    Scaffold(
        topBar = {
            // Custom TopAppBar with perfect visual balance
            Box(modifier = Modifier
                .fillMaxWidth()) {
                TopAppBar(
                    title = { /* Title intentionally left empty */ },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (canClick()) {
                                    coroutineScope.launch {
                                        delay(300) // kasih waktu network content settle
                                        navController.navigateUp()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    // Add an empty action to balance the layout
                    actions = {
                        // Empty spacer with same size as back button for visual balance
                        Spacer(modifier = Modifier.width(48.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrightTeal20  // Header putih
                    )
                )

                // Centered title overlay
                Text(
                    text = "Payment Top Up",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 25.dp)
                )
            }
        },
        bottomBar = {
            // Bottom Bar (Tombol Lanjut)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrightTeal20)
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
                    Text("Continue")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrightTeal20)  // Latar luar berwarna abu-abu
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

                    if(!isInternetStable) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(24.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_networks_errors),
                                    contentDescription = "No Internet",
                                    modifier = Modifier.size(120.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Internet Connection or Unstable Internet Connection",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Silakan periksa koneksi Anda dan coba lagi.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
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
                                        val unformattedInput = newValue.text.replace(
                                            "[^\\d]".toRegex(),
                                            ""
                                        ) // Hapus semua titik
                                        if (unformattedInput.all { it.isDigit() }) { // Validasi hanya angka
                                            rawAmount = unformattedInput // Simpan nilai mentah
                                            val inputAmount = unformattedInput.toBigIntegerOrNull()
                                                ?.times(100.toBigInteger()) ?: BigInteger.ZERO
                                            val formattedText = if (inputAmount > BigInteger.ZERO) {
                                                RupiahFormatter.formatToRupiah(inputAmount / 100.toBigInteger()) // Format ke Rupiah tanpa "00" di belakang
                                            } else {
                                                ""
                                            }

                                            // Hitung posisi kursor baru berdasarkan panjang teks sebelum dan sesudah
                                            val cursorPosition =
                                                formattedText.length - (unformattedInput.length - newValue.selection.end)

                                            amount = TextFieldValue(
                                                text = formattedText,
                                                selection = TextRange(
                                                    cursorPosition.coerceIn(
                                                        0,
                                                        formattedText.length
                                                    )
                                                ) // Tetapkan posisi kursor baru
                                            )

                                            // Validasi jumlah
                                            isValidAmount =
                                                inputAmount in BigInteger("100")..BigInteger("2000000000")
                                            errorMessage =
                                                if (isValidAmount) null else "Minimal Rp 10.000 dan Maksimal Rp 20.000.000"
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
                                    text = "Saldo Tersedia : ${
                                        balanceState?.let {
                                            RupiahFormatter.formatToRupiah(
                                                it.balance
                                            )
                                        }
                                    }",
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
    // Tambahkan ini untuk menampilkan NetworkContent
    NetworkContent(
        onRetry = {
            coroutineScope.launch {
                balanceViewModel.fetchBalance()
            }
        }
    )
}