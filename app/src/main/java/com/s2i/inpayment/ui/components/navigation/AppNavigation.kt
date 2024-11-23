
package com.s2i.inpayment.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.ui.components.camera.KycCameraScreen
import com.s2i.inpayment.ui.screen.home.HomeScreen
import com.s2i.inpayment.ui.screen.auth.LoginScreen
import com.s2i.inpayment.ui.screen.auth.RegisterScreen
import com.s2i.inpayment.ui.screen.kyc.KYCIntroScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.profile.ProfileScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.screen.wallet.WalletHistoryScreen
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import com.s2i.inpayment.ui.viewmodel.TokenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    balanceViewModel: BalanceViewModel = koinViewModel(),
    tokenViewModel: TokenViewModel = koinViewModel(),
    context: Context
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val sessionManager = SessionManager(context)
    val username = sessionManager.getFromPreference(SessionManager.KEY_USERNAME) ?: "User"

    // Observe the token state
    val tokenState by tokenViewModel.tokenState.collectAsState()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(
                navController = navController,
                isLoggedIn = authViewModel.isLoggedIn()
            )
        }
        composable("onboard_screen") {
            OnboardScreen(navController = navController)
        }
        composable("login_screen") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(
            "register_screen/{detectedText}/{extractedName}?filePath={filePath}",
            arguments = listOf(
                navArgument("detectedText") { type = NavType.StringType },
                navArgument("extractedName") { type = NavType.StringType },
                navArgument("filePath") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val detectedText = backStackEntry.arguments?.getString("detectedText") ?: ""
            val extractedName = backStackEntry.arguments?.getString("extractedName") ?: ""
            val filePath = backStackEntry.arguments?.getString("filePath")

            RegisterScreen(
                navController = navController,
                identityNumber = detectedText,
                name = extractedName,
                filePath = filePath,
                authViewModel = authViewModel,
            )
        }
        composable("kyc_intro_screen") {
            KYCIntroScreen(navController = navController)
        }
        composable("kyc_camera_screen") {
            KycCameraScreen(navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("history_screen") {
            WalletHistoryScreen(
                navController = navController,
                balanceViewModel =  balanceViewModel
            )
        }
        composable("home_screen") {
            if (sessionManager.isLogin) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        username = username,
                        viewModel = homeViewModel,
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        sessionManager = sessionManager
                    )
                }
            } else {
                // If the user is not logged in, navigate to the login screen
                navController.navigate("login_screen") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    // Observe tokenState for logout or token refresh
    LaunchedEffect(tokenState) {
        if (tokenState is TokenViewModel.TokenState.Expired) {
            navController.navigate("login_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}