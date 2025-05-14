package com.s2i.inpayment.ui.screen.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
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

    // Handle back button
    BackHandler {
        if (canClick()) {
            scope.launch {
                navController.navigateUp()
            }
        }
    }

    // Setup pull-to-refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                // balanceViewModel.fetchDetailTrx(transactionId)
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    val showLoading = isStartupLoading || isRefreshing

    // Initial data loading
    LaunchedEffect(Unit) {
        scope.launch {
            delay(500)
            isStartupLoading = false
        }
    }

    PaymentMethodsContent(
        navController = navController,
        canClick = canClick,
        showLoading = showLoading,
        pullRefreshState = pullRefreshState,
        scope = scope
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PaymentMethodsContent(
    navController: NavController,
    canClick: () -> Boolean,
    showLoading: Boolean,
    pullRefreshState: PullRefreshState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightTeal20)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            PaymentMethodsHeader(
                onBackClick = {
                    if (canClick()) {
                        scope.launch {
                            navController.navigateUp()
                        }
                    }
                }
            )

            // Content
            if (showLoading) {
                LoadingContent()
            } else {
                PaymentMethodsList(navController)
            }
        }
    }
}

@Composable
private fun PaymentMethodsHeader(
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        // Back button
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Title
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Top Up",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Empty space for balance
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LoadingContent() {
    CustomLinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    repeat(3) {
        PaymentMethodItemShimmer()
    }
}

@Composable
private fun PaymentMethodsList(navController: NavController) {
    Spacer(modifier = Modifier.height(24.dp))

    // Section title
    Text(
        text = "Payment Methods",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Payment methods section
    Text(
        text = "QRIS",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(8.dp))

    PaymentMethodItem(
        imageRes = R.drawable.qris_logo,
        title = "QRIS",
        onClick = {
            navController.navigate("payment_screen") {
                launchSingleTop = true
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun PaymentMethodItem(
    imageRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}