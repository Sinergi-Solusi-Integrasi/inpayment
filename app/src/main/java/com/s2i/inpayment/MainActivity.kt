package com.s2i.inpayment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.s2i.common.utils.networkmanager.NetworkUtils
import com.s2i.inpayment.ui.MyApp
import com.s2i.inpayment.ui.components.NetworkContent
import com.s2i.inpayment.ui.components.navigation.AppNavigation
import com.s2i.inpayment.ui.screen.home.HomeScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.theme.InPaymentTheme
import com.s2i.inpayment.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkUtils.initializeNetworkCallback(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        setContent {
            InPaymentTheme {
                // Tambahkan NetworkContent untuk menampilkan status jaringan secara global
                NetworkContent()
//                val navController = rememberNavController()
                // Initialize HomeView  Model using the viewModel() function
//                val homeViewModel: HomeViewModel = viewModel()

                // Navigation Setup

                // Call your navigation setup here
//                AppNavigation(navController = navController)
//                MyApp()
                MyApp()
            }
        }
    }
}

