package com.s2i.inpayment.ui.components.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.s2i.inpayment.ui.screen.home.HomeScreen
import com.s2i.inpayment.ui.screen.auth.LoginScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.viewmodel.HomeViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val homeViewModel: HomeViewModel = viewModel()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("onboard_screen") {
            OnboardScreen(navController = navController) // Pass navController
        }
        composable("login_screen") {
            LoginScreen() // Define this separately
        }
        composable("home_screen") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                HomeScreen(
                    viewModel = homeViewModel,
                    modifier = Modifier.padding(innerPadding)

                )
            }
        }
    }
}
