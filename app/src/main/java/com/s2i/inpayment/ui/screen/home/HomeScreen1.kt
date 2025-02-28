package com.s2i.inpayment.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.BottomNavigationBar
import com.s2i.inpayment.ui.navigation.bottomNavItems
import com.s2i.inpayment.ui.screen.history.HistoryScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedIndex, onItemSelected = { index ->
                if (selectedIndex != index) {  // ✅ Cegah navigasi ke halaman yang sama
                    selectedIndex = index
                    navController.navigate(bottomNavItems[index].route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            println("✅ Navigasi ke HomeScreen1")
            HomeScreen1()
        }
        composable("history") {
            println("✅ Navigasi ke HistoryScreen")
            HistoryScreen()
        }
    }
}


@Composable
fun HomeScreen1() {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = {
                println("Notifikasi aktif")
                },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Posisikan di pojok kanan atas
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeSreenPreview(){
    MainScreen()
}

//@Composable
//fun HomeScreen1(balanceViewModel: BalanceViewModel = koinViewModel()) {
//    val balanceState by balanceViewModel.balance.collectAsState()
//
//    LaunchedEffect(Unit) {
//        balanceViewModel.fetchBalance()
//    }
//
//    Scaffold { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.background),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//
//            IconButton(
//                onClick = { println("Notifikasi aktif") },
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(16.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Notifications,
//                    contentDescription = "Notifications",
//                    tint = Color.White
//                )
//            }
//
//            // Tampilan Saldo
//            balanceState?.let { balance ->
//                Card(
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(top = 64.dp)
//                        .fillMaxWidth(0.8f),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.White),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(text = "Saldo Anda", style = MaterialTheme.typography.titleMedium)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "Rp ${balance.balance}",
//                            style = MaterialTheme.typography.headlineMedium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
