package com.s2i.inpayment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.ui.MyApp
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.theme.InPaymentTheme

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

