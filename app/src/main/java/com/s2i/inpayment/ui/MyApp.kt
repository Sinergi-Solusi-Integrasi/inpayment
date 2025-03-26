package com.s2i.inpayment.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.s2i.inpayment.ui.components.NetworkContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.ui.viewmodel.TokenViewModel
import com.s2i.inpayment.ui.components.navigation.AppNavigation
import com.s2i.inpayment.utils.helper.workmanager.TokenWorkManagerUtil
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(navController: NavHostController) {
    KoinContext {
//        val navController = rememberNavController()
        val context = LocalContext.current
        val tokenViewModel: TokenViewModel = koinViewModel()
        val tokenState by tokenViewModel.tokenState.collectAsState()

        LaunchedEffect(tokenState) {
            val currentRoute = navController.currentDestination?.route

            val exampleRoutes = listOf("splash_screen", "onboard_screen", "login_screen","register_screen")

            if (currentRoute !in exampleRoutes && tokenViewModel.tokenState.value is TokenViewModel.TokenState.Expired) {
                TokenWorkManagerUtil.stopWorkManager(context)
                navController.navigate("login_screen") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
        AppNavigation(navController = navController, context = context)
    }
}