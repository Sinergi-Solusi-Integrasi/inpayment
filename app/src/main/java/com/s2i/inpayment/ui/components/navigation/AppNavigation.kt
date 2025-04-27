
package com.s2i.inpayment.ui.components.navigation

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.ui.components.camera.CameraScreen
import com.s2i.inpayment.ui.components.camera.DocCameraScreen
import com.s2i.inpayment.ui.components.camera.KycCameraScreen
import com.s2i.inpayment.ui.components.gallery.GalleryContent
import com.s2i.inpayment.ui.screen.home.HomeScreen
import com.s2i.inpayment.ui.screen.auth.LoginScreen
import com.s2i.inpayment.ui.screen.auth.RegisterScreen
import com.s2i.inpayment.ui.screen.kyc.KYCIntroScreen
import com.s2i.inpayment.ui.screen.onboard.OnboardScreen
import com.s2i.inpayment.ui.screen.permission.PermissionScreen
import com.s2i.inpayment.ui.screen.profile.ProfileScreen
import com.s2i.inpayment.ui.screen.splash.SplashScreen
import com.s2i.inpayment.ui.screen.vehicles.DetailVehiclesScreen
import com.s2i.inpayment.ui.screen.vehicles.DocImageVehiclesScreen
import com.s2i.inpayment.ui.screen.vehicles.ImageVehiclesScreen
import com.s2i.inpayment.ui.screen.vehicles.IntroAddVehiclesScreen
import com.s2i.inpayment.ui.screen.vehicles.LendVehiclesScreen
import com.s2i.inpayment.ui.screen.vehicles.VehiclesInputSheet
import com.s2i.inpayment.ui.screen.vehicles.VehiclesScreen
import com.s2i.inpayment.ui.screen.wallet.DetailTransactionScreen
import com.s2i.inpayment.ui.screen.wallet.PaymentMethodsScreen
import com.s2i.inpayment.ui.screen.wallet.PaymentScreen
import com.s2i.inpayment.ui.screen.wallet.QrisScreen
import com.s2i.inpayment.ui.screen.wallet.WalletHistoryScreen
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import com.s2i.inpayment.ui.viewmodel.HomeViewModel
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.launch
//import com.s2i.inpayment.ui.viewmodel.TokenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    balanceViewModel: BalanceViewModel = koinViewModel(),
//    tokenViewModel: TokenViewModel = koinViewModel(),
    context: Context
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val vehiclesViewModel: VehiclesViewModel = koinViewModel()
    val sessionManager: SessionManager = koinInject()
    val username = sessionManager.getFromPreference(SessionManager.KEY_USERNAME) ?: "User"

    // Observe the token state
//    val tokenState by tokenViewModel.tokenState.collectAsState()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(
                navController = navController,
                sessionManager = sessionManager,
                isLoggedIn = authViewModel.isLoggedIn()
            )
        }
        composable("onboard_screen") {
            OnboardScreen(navController = navController)
        }
        composable("payment_methods_screen") {
            PaymentMethodsScreen(navController = navController)
        }

        composable("payment_screen") {
            PaymentScreen(navController = navController)
        }
        composable(
            "qris_screen/{qrisCode}/{trxId}/{amount}",
            arguments = listOf(navArgument(
                "qrisCode") {type = NavType.StringType},
                navArgument("trxId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val qrisCode = backStackEntry.arguments?.getString("qrisCode")
            val trxId = backStackEntry.arguments?.getString("trxId")
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() // tanpa format desimal 00
            // Log untuk memastikan data diterima
            Log.d("Navigation", "Navigated to QRIS Screen")
            Log.d("Navigation", "qrisCode: $qrisCode")
            Log.d("Navigation", "trxId: $trxId")
            Log.d("Navigation", "amount: $amount")
            QrisScreen(qrisState = qrisCode, trxId = trxId, amount = amount,  navController = navController)

        }
        composable("login_screen") {
            LoginScreen(navController = navController, authViewModel = authViewModel, sessionManager = sessionManager)
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

        composable("camera_screen") {
            CameraScreen(navController = navController, vehiclesViewModel = vehiclesViewModel)
        }

        // Detail vehicle route
        composable(
            route = "detail_vehicle_screen/{vehicleId}",
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            DetailVehiclesScreen(
                navController = navController,
                vehicleId = vehicleId,
                onDismiss = { navController.navigateUp() }
            )
        }

        // Lend vehicle route dengan parameter
        composable(
            route = "lend_vehicle_screen/{vehicleId}?brand={brand}&model={model}&plateNumber={plateNumber}",
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("brand") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("model") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("plateNumber") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val brand = backStackEntry.arguments?.getString("brand") ?: ""
            val model = backStackEntry.arguments?.getString("model") ?: ""
            val plateNumber = backStackEntry.arguments?.getString("plateNumber") ?: ""

            LendVehiclesScreen(
                navController = navController,
                vehicleId = vehicleId,
                vehicleBrand = brand,
                vehicleModel = model,
                vehiclePlateNumber = plateNumber,
                vehiclesViewModel = vehiclesViewModel
            )
        }

        composable("vehicles_screen") {
            VehiclesScreen(navController = navController)
        }

        composable("doc_camera_screen") {
            DocCameraScreen(navController = navController, vehiclesViewModel = vehiclesViewModel)
        }

        composable("gallery_screen") {

            GalleryContent(
                navController = navController,
                vehiclesViewModel = vehiclesViewModel
            )
        }

        composable("intro_vehicle_screen") {
            IntroAddVehiclesScreen(navController = navController)
        }
        composable("image_vehicle_screen") {
            ImageVehiclesScreen(navController = navController, vehiclesViewModel = vehiclesViewModel)
        }

        composable("doc_vehicle_screen") {
            DocImageVehiclesScreen(navController = navController, vehiclesViewModel = vehiclesViewModel)
        }

        composable("input_vehicles_screen") {
                VehiclesInputSheet(
                    navController = navController,
                    vehiclesViewModel = vehiclesViewModel
                )
        }


        composable("permission_screen") {
            PermissionScreen(navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController = navController, sessionManager = sessionManager)
        }
        composable("history_screen") {
            WalletHistoryScreen(
                navController = navController,
                balanceViewModel =  balanceViewModel
            )
        }
        composable(
            "detail_transaksi_screen/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            if (transactionId.isEmpty()) {
                Log.e("DetailTransactionScreen", "Gagal navigasi: transactionId null atau kosong")
            } else {
                DetailTransactionScreen(
                    navController = navController,
                    balanceViewModel = balanceViewModel,
                    transactionId = transactionId
                )
            }
        }
        composable("home_screen") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                HomeScreen(
                    username = username,
                    viewModel = homeViewModel,
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    sessionManager = sessionManager,
                )
            }
        }
    }

    // Observe tokenState for logout or token refresh
//    LaunchedEffect(tokenState) {
//        if (tokenState is TokenViewModel.TokenState.Expired) {
//            navController.navigate("login_screen") {
//                popUpTo(0) { inclusive = true }
//            }
//        }
//    }
}