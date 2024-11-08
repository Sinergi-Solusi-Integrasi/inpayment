package com.s2i.inpayment.ui.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.TransactionItem
import com.s2i.inpayment.ui.components.custome.LogoIndicator
import com.s2i.inpayment.ui.components.custome.LogoWithBeam
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.screen.wallet.BalanceCard
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.gradientBrush
import com.s2i.inpayment.ui.theme.triGradientBrush
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

// In HomeScreen.kt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    balanceViewModel: BalanceViewModel = koinViewModel(),
    username: String
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                balanceViewModel.fetchBalance()
                balanceViewModel.fetchTriLastTransaction()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )
    val transactions by balanceViewModel.triLastTransaction.collectAsState()
    //toogle visible balance
    val balanceState by balanceViewModel.balance.collectAsState()
    val textMeasurer = rememberTextMeasurer()
    var isBalanceValid by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        balanceViewModel.fetchBalance() // Trigger fetching the balance when screen is launched
        balanceViewModel.fetchTriLastTransaction() // Trigger fetching the transaction when screen is launched
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
            .background(Color.White)
    ) {

        val cardOffset by animateIntAsState(
            targetValue = when {
                isRefreshing -> 250
                pullRefreshState.progress in 0f..1f -> (250 * pullRefreshState.progress).roundToInt()
                pullRefreshState.progress > 1f -> (250 + ((pullRefreshState.progress - 1f) * .1f) * 100).roundToInt()
                else -> 0
            }, label = "cardOffset"
        )

        val cardRotation by animateFloatAsState(
            targetValue = when {
                isRefreshing || pullRefreshState.progress > 1f -> 5f
                pullRefreshState.progress > 0f -> 5 * pullRefreshState.progress
                else -> 0f
            }, label = "cardRotation"
        )

        // Section for the top part with the balance card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = gradientBrush()) // The color from your example
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Header with Logo, Notification, and Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pullRefresh(state = pullRefreshState)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Menjaga jarak antara logo dan profil
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo on the left
                Image(
                    painter = painterResource(id = R.drawable.second_logo), // Replace with the correct logo resource
                    contentDescription = "Logo",
                    modifier = Modifier.size(135.dp, 27.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Notification icon in the center
                Row(
                    verticalAlignment = Alignment.CenterVertically // Sejajarkan notifikasi dan profil secara vertikal
                ) {
                    IconButton(onClick = { /* Handle notification click */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Profile picture on the right
                    Image(
                        painter = painterResource(id = R.drawable.ic_people), // Replace with the correct profile image
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(32.dp) // Ukuran avatar diubah menjadi 32.dp
                            .clip(CircleShape) // Bentuk lingkaran
                            .background(Color.Gray, shape = CircleShape) // Background lingkaran
                            .clickable {
                                // Aksi ketika di klik
                                navController.navigate("profile_screen") {
                                    popUpTo("home_screen") { inclusive = false }
                                }
                            }
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Balance Card
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(1) { index ->
                    Box(
                        modifier = Modifier
                            .zIndex((100 - index).toFloat())
                            .fillMaxWidth()
                            .graphicsLayer {
                                rotationZ = cardRotation * if (index % 2 == 0) 1 else -1
                                translationY = (cardOffset * ((5f - (index + 1)) / 5f)).dp
                                    .roundToPx()
                                    .toFloat()
                            }
                    ){
                        BalanceCard(balanceState, isBalanceValid) { isBalanceValid = !isBalanceValid }
                    }

                    // Top section with balance card
//                    BalanceCard(balanceState, isBalanceVisible) { isBalanceVisible = !isBalanceVisible }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Section for Pemasukan and Pengeluaran
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Card for Pemasukan
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp), // Space between the two cards
                            elevation = CardDefaults.elevatedCardElevation(4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = "Pemasukan", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Rp 100.000", style = MaterialTheme.typography.bodyLarge, color = Color.Green)
                                Text(text = "Top-Up M-BCA", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        // Card for Pengeluaran
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp), // Space between the two cards
                            elevation = CardDefaults.elevatedCardElevation(4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = "Pengeluaran", style = MaterialTheme.typography.titleMedium)
                                Text(text = "-Rp 9.500", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                                Text(text = "GT-Fatmawati 1", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp) // Adjust height as needed
//                    .padding(8.dp), // Padding around the card
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(DarkTeal40) // Apply the gradient brush here
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Saldo",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = Color.White
//                        )
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = if (isBalanceValid) balanceState?.let { RupiahFormatter.formatToRupiah(it.balance) } ?: "Loading..." else "****", // Update balance value
//                                style = MaterialTheme.typography.displaySmall,
//                                color = Color.White
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            IconButton(onClick = { isBalanceValid = !isBalanceValid }) {
//                                Icon(
//                                    imageVector = if(isBalanceValid) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                                    contentDescription = if (isBalanceValid) "Hide Balance" else "Show Balance",
//                                    tint = Color.White
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Row(
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            // Top-Up Button
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ic_topup), // Replace with top-up icon
//                                    contentDescription = "Top Up",
//                                    tint = Color.White
//                                )
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Top Up", color = Color.White)
//                                Spacer(modifier = Modifier.width(8.dp))
//                                //riwayat
//                                Icon(
//                                    Icons.Default.Receipt, // Replace with history icon
//                                    contentDescription = "Riwayat",
//                                    modifier = Modifier
//                                        .size(16.dp),
//                                    tint = Color.White
//                                )
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text("Riwayat", color = Color.White)
//                            }
//                        }
//                    }
//                }
//            }

        }

        // Section for transaction history with rounded corners at the top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 420.dp) // Start this part below the top section
                .background(Color.White, shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp))) // Rounded corner for the bottom section
                .padding(16.dp)
        ) {
            // Transaction History Section
            Text(text = "Riwayat Transaksi", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp), // Limit height for the transaction history
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                shape = MaterialTheme.shapes.medium // Corner radius for the card
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(32.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions.take(3)) { transaction -> // Show the latest 3 transactions
                            TransactionItem(
                                title = if (transaction.title.isEmpty()) " " else transaction.title,
                                description = transaction.trxType,
                                amount = if (transaction.cashFlow == "MONEY_OUT") "-Rp. ${ transaction.amount}" else "+Rp. ${ transaction.amount}",
                                isNegative = transaction.cashFlow == "MONEY_OUT"
                            )
                        }
                    }
                    // "See More" button
                    TextButton(onClick = { /* Handle see more */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Lihat Riwayat",
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        LogoIndicator(isRefreshing, pullRefreshState)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen(){
//    HomeScreen(HomeViewModel(), sessionManager = sessionsManager)
//}