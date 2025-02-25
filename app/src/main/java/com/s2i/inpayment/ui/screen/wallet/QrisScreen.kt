package com.s2i.inpayment.ui.screen.wallet

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.QrisViewModel
import com.s2i.inpayment.utils.NotificationManagerUtil
import com.s2i.inpayment.utils.helper.generateQRCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.OutputStream
import androidx.compose.ui.draw.clip
import com.s2i.inpayment.ui.components.permission.hasAllPermissions
import com.s2i.inpayment.ui.theme.GreenTeal40

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Izin diperlukan untuk menyimpan QRIS", Toast.LENGTH_SHORT).show()
        }
    }

//    LaunchedEffect(Unit) {
//        while (true) {
//            val allPermissionsGranted = hasAllPermissions(context)
//            if (!allPermissionsGranted) {
//                navController.navigate("permission_screen") {
//                    popUpTo("home_screen") { inclusive = true }
//                }
//            }
//            delay(1000) // Check every second
//        }
//    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
//                Button(
//                    onClick = {
//                        if (ContextCompat.checkSelfPermission(
//                                context,
//                                android.Manifest.permission.READ_MEDIA_IMAGES
//                            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
//                        ) {
//                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
//                        }
//                        else {
//                            saveQRCode(context, qrisState)
//                        }
//
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Download")
//                }

                Button(
                    onClick = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        } else {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }

                        if (ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(permission)
                        } else {
                            saveQRCode(context, qrisState)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 300.dp, height = 55.dp),
                    shape = RoundedCornerShape(10.dp)
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
                                        "00" -> GreenTeal40  // Success
                                        "99" -> Color.Black // Pending
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

//private fun saveQRCode(context: Context, qrisState: String?) {
//    if (qrisState == null) {
//        Toast.makeText(context, "QRIS tidak tersedia.", Toast.LENGTH_SHORT).show()
//        return
//    }
//
//    val bitmap = generateQRCode(qrisState)
//    val contentValues = ContentValues().apply {
//        put(MediaStore.Images.Media.DISPLAY_NAME, "QRIS_${System.currentTimeMillis()}.jpg")
//        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/INPayment")
//        put(MediaStore.Images.Media.IS_PENDING, 1)
//    }
//
//    val resolver = context.contentResolver
//    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//    if (uri != null) {
//        var outputStream: OutputStream? = null
//        try {
//            outputStream = resolver.openOutputStream(uri)
//            if (outputStream != null) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            }
//            contentValues.clear()
//            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
//            resolver.update(uri, contentValues, null, null)
//            Toast.makeText(context, "QRIS berhasil disimpan ke galeri!", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(context, "Gagal menyimpan QRIS: ${e.message}", Toast.LENGTH_SHORT).show()
//        } finally {
//            outputStream?.close()
//        }
//    }
//}

private fun saveQRCode(context: Context, qrisState: String?) {
    if (qrisState == null) {
        Toast.makeText(context, "QRIS tidak tersedia.", Toast.LENGTH_SHORT).show()
        return
    }

    // Generate QRIS Bitmap
    val qrCodeBitmap = generateQRCode(qrisState)
    val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.template_qris)

    // Tentukan ukuran QR Code yang proporsional dengan template
    val qrCodeSize = (templateBitmap.width * 0.9).toInt() // 90% dari lebar template
    val scaledQRCodeBitmap = Bitmap.createScaledBitmap(qrCodeBitmap, qrCodeSize, qrCodeSize, true)

    // Gabungkan QRIS dan template
    val combinedBitmap = Bitmap.createBitmap(
        templateBitmap.width,
        templateBitmap.height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(combinedBitmap)
    canvas.drawBitmap(templateBitmap, 0f, 0f, null)

    // Hitung posisi QR Code agar berada di tengah template
    val left = (templateBitmap.width - scaledQRCodeBitmap.width) / 2f
    val top = (templateBitmap.height - scaledQRCodeBitmap.height) / 2f
    canvas.drawBitmap(scaledQRCodeBitmap, left, top, null)

    // Simpan bitmap yang sudah digabungkan ke galeri
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "QRIS_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/INPayment")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        var outputStream: OutputStream? = null
        try {
            outputStream = resolver.openOutputStream(uri)
            if (outputStream != null) {
                combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            Toast.makeText(context, "QRIS berhasil disimpan ke galeri!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal menyimpan QRIS: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream?.close()
        }
    }
}