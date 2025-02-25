//package com.s2i.inpayment
//
//import android.os.Bundle
//import android.content.Context
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.core.view.WindowCompat
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.work.Configuration
//import androidx.work.Data
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import com.s2i.common.utils.networkmanager.NetworkUtils
//import com.s2i.inpayment.ui.MyApp
//import com.s2i.inpayment.ui.components.NetworkContent
//import com.s2i.inpayment.ui.components.navigation.AppNavigation
//import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
//import com.s2i.inpayment.ui.screen.home.HomeScreen
//import com.s2i.inpayment.ui.screen.home.HomeScreen1
//import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
//import com.s2i.inpayment.ui.screen.splash.SplashScreen
//import com.s2i.inpayment.ui.theme.InPaymentTheme
//import com.s2i.inpayment.ui.viewmodel.HomeViewModel
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge()
//        super.onCreate(savedInstanceState)
////        WorkManager.initialize(this, Configuration.Builder().build())
//        NetworkUtils.initializeNetworkCallback(this)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        setContent {
//            InPaymentTheme {
//                // Tambahkan NetworkContent untuk menampilkan status jaringan secara global
//                NetworkContent()
////                val navController = rememberNavController()
//                // Initialize HomeView  Model using the viewModel() function
////                val homeViewModel: HomeViewModel = viewModel()
//                // Navigation Setup
//
//                // Call your navigation setup here
////                AppNavigation(navController = navController)
////                MyApp()
////                MyApp()
//
//                    InPaymentTheme {
//                        HomeScreen1() // Ganti dari MyApp() atau HomeScreen()
//                    }
//
//            }
//        }
//    }
//
//    // Fungsi untuk menjadwalkan worker
//    fun scheduleOrderQueryWorker(trxId: String) {
//        val inputData = Data.Builder()
//            .putString("trxId", trxId)
//            .build()
//
//        val orderQueryWork = OneTimeWorkRequestBuilder<NotificationWorker>()
//            .setInputData(inputData)
//            .build()
//
//        WorkManager.getInstance(this).enqueue(orderQueryWork)
//    }
//}
//

package com.s2i.inpayment
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.ui.MyApp
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.theme.InPaymentTheme
import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
import com.s2i.inpayment.ui.screen.home.HomeScreen1
import com.s2i.inpayment.ui.screen.home.MainScreen
import com.s2i.inpayment.ui.screen.wallet.BalanceCard

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inisialisasi Network Callback
        NetworkUtils.initializeNetworkCallback(this)

        // Menyesuaikan tampilan agar edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Menjalankan UI dengan tema
        setContent {
            InPaymentTheme {
                NetworkContent()  // ✅ Untuk monitoring jaringan
                MyApp()   // ✅ Memuat layar utama dengan navigasi
            }
        }
    }

    // Fungsi untuk menjadwalkan worker notifikasi berdasarkan transaksi ID
    fun scheduleOrderQueryWorker(trxId: String) {
        val inputData = Data.Builder()
            .putString("trxId", trxId)
            .build()

        val orderQueryWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(this).enqueue(orderQueryWork)
    }
}
