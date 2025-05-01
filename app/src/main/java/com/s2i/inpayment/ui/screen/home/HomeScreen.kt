package com.s2i.inpayment.ui.screen.home

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.s2i.inpayment.ui.components.custome.LinierPullRefreshProgressIndicator
import com.s2i.inpayment.ui.components.custome.LogoIndicator
import com.s2i.inpayment.ui.components.custome.LogoWithBeam
import com.s2i.inpayment.ui.components.permission.hasAllPermissions
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.screen.wallet.BalanceCard
import com.s2i.inpayment.ui.theme.BrightTeal
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.GreenTeal40
import com.s2i.inpayment.ui.theme.Red560
import com.s2i.inpayment.ui.theme.backgroundGradientBrush
import com.s2i.inpayment.ui.theme.exComeGradient
import com.s2i.inpayment.ui.theme.gradientBrush
import com.s2i.inpayment.ui.theme.inComeGradient
import com.s2i.inpayment.ui.theme.triColorGradientBrush
import com.s2i.inpayment.ui.theme.triGradientBrush
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.ui.viewmodel.ServicesViewModel
import com.s2i.inpayment.utils.NotificationManagerUtil
import com.s2i.inpayment.utils.helper.workmanager.TokenWorkManagerUtil
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
    notificationViewModel: NotificationsViewModel = koinViewModel(),
    servicesViewModel: ServicesViewModel = koinViewModel(),
    username: String,
    sessionManager: SessionManager,
) {

    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by balanceViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isTokenRegistered by remember { mutableStateOf(false) }
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

    // Ambil context dari LocalContext
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        while (true) {
            val allPermissionsGranted = hasAllPermissions(context)
            if (!allPermissionsGranted) {
                navController.navigate("permission_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
            }
            delay(1000) // Check every second
        }
    }

    val bindingState by servicesViewModel.bindingState.collectAsState()
    val errorState by servicesViewModel.errorState.collectAsState()
    val loadingState by servicesViewModel.loading.collectAsState()
    val transactions by balanceViewModel.triLastTransaction.collectAsState()
    val incomeExpense by balanceViewModel.incomeExpenses.collectAsState()
    //toogle visible balance
    val balanceState by balanceViewModel.balance.collectAsState()
    val textMeasurer = rememberTextMeasurer()
    var isBalanceValid by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
            val deviceId = sessionManager.getFromPreference(SessionManager.KEY_DEVICE_ID)
            if (!deviceId.isNullOrBlank()) {
                Log.d("HomeScreen", "Device ID: $deviceId, proceeding to bind account.")
                servicesViewModel.bindAccount(deviceId)
            } else {
                Log.e("HomeScreen", "Device ID not found after registration")
            }
            balanceViewModel.fetchBalance()
            balanceViewModel.fetchTriLastTransaction()
            balanceViewModel.fetchInComeExpenses()
    }

    // Menangani hasil response BindingModel
    bindingState?.let { bindingModel ->
        Log.d("HomeScreen", "Binding Successful: ${bindingModel.message}")
    }

    // Menampilkan error jika ada
    errorState?.let { errorMessage ->
        Log.e("HomeScreen", "Error: $errorMessage")
    }


    if (sessionManager.isUserLogin()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = backgroundGradientBrush())
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .pullRefresh(state = pullRefreshState)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_spider),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .zIndex(0f)
                        .align(Alignment.TopCenter),
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.8f
                )

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(2f)
                )

                // Main content column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                ) {
                    // Show loading indicator when needed
                    if (isStartupLoading) {
                        CustomLinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        )
                    }

                    // Header section with the football field background

                        // Header content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Header with Logo, Notification, and Profile
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Logo on the left
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.CenterVertically)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Notification and profile section
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { /* Handle notification click */ }) {
                                        Icon(
                                            Icons.Filled.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(30.dp)

                                        )
                                    }

                                    // Profile picture
//
                                        Icon(
                                            imageVector = Icons.Filled.AccountCircle,
                                            contentDescription = "Profile",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable{
                                                navController.navigate("profile_screen") {
                                                    launchSingleTop = true
                                                }
                                            }
                                        )
                                }
                            }

                            // Balance Card positioned to overlap with background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                // Use your existing BalanceCard component
                                BalanceCard(
                                    navController,
                                    balanceState,
                                    isBalanceValid
                                ) {
                                    isBalanceValid = !isBalanceValid
                                }

                                // Bridge/Tower icon watermark at bottom right of card
                                Image(
                                    painter = painterResource(id = R.drawable.ic_road),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .size(80.dp)
//                                        .alpha(0.8f)
                                )
                            }
                        }


                    // Water drop effect below balance card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Create water drop shape using a rotated rounded rectangle
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(45f)
                                .offset(y = (-10).dp)
                                .background(
                                    color = BrightTeal,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Spacer(modifier = Modifier.height(8.dp))
                    // Scrollable content (transaction history and income/expense cards)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrightTeal20)
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            .weight(1f) // Take remaining space
                    ) {
                        // Scrollable content container
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Income and Expense section
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Income Card
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp),
                                        elevation = CardDefaults.elevatedCardElevation(4.dp),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    brush = inComeGradient(
                                                        IntSize(
                                                            1080,
                                                            1920
                                                        )
                                                    )
                                                )
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Pemasukan",
                                                    style = MaterialTheme.typography.titleSmall
                                                )

                                                // Triangle Up Icon for Income
