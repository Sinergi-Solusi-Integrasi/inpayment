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
import com.s2i.inpayment.ui.screen.auth.RegisterScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel = koinViewModel()) {
    val homeViewModel: HomeViewModel = koinViewModel()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(
                navController = navController,
                isLoggedIn = authViewModel.isLoggedIn()
            )
        }
        composable("onboard_screen") {
            OnboardScreen(navController = navController) // Pass navController
        }
        composable("login_screen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)  // Pass Koin ViewModel to the screen
        }
        composable("register_screen") {
            RegisterScreen()  // Pass Koin ViewModel to the screen
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
