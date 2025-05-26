package com.s2i.inpayment.ui.screen.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.ui.components.HistoryCard
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.components.shimmer.balance.HistoryCardShimmer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.graphics.Color
import com.s2i.inpayment.ui.components.DateHeader
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun WalletHistoryScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    navController: NavController
) {
    val groupedTransaction by balanceViewModel.historyTransaction.collectAsState()
    val canClick = rememberSingleClickHandler()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Handle back button
    BackHandler(enabled = true) {
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
                balanceViewModel.fetchHistory()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    // Show loading indicator initially and when refreshing
    val showLoading = isStartupLoading || loading || isRefreshing

    // Fetch history only once when the screen first opens
    LaunchedEffect(Unit) {
        if (loading && isStartupLoading) {
            isStartupLoading = true
        } else if (!loading) {
            delay(500) // Optional delay to keep the loading indicator visible for a short time
            isStartupLoading = false
            balanceViewModel.fetchHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // TopAppBar is now outside the Box
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    ),
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
                        contentDescription = "Back",
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = DarkGreen,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
        // Content in a Box with pull refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightTeal20)
                .pullRefresh(state = pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Progress indicator when loading
                if (showLoading) {
                    CustomLinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showLoading) {
                        // Show shimmer when loading or refreshing
                        repeat(3) {
                            item {
                                HistoryCardShimmer()
                            }
                        }
                    } else {
                        groupedTransaction.forEach { (dateLabel, transactions) ->
                            if (transactions.isNotEmpty()) {
                                stickyHeader {
                                    DateHeader(dateLabel = dateLabel)
                                }
                                item {
                                    HistoryCard(
                                        transactions,
                                        onTransactionClick = { transactionId ->
                                            navController.navigate("detail_transaksi_screen/$transactionId")
                                        })
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWalletScreen() {
    WalletHistoryScreen(navController = NavController(LocalContext.current))
}