//                                                Icon(
//                                                    painter = painterResource(id = R.drawable.ic_triangle_up),
//                                                    contentDescription = "Income Indicator",
//                                                    tint = GreenTeal40,
//                                                    modifier = Modifier.size(16.dp)
//                                                )
                                                // You can also use Canvas to draw a triangle if you don't have a resource:

                                                Canvas(modifier = Modifier.size(16.dp)) {
                                                    val path = Path().apply {
                                                        moveTo(size.width / 2f, 0f)
                                                        lineTo(size.width, size.height)
                                                        lineTo(0f, size.height)
                                                        close()
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = BrightTeal,
                                                        style = Fill
                                                    )
                                                }

                                            }
                                            Text(
                                                text = incomeExpense?.data?.incomeTrx?.amount?.let {
                                                    RupiahFormatter.formatToRupiah(
                                                        it
                                                    )
                                                } ?: "Loading...",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = GreenTeal40
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = incomeExpense?.data?.incomeTrx?.title?.ifEmpty { "" }
                                                    ?: " ",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    // Expense Card
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        elevation = CardDefaults.elevatedCardElevation(4.dp),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    brush = exComeGradient(
                                                        IntSize(
                                                            1080,
                                                            1920
                                                        )
                                                    )
                                                )
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Pengeluaran",
                                                    style = MaterialTheme.typography.titleSmall
                                                )

                                                // Triangle Down Icon for Expense
//                                                Icon(
//                                                    painter = painterResource(id = R.drawable.ic_triangle_down),
//                                                    contentDescription = "Expense Indicator",
//                                                    tint = Color.Red,
//                                                    modifier = Modifier.size(16.dp)
//                                                )
                                                // Canvas alternative if resource isn't available:

                                                Canvas(modifier = Modifier.size(16.dp)) {
                                                    val path = Path().apply {
                                                        moveTo(0f, 0f)
                                                        lineTo(size.width, 0f)
                                                        lineTo(size.width / 2f, size.height)
                                                        close()
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = Red560,
                                                        style = Fill
                                                    )
                                                }

                                            }

                                            Text(
                                                text = incomeExpense?.data?.expenseTrx?.amount?.let {
                                                    "-" + RupiahFormatter.formatToRupiah(
                                                        it
                                                    )
                                                } ?: "Loading...",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Red
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = incomeExpense?.data?.expenseTrx?.title
                                                    ?: " ",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }

                            // Transaction History Title
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left line
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .weight(1f),
                                        thickness = 1.dp,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Riwayat Transaksi",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    // Left line
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .weight(1f),
                                        thickness = 1.dp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            // Transaction History Card
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .padding(16.dp)
                                    ) {
                                        // Transactions list
                                        transactions.forEach { transaction ->
                                            val dateTimeFormatted = Dates.formatDate(
                                                Dates.parseIso8601(
                                                    transaction.trxDate
                                                )
                                            )

                                            TransactionItem(
                                                title = transaction.title.ifEmpty { " " },
                                                description = when (transaction.paymentMethod) {
                                                    "WALLET_CASH" -> "Bablas Saldo"
                                                    "BANK_TRANSFER" -> "Bank Transfer"
                                                    else -> transaction.paymentMethod
                                                },
                                                amount = if (transaction.cashFlow == "MONEY_OUT")
                                                    "-${RupiahFormatter.formatToRupiah(transaction.amount)}"
                                                else
                                                    "+${RupiahFormatter.formatToRupiah(transaction.amount)}",
                                                isNegative = transaction.cashFlow == "MONEY_OUT",
                                                dateTime = dateTimeFormatted,
                                                transactionId = transaction.transactionId,
                                                onClick = { transactionId ->
                                                    navController.navigate("detail_transaksi_screen/$transactionId")
                                                }
                                            )

                                            if (transaction != transactions.last()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }

                                        // "See More" button
//                                    TextButton(
//                                        onClick = {
//                                            navController.navigate("history_screen") {
//                                                launchSingleTop = true
//                                            }
//                                        },
//                                        modifier = Modifier
//                                            .align(Alignment.CenterHorizontally)
//                                            .padding(top = 12.dp)
//                                            .height(40.dp)
//                                    ) {
//                                        Text(
//                                            text = "Lihat Riwayat",
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            color = GreenTeal40
//                                        )
//                                    }
                                    }
                                }

                                // Add some bottom padding for better scrolling experience
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
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