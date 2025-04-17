package com.s2i.inpayment.ui.screen.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.ui.components.HistoryCard
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.custome.LogoIndicator
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.components.shimmer.balance.HistoryCardShimmer
import com.s2i.inpayment.ui.theme.White30
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.ArrowBack
import com.s2i.inpayment.ui.components.DateHeader
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun WalletHistoryScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    navController: NavController
){
    val groupedTransaction by  balanceViewModel.historyTransaction.collectAsState()
    val canClick = rememberSingleClickHandler()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Handle initial loading

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
                balanceViewModel.fetchHistory()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    // Show loading indicator initially and when refreshing
    val showLoading = isStartupLoading || loading || isRefreshing


    // Memanggil fetchHistory hanya sekali ketika layar pertama kali dibuka
   LaunchedEffect(Unit){
       if (loading && isStartupLoading) {
           isStartupLoading = true
       } else if (!loading) {
           delay(500) // Optional delay to keep the loading indicator visible for a short time
           isStartupLoading = false
           balanceViewModel.fetchHistory()
       }
   }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightTeal20)
            .windowInsetsPadding(WindowInsets.statusBars)
            .pullRefresh(state = pullRefreshState)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            if (showLoading) {
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ){
                IconButton(
                    onClick = {
                        if (canClick()) {
                            scope.launch {
                                navController.navigateUp()
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Center-aligned title
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }
                // Empty space with same size as the back button for symmetry
                Spacer(modifier = Modifier.size(48.dp))

            }
            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                if (showLoading) {
                    // Tampilkan shimmer saat loading atau refreshing
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

//        PullRefreshIndicator(
//            isRefreshing,
//            pullRefreshState,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//        )
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewWalletScreen() {
   WalletHistoryScreen(navController = NavController(LocalContext.current))
}