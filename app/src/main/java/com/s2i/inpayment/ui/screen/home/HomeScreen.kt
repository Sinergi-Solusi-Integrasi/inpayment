package com.s2i.inpayment.ui.screen.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.common.utils.date.Dates
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.TransactionItem
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.custome.LogoIndicator
import com.s2i.inpayment.ui.components.custome.LogoWithBeam
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.screen.wallet.BalanceCard
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.GreenTeal40
import com.s2i.inpayment.ui.theme.exComeGradient
import com.s2i.inpayment.ui.theme.gradientBrush
import com.s2i.inpayment.ui.theme.inComeGradient
import com.s2i.inpayment.ui.theme.triGradientBrush
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// In HomeScreen.kt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    balanceViewModel: BalanceViewModel = koinViewModel(),
    username: String,
    sessionManager: SessionManager
) {

    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Set `isStartupLoading` only based on the ViewModel's loading state during initial load

    // Handle initial loading
    LaunchedEffect(loading) {
        if (loading && isStartupLoading) {
            isStartupLoading = true
        } else if (!loading) {
            delay(500) // Optional delay to keep the loading indicator visible for a short time
            isStartupLoading = false
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isStartupLoading = false
                isRefreshing = true
                balanceViewModel.fetchBalance()
                balanceViewModel.fetchTriLastTransaction()
                balanceViewModel.fetchInComeExpenses()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    val transactions by balanceViewModel.triLastTransaction.collectAsState()
    val incomeExpense by balanceViewModel.incomeExpenses.collectAsState()
    //toogle visible balance
    val balanceState by balanceViewModel.balance.collectAsState()
    val textMeasurer = rememberTextMeasurer()
    var isBalanceValid by remember { mutableStateOf(true) }

    LaunchedEffect(sessionManager.isLogin) {
        if (!sessionManager.isLogin) {
            Log.d("HomeScreen", "User is not logged in. Redirecting to login_screen.")
            navController.navigate("login_screen") {
                popUpTo("home_screen") { inclusive = true }
            }
        } else {
            balanceViewModel.fetchBalance()
            balanceViewModel.fetchTriLastTransaction()
            balanceViewModel.fetchInComeExpenses()
        }
    }


    if (sessionManager.isUserLogin()) {
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
                    .background(Color.White) // Background color from your example) // The color from your example
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {

                if (isStartupLoading) {
                    CustomLinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

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
                        painter = painterResource(id = R.drawable.logo), // Replace with the correct logo resource
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
                                tint = Color.Gray
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
                                        launchSingleTop = true
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
                            BalanceCard(navController, balanceState, isBalanceValid) { isBalanceValid = !isBalanceValid }
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
                                        .background(brush = inComeGradient(IntSize(1080, 1920)))
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = "Pemasukan", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = incomeExpense?.data?.incomeTrx?.amount?.let { RupiahFormatter.formatToRupiah(it) } ?: "Loading...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = GreenTeal40
                                    )
                                    Text(
                                        text = incomeExpense?.data?.incomeTrx?.title?.ifEmpty { "" } ?: " ",
                                        style = MaterialTheme.typography.bodySmall
                                    )
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
                                        .background(brush = exComeGradient(IntSize(1080, 1920)))
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = "Pengeluaran", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = incomeExpense?.data?.expenseTrx?.amount?.let { "-" + RupiahFormatter.formatToRupiah(it) } ?: "Loading...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Red
                                    )
                                    Text(text = incomeExpense?.data?.expenseTrx?.title ?: " ", style = MaterialTheme.typography.bodySmall)
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
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp), // Limit height for the transaction history
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium // Corner radius for the card
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(transactions) { transaction -> // Show the latest 3 transactions
//                            val transactionDate = Dates.parseIso8601(transaction.trxDate)
//                            val dateTimeFormatted = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
//                                Date(transactionDate)
//                            )
                                val dateTimeFormatted = Dates.formatTimeDifference(
                                    startTime = Dates.parseIso8601(transaction.trxDate),
                                    endTime = System.currentTimeMillis()
                                )
                                TransactionItem(
                                    title = transaction.title.ifEmpty { " " },
                                    description = transaction.trxType,
                                    amount = if (transaction.cashFlow == "MONEY_OUT") "-${ RupiahFormatter.formatToRupiah(transaction.amount)}" else "+${RupiahFormatter.formatToRupiah(transaction.amount)}",
                                    isNegative = transaction.cashFlow == "MONEY_OUT",
                                    dateTime = dateTimeFormatted,
                                    transactionId = transaction.transactionId,
                                    onClick = { transactionId ->
                                        navController.navigate("detail_transaksi_screen/$transactionId")
                                    }
                                )
                            }
                        }
                        // "See More" button
                        TextButton(onClick = { /* Handle see more */
                            navController.navigate("history_screen") {
                                launchSingleTop = true
                            }
                        },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Lihat Riwayat",
                                style = MaterialTheme.typography.titleSmall,
                                color = DarkTeal21)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            LogoIndicator(isRefreshing, pullRefreshState)
        }
    } else {
        navController.navigate("login_screen") {
            popUpTo("home_screen") { inclusive = true }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen(){
//    HomeScreen(HomeViewModel(), sessionManager = sessionsManager)
//}