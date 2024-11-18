package com.s2i.inpayment.ui.screen.splash

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.DarkTeal21
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, isLoggedIn: Boolean){
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

    // Automatically navigate to OnboardScreen after a delay
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds delay
        if(isLoggedIn) {
            navController.navigate("home_screen") {
                popUpTo(0) { inclusive = true } // This clears everything before `home_screen`
                launchSingleTop = true
            }
        } else {
            navController.navigate("onboard_screen") {
                popUpTo(0) { inclusive = true } // This clears everything before `onboard_screen`
                launchSingleTop = true
            }
        }
    }
}
