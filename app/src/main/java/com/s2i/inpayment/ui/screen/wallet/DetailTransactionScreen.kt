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
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.ExperimentalMaterialApi
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.CheckCircle
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.pullrefresh.pullRefresh
    import androidx.compose.material.pullrefresh.rememberPullRefreshState
    import androidx.compose.material3.Button
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
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
    import com.s2i.inpayment.ui.components.DetailTrxCard
    import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
    import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import org.koin.compose.viewmodel.koinViewModel

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DetailTransactionScreen(
        balanceViewModel: BalanceViewModel = koinViewModel(),
        navController: NavController,
        transactionId: String
    ) {

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
                        text = "Transaction Detail",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
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

        }
    }


    @Preview(showBackground = true)
    @Composable
    fun PreviewDetailTransactionScreen() {
        DetailTransactionScreen(navController = NavController(LocalContext.current),transactionId = "dummy_transaction_id")
    }