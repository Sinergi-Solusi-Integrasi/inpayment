package com.s2i.inpayment.ui.screen.wallet

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
//import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.DetailTrxCard
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.components.saveBitmapToFile
import com.s2i.inpayment.ui.components.shareScreenshot
import com.s2i.inpayment.ui.components.shimmer.balance.PaymentMethodItemShimmer
import com.s2i.inpayment.ui.theme.BrightTeal20
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun PaymentMethodsScreen(
    navController: NavController,
) {

    val canClick = rememberSingleClickHandler()
    var isStartupLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = true) {
        if (canClick()) {
            scope.launch {
                navController.navigateUp()
            }
        }
    }

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
    val showLoading = isStartupLoading || isRefreshing

    // Memanggil fetchHistory hanya sekali ketika layar pertama kali dibuka
    LaunchedEffect(Unit){
        scope.launch {
            delay(500)
            isStartupLoading = false
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(BrightTeal20)
    ) {
        Spacer(modifier = Modifier.width(32.dp))
        // Header di posisi atas tetap
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            // Spacer to push the content down
            Spacer(modifier = Modifier.height(24.dp))

            // Centered header with Close button on the left
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            ) {
                // Close button on the left
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick = {
                            if (canClick()) {
                                scope.launch {
                                    navController.navigateUp()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-14).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Centered title
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Top Up",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    )
                }

                // Empty space with same weight on the right for balance
                Box(
                    modifier = Modifier.weight(1f)
                )
            }

            // Tampilkan loading jika masih dalam kondisi loading
            if (showLoading) {
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                repeat(3) {
                    PaymentMethodItemShimmer()
                }
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
                        navController.navigate("payment_screen") {
                            launchSingleTop = true
                        }
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
            .background(Color.White)
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