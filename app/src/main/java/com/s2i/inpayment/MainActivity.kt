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
    private var pendingQrisInfo: Triple<String, String, Int>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        WorkManager.initialize(this, Configuration.Builder().build())
        NetworkUtils.initializeNetworkCallback(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Periksa jika ada intent dari notifikasi QRIS
        if (intent?.action == "ACTION_OPEN_QRIS") {
            val qrisCode = intent.getStringExtra("EXTRA_QRIS_CODE")
            val trxId = intent.getStringExtra("EXTRA_TRX_ID")
            val amount = intent.getIntExtra("EXTRA_AMOUNT", 0)

            if (!qrisCode.isNullOrEmpty() && !trxId.isNullOrEmpty()) {
                pendingQrisInfo = Triple(qrisCode, trxId, amount)
                Log.d("MainActivity", "📲 Intent QRIS diterima: qrisCode=$qrisCode, trxId=$trxId, amount=$amount")
            }
        } else {
            // Handle intent biasa
            val transactionId = intent?.getStringExtra("transaction_id")
            if (!transactionId.isNullOrBlank()) {
                pendingTransactionId = transactionId
            }
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

                    // Handle QRIS dari notifikasi
                    pendingQrisInfo?.let { (qrisCode, trxId, amount) ->
                        Log.d("MainActivity", "Navigasi ke QRIS screen: qrisCode=$qrisCode, trxId=$trxId, amount=$amount")
                        navController.navigate("qris_screen/$qrisCode/$trxId/$amount"){
                            popUpTo("home_screen") { inclusive = false }
                        }
                        pendingQrisInfo = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent.action == "ACTION_OPEN_QRIS") {
            val qrisCode = intent.getStringExtra("EXTRA_QRIS_CODE")
            val trxId = intent.getStringExtra("EXTRA_TRX_ID")
            val amount = intent.getIntExtra("EXTRA_AMOUNT", 0)

            Log.d("MainActivity", "📲 onNewIntent: QRIS intent diterima")

            if (qrisCode.isNullOrEmpty() || trxId.isNullOrEmpty()) {
                Log.e("MainActivity", "❌ onNewIntent: Data QRIS tidak lengkap")
                return
            }

            if (::navController.isInitialized && navController.graph.startDestinationRoute != null) {
                Log.d("MainActivity", "✅ onNewIntent: Navigasi ke QrisScreen")
                navController.navigate("qris_screen/$qrisCode/$trxId/$amount"){
                    popUpTo("home_screen") { inclusive = false }
                }
            } else {
                Log.w("MainActivity", "⚠️ navController belum siap, menyimpan data untuk navigasi nanti")
                pendingQrisInfo = Triple(qrisCode, trxId, amount)
            }
        } else {
            // Handle intent biasa
            val transactionId = intent.getStringExtra("transaction_id")
            if (!transactionId.isNullOrBlank()) {
                if (::navController.isInitialized && navController.graph.startDestinationRoute != null) {
                    navController.navigate("detail_transaksi_screen/$transactionId") {
                        popUpTo("home_screen") { inclusive = false }
                    }
                } else {
                    pendingTransactionId = transactionId
                }
            }
        }
    }

}

