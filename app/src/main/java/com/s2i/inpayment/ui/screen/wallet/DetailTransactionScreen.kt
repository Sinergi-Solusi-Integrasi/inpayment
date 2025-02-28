    package com.s2i.inpayment.ui.screen.wallet

    import android.Manifest
    import android.content.Intent
    import android.graphics.Bitmap
    import android.os.Build
    import android.provider.Settings
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
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
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.ExperimentalMaterialApi
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBackIos
    import androidx.compose.material.icons.filled.CheckCircle
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.pullrefresh.pullRefresh
    import androidx.compose.material.pullrefresh.rememberPullRefreshState
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
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
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalDensity
    import androidx.compose.ui.platform.LocalView
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.core.app.NotificationManagerCompat
    import androidx.navigation.NavController
    import com.google.accompanist.permissions.ExperimentalPermissionsApi
    import com.google.accompanist.permissions.isGranted
    import com.google.accompanist.permissions.rememberPermissionState
    import com.s2i.inpayment.R
    import com.s2i.inpayment.ui.components.DetailTrxCard
    import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
    import com.s2i.inpayment.ui.components.saveBitmapToFile
    import com.s2i.inpayment.ui.components.shareScreenshot
    import com.s2i.inpayment.ui.theme.White
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
        val density = LocalDensity.current

        // State for permissions
        val storagePermissionState = rememberPermissionState(
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        )

        val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else {
            null // Notification permissions are not applicable for API < 33
        }

        // Function to check if notifications are enabled (for Realme or other devices)
        fun areNotificationsEnabled(): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        val transactionDetail by balanceViewModel.detailTrx.collectAsState()
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
            Spacer(modifier = Modifier.width(32.dp))
            Image(
                painter = painterResource(id = R.drawable.background1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            // Header di posisi atas tetap
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                // Spacer to push the content down
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                ) {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier
                            .size(16.dp)
//                            .background(
//                                color = MaterialTheme.colorScheme.onSecondary,
//                                shape = CircleShape
//                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIos,
                            contentDescription = "Close",
                            tint = White
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        text = "Transaction Detail",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        color = White
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
            // Konten scrollable di bawah header
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 72.dp) // Tambahkan jarak agar tidak menimpa header
            ) {
                DetailTrxCard(transactionDetail = transactionDetail?.data) // Panggil komponen `DetailTrxCard`
            }

            // Tombol Aksi
            // Tombol Share di bagian bawah
            Button(
                onClick = {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                notificationPermissionState != null &&
                                !notificationPermissionState.status.isGranted -> {
                            notificationPermissionState.launchPermissionRequest()
                        }

                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Please enable notifications for this app in system settings."
                                )
                            }
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                            )
                        }

                        !storagePermissionState.status.isGranted -> {
                            storagePermissionState.launchPermissionRequest()
                        }

                        else -> {
                            coroutineScope.launch {
                                val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                                val fileUri = saveBitmapToFile(context, bitmap)

                                fileUri?.let {
                                    shareScreenshot(context, it)
                                } ?: snackbarHostState.showSnackbar("Failed to save receipt")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
                    .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Share receipt")
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewDetailTransactionScreen() {
        DetailTransactionScreen(navController = NavController(LocalContext.current),transactionId = "dummy_transaction_id")
    }