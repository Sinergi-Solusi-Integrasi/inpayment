package com.s2i.inpayment.ui.screen.wallet


import android.os.Build
import android.widget.ImageView
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.utils.helper.generateQRCode
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrisScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    qrisState: String?,
    navController: NavController
) {

    var isValidAmount by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
        coroutineScope.launch {
            delay(500)
            balanceViewModel.fetchBalance()
            isStartupLoading = false
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
                    onClick = { /* Aksi ketika tombol lanjut ditekan */ },
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
                            if (qrisState !=null) {
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
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scan QR Code untuk melanjutkan Pembayaran",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    textAlign = TextAlign.Center
                                )
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
}

