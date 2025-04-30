package com.s2i.inpayment.ui.screen.wallet

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.common.utils.convert.RupiahFormatter
import kotlinx.coroutines.delay

@Composable
fun PaymentSuccessScreen(
    navController: NavController,
    transactionId: String,
    amount: Int // Default amount if none is provided
) {
    // Animation states
    var showCheckmark by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showAmount by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Scale animation for checkmark
    val checkmarkScale by animateFloatAsState(
        targetValue = if (showCheckmark) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "checkmark_scale"
    )

    // Background color - Success green
    val successGreen = Color(0xFF4CAF50)

    // Trigger animations sequentially
    LaunchedEffect(key1 = true) {
        delay(300)
        showCheckmark = true
        delay(800)
        showTitle = true
        delay(300)
        showSubtitle = true
        delay(300)
        showAmount = true
        delay(300)
        showButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(successGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Checkmark circle with animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(checkmarkScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(successGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success Check",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title with fade-in animation
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(500)
                        )
            ) {
                Text(
                    text = "Payment successful!",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle with fade-in animation
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(500)
                        )
            ) {
                Text(
                    text = "Yeyyy! You have completed your payment",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount with fade-in animation
            AnimatedVisibility(
                visible = showAmount,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(500)
                        )
            ) {
                Text(
                    text = RupiahFormatter.formatToRupiah(amount),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Button at the bottom
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        initialOffsetY = { 100 },
                        animationSpec = tween(500)
                    ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            Button(
                onClick = {
                    // Navigate to home screen
                    navController.navigate("detail_transaksi_screen/${transactionId}") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0C14B) // Gold/yellow color for button
                )
            ) {
                Text(
                    text = "Check Detail",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}