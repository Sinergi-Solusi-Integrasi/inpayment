package com.s2i.inpayment.ui.screen.wallet

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.components.shimmer.balance.PaymentMethodItemShimmer
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
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
        ) {
            // TopAppBar instead of custom header
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Top Up",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (canClick()) {
                                scope.launch {
                                    navController.navigateUp()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            // Content with proper padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Content
                if (showLoading) {
                    LoadingContent()
                } else {
                    PaymentMethodsList(navController)
                }
            }
        }
    }
}

// PaymentMethodsHeader is removed since we're using TopAppBar now

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