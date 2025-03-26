package com.s2i.inpayment

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.ui.MyApp
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.components.navigation.AppNavigation
import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
import com.s2i.inpayment.ui.screen.home.HomeScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.theme.InPaymentTheme
import com.s2i.inpayment.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private var pendingTransactionId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        WorkManager.initialize(this, Configuration.Builder().build())
        NetworkUtils.initializeNetworkCallback(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val transactionId = intent?.getStringExtra("transaction_id")

        if (!transactionId.isNullOrBlank()) {
            pendingTransactionId = transactionId
        }

        setContent {
            InPaymentTheme {
                // Tambahkan NetworkContent untuk menampilkan status jaringan secara global
                NetworkContent()
                val navController = rememberNavController()
                // Initialize HomeView  Model using the viewModel() function
//                val homeViewModel: HomeViewModel = viewModel()
                // Navigation Setup

                // Call your navigation setup here
//                AppNavigation(navController = navController)
//                MyApp()
                MyApp(navController)

                LaunchedEffect(navController) {
                    pendingTransactionId?.let { id ->
                        Log.d("MainActivity", "Navigasi ke detail transaction dengan transactionid: $id")
                        navController.navigate("detail_transaksi_screen/$id"){
                            popUpTo("home_screen") { inclusive = false }
                        }
                        pendingTransactionId = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val transactionId = intent.getStringExtra("transaction_id") // Gunakan "transaction_id" yang benar
        Log.d("MainActivity", "📲 onNewIntent: transactionId = $transactionId")

        if (transactionId.isNullOrBlank()) {
            Log.e("MainActivity", "❌ onNewIntent: transactionId null atau kosong, tidak melakukan navigasi.")
            return
        }

        if (::navController.isInitialized && navController.graph.startDestinationRoute != null) {
            Log.d("MainActivity", "✅ onNewIntent: Navigasi ke detail transaksi")
            if (navController.currentDestination?.route != "detail_transaksi_screen/$transactionId") {
                navController.navigate("detail_transaksi_screen/$transactionId") {
                    popUpTo("home_screen") { inclusive = false }
                }
            }
        } else {
            Log.w("MainActivity", "⚠️ navController belum siap, menyimpan transactionId untuk navigasi nanti")
            pendingTransactionId = transactionId
        }
    }

}

