package com.s2i.inpayment.ui.screen.splash

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.permission.hasAllPermissions
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.ui.viewmodel.ServicesViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    notificationViewModel: NotificationsViewModel = koinViewModel(),
    servicesViewModel: ServicesViewModel = koinViewModel(),
    sessionManager: SessionManager,
    isLoggedIn: Boolean
){
    var hasNavigated by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val bindingState by servicesViewModel.bindingState.collectAsState()
    val errorState by servicesViewModel.errorState.collectAsState()
    // Automatically navigate to OnboardScreen after a delay
    // Cek apakah semua izin telah diberikan
    LaunchedEffect(hasNavigated) {
        delay(3000) // 3 seconds delay
        if (!hasNavigated) {
            hasNavigated = true
            val allPermissionsGranted = hasAllPermissions(context) // Periksa izin

            when {
                isLoggedIn && !allPermissionsGranted -> {
                    // Login berhasil tapi izin belum diberikan
                    navController.navigate("permission_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }

                isLoggedIn && allPermissionsGranted -> {
                    // Login berhasil dan semua izin sudah diberikan
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }

                else -> {
                    navController.navigate("onboard_screen") {
                        popUpTo("splash_screen") {
                            inclusive = true
                        } // This clears everything before `onboard_screen`
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Background circles (using box)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.rectangle_top_left),
                contentDescription = "rectagle top left",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(100.dp) // Adjust Size
                    .align(Alignment.TopStart)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.circle_down_left),
                contentDescription = "rectagle bottom right",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.circle),
                contentDescription = "rectagle bottom right",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(100.dp),
            )
        }
    }

    // center logo
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )
    }

    // Powered by section
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.BottomCenter

    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered by",
                fontSize = 12.sp,
                color = DarkTeal21,
                fontWeight = FontWeight.Light

            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.intracts_logo),
                contentDescription = "s2i logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
