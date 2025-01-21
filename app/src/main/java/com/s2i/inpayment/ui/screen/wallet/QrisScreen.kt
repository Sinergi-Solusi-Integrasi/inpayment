package com.s2i.inpayment.ui.screen.wallet


import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.QrisViewModel
import com.s2i.inpayment.utils.NotificationManagerUtil
import com.s2i.inpayment.utils.helper.generateQRCode
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrisScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    qrisState: String?,
    trxId: String?,
    amount: Int?, // tanpa format desimal 00
    qrisViewModel: QrisViewModel = koinViewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    val orderQrisState by qrisViewModel.orderQrisState.collectAsState() // Observe orderQuery result
    val sessionManager = SessionManager(context)
    val userId = sessionManager.getFromPreference(SessionManager.KEY_USER_ID).toString()
    var isValidAmount by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isStartupLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()
    // State untuk BottomSheet
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
        coroutineScope.launch {
            delay(500)
            balanceViewModel.fetchBalance()
            isStartupLoading = false
        }
    }

    LaunchedEffect(trxId) {
        trxId?.let {
            var lastState: String? = null
            while (true) {
                try {
                    qrisViewModel.orderQuery(it)

                    //periksa status api
                    val orderState = orderQrisState
                    if (orderState !=null) {
                        val currentStatus = orderState.rCode
                        Log.d("QrisScreen", "Order state: ${orderState.rCode}, message: ${orderState.message}")

                        // Jika status berubah, tampilkan notifikasi
                        if(currentStatus != lastState) {
                            val statusMessage = when (orderState.rCode) {
                                "00" -> "Pembayaran Berhasil"
                                "99" -> "Pembayaran Pending"
                                else -> "Pembayaran Gagal: ${orderState.message}"
                            }

                            // Tampilkan notifikasi
                            NotificationManagerUtil.showNotification(context, trxId = it, title = "Status Pembayaran", messageBody = statusMessage)
                            lastState = currentStatus
                        }


                        // Jika pembayaran berhasil atau gagal, hentikan polling
                        if (currentStatus == "00") {
                            Log.d("QrisScreen", "Stopping polling: Pembayaran berhasil.")
                            qrisViewModel.topup(
                                userId = userId,
                                referenceId = trxId,
                                amount = amount?: 0,
                                feeAmount = 0,
                                paymentMethod = "QRIS"
                            )
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.e("QrisScreen", "Error during order query: ${e.message}")
                }

                // Tunggu 5 detik sebelum polling berikutnya
                delay(1000L)
            }
        }
    }

    Scaffold(
        topBar = {
            // Header
            TopAppBar(
                title = {
                    Text(
                        text = "Payment QRIS",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        navController.navigate("home_screen") {
//                            popUpTo("qris_screen") { inclusive = true }
//                        }
                        showBottomSheet = true
                    }) {
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
                    onClick = { /* Aksi ketika tombol lanjut ditekan */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValidAmount,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isValidAmount) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                ) {
                    Text("Download")
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (qrisState !=null) {
                                // Template QRIS dengan barcode di tengah
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(3/4f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.template_qris),
                                        contentDescription = "Template QRIS",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Generate QRCode from qrisstate
                                    AndroidView(
                                        factory = { context ->
                                            ImageView(context).apply {
                                                val qrisBitmap = generateQRCode(qrisState)
                                                setImageBitmap(qrisBitmap)
                                            }
                                        },
                                        modifier = Modifier
                                            .size(250.dp)
                                            .padding(16.dp)

                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scan QR Code untuk melanjutkan Pembayaran",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    textAlign = TextAlign.Center
                                )

                                // Display QRIS order status (optional)
                                orderQrisState?.let { orderState ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    val statusColor = when (orderState.rCode) {
                                        "00" -> Color.Green  // Success
                                        "99" -> Color.Yellow // Pending
                                        else -> Color.Red    // Error
                                    }
                                    Text(
                                        text = when (orderState.rCode) {
                                            "00" -> "Pembayaran Berhasil"
                                            "99" -> "Pembayaran Pending"
                                            else -> "Pembayaran Gagal: ${orderState.message}"
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = statusColor,
                                        textAlign = TextAlign.Center
                                    )
                                    orderState.trxId?.let { trxId ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Transaction ID: $trxId",
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                            } else {
                                Text(
                                    text = "QR Code Tidak Ditemukan",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //ReusableBottomSheet
    if(showBottomSheet){
        ReusableBottomSheet(
            message = "Apakah Anda yakin ingin membatalkan pembayaran ini?",
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            },
            content = {
                // Add the content here
                Button(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                            navController.navigate("home_screen") {
                                popUpTo("qris_screen") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Quit", style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}

