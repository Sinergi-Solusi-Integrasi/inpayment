    package com.s2i.inpayment.ui.screen.wallet

    import android.Manifest
    import android.app.Activity
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.os.Build
    import android.provider.Settings
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.navigationBarsPadding
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.ExperimentalMaterialApi
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ChangeCircle
    import androidx.compose.material.icons.filled.CheckCircle
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material.icons.filled.DownloadForOffline
    import androidx.compose.material.icons.filled.IosShare
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.pullrefresh.pullRefresh
    import androidx.compose.material.pullrefresh.rememberPullRefreshState
    import androidx.compose.material3.Button
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.SnackbarHostState
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.ComposeView
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalDensity
    import androidx.compose.ui.platform.LocalView
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.viewinterop.AndroidView
    import androidx.core.app.NotificationManagerCompat
    import androidx.core.content.ContextCompat
    import androidx.navigation.NavController
    import com.google.accompanist.permissions.ExperimentalPermissionsApi
    import com.google.accompanist.permissions.isGranted
    import com.google.accompanist.permissions.rememberPermissionState
    import com.s2i.inpayment.ui.components.DetailTrxCard
    import com.s2i.inpayment.ui.components.button.SplitButton
    import com.s2i.inpayment.ui.components.captureView
    import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
    import com.s2i.inpayment.ui.components.saveBitmapToFile
    import com.s2i.inpayment.ui.components.shareScreenshot
    import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import org.koin.compose.viewmodel.koinViewModel

    @OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
    @Composable
    fun DetailTransactionScreen(
        balanceViewModel: BalanceViewModel = koinViewModel(),
        navController: NavController,
        transactionId: String
    ) {

        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val view = LocalView.current
        val transactionDetail by balanceViewModel.detailTrx.collectAsState()

        val transactionView = remember { mutableStateOf<ComposeView?>(null) }
        var excludeImage by remember { mutableStateOf(false) }


        // Function to check if notifications are enabled (for Realme or other devices)
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Izin diperlukan untuk menyimpan QRIS", Toast.LENGTH_SHORT).show()
            }
        }

        var isStartupLoading by remember { mutableStateOf(true) }
        val loading by balanceViewModel.loading.collectAsState()
        var isRefreshing by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    balanceViewModel.fetchDetailTrx(transactionId)
                    delay(2000) // simulate refresh delay
                    isRefreshing = false
                }
            }
        )

        // Show loading indicator initially and when refreshing
        val showLoading = loading || isRefreshing

        // Memanggil fetchHistory hanya sekali ketika layar pertama kali dibuka
        LaunchedEffect(Unit){
            balanceViewModel.fetchDetailTrx(transactionId)
            if (loading && isStartupLoading) {
                isStartupLoading = true
            } else if (!loading) {
                delay(500) // Optional delay to keep the loading indicator visible for a short time
                isStartupLoading = false
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            // Spacer to push the content down
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // HEADER (TIDAK SCROLL)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { navController.navigateUp() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Transaction Detail",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (isStartupLoading) {
                        CustomLinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                // KONTEN YANG BISA DI-SCROLL
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Hanya konten ini yang bisa di-scroll
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        AndroidView(
                            factory = { context ->
                                ComposeView(context).apply {
                                    transactionView.value = this
                                    setContent {
                                        DetailTrxCard(transactionDetail = transactionDetail?.data, excludeImage = excludeImage)
                                    }
                                }

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // FOOTER (TIDAK SCROLL)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .align(Alignment.CenterHorizontally)
                        .navigationBarsPadding()
                        .padding(8.dp)
                ) {
                    val buttons = listOf(
                        Pair(Icons.Filled.IosShare, "Share"),
                        Pair(Icons.Filled.DownloadForOffline, "Downloads")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = if (buttons.size == 2) Arrangement.SpaceAround else Arrangement.SpaceEvenly, // Beri jarak antar tombol
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        buttons.forEach { (icon, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .padding(horizontal = if (buttons.size == 2) 8.dp else 0.dp)
                            ) {
                                SplitButton(
                                    onClick = {
                                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        } else {
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        }

                                        if (ContextCompat.checkSelfPermission(
                                            context,
                                            permission
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            permissionLauncher.launch(permission)
                                            return@SplitButton
                                        }
                                        Log.d("SplitButton", "Clicked $label")
                                        when(label) {
                                            "Share" -> coroutineScope.launch {
                                                excludeImage = true
                                                delay(300)
                                                transactionView.value?.let {
                                                    val bitmap = captureView(it) { excludeImage = it }
                                                    val fileUri = saveBitmapToFile(context, bitmap)
                                                    excludeImage = false

                                                    fileUri?.let {
                                                        shareScreenshot(context, it)
                                                        Toast.makeText(context, "Receipt share successfully", Toast.LENGTH_SHORT).show()
                                                        snackbarHostState.showSnackbar("Receipt share successfully")
                                                    }
                                                        ?: snackbarHostState.showSnackbar("Failed to save receipt")
                                                }
                                            }
                                            "Downloads" -> coroutineScope.launch {
                                                excludeImage = true
                                                delay(300)
                                                transactionView.value?.let {
                                                    val bitmap = captureView(it) { excludeImage = it }
                                                    val fileUri = saveBitmapToFile(context, bitmap)
                                                    excludeImage = false

                                                    fileUri?.let {
                                                        Toast.makeText(context, "Receipt saved successfully", Toast.LENGTH_SHORT).show()
                                                        snackbarHostState.showSnackbar("Receipt saved successfully")
                                                    }
                                                        ?: snackbarHostState.showSnackbar("Failed to save receipt")
                                                }
                                            }
                                        }
                                    },
                                    icon = icon,
                                    label = label,
                                    isSelected = false,
                                )
                            }
                        }
                    }
                }
            }
        }

    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewDetailTransactionScreen() {
        DetailTransactionScreen(navController = NavController(LocalContext.current),transactionId = "dummy_transaction_id")
    }