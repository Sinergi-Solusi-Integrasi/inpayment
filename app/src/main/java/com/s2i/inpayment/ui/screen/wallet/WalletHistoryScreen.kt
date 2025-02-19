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
import com.s2i.inpayment.ui.theme.White30
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletHistoryScreen(
    balanceViewModel: BalanceViewModel = koinViewModel(),
    navController: NavController
){
    val groupedTransaction by  balanceViewModel.historyTransaction.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Handle initial loading

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
    val showLoading = loading || isRefreshing


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
            .pullRefresh(state = pullRefreshState)

    ) {
        if (isStartupLoading) {
            CustomLinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ){
            if (showLoading) {
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Spacer to push the content down
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            ){
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
                    text = "History",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                groupedTransaction.forEach { (dateLabel, transactions) ->
                    item {
                        HistoryCard(dateLabel, transactions, onTransactionClick = { transactionId ->
                            navController.navigate("detail_transaksi_screen/$transactionId")
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewWalletScreen() {
   WalletHistoryScreen(navController = NavController(LocalContext.current))
}