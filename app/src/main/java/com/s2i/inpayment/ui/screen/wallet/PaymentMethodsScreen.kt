package com.s2i.inpayment.ui.screen.wallet

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun PaymentMethodsScreen(
    navController: NavController,
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


    // Function to check if notifications are enabled (for Realme or other devices)
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

//    val transactionDetail by balanceViewModel.detailTrx.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
//    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
//                balanceViewModel.fetchDetailTrx(transactionId)
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    // Show loading indicator initially and when refreshing
//    val showLoading = loading || isRefreshing
//
    // Memanggil fetchHistory hanya sekali ketika layar pertama kali dibuka
    LaunchedEffect(Unit){
//        balanceViewModel.fetchDetailTrx(transactionId)
//        if (loading && isStartupLoading) {
//            isStartupLoading = true
//        } else if (!loading) {
//            delay(500) // Optional delay to keep the loading indicator visible for a short time
//            isStartupLoading = false
//        }
        scope.launch {
            delay(500)
            isStartupLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.width(32.dp))
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
                Spacer(modifier = Modifier.width(24.dp))

                Text(
                    text = "Top Up",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

            }
            // Tampilkan loading jika masih dalam kondisi loading
            if (isStartupLoading) {
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))

                // Title "Pilih Metode"
                Text(
                    text = "Payment Methods",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // QRIS Section
                Text(
                    text = "QRIS",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                PaymentMethodItem(
                    imageRes = R.drawable.qris_logo,
                    title = "QRIS",
                    onClick = {
                        // Aksi untuk QRIS
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Virtual Account Section
//                Text(
//                    text = "Virtual Account",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )

//                PaymentMethodItem(
//                    imageRes = R.drawable.bca_logo,
//                    title = "m-BCA",
//                    onClick = {
//                        // Aksi untuk m-BCA
//                    }
//                )
//
//                PaymentMethodItem(
//                    imageRes = R.drawable.bca_logo,
//                    title = "myBCA",
//                    onClick = {
//                        // Aksi untuk myBCA
//                    }
//                )
//
//                PaymentMethodItem(
//                    imageRes = R.drawable.bni_logo,
//                    title = "BNI Mobile",
//                    onClick = {
//                        // Aksi untuk BNI Mobile
//                    }
//                )
//
//                PaymentMethodItem(
//                    imageRes = R.drawable.bri_logo,
//                    title = "BRI Mobile",
//                    onClick = {
//                        // Aksi untuk BRI Mobile
//                    }
//                )
            }
        }
    }
}

// Reusable Composable for Payment Item
@Composable
fun PaymentMethodItem(
    imageRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(60.dp)
                .padding(end = 16.dp)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}