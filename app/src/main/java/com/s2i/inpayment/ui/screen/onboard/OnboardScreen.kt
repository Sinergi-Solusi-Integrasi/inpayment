package com.s2i.inpayment.ui.screen.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.ui.components.onboardingPages
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.GreenTeal21
import kotlinx.coroutines.delay

@Composable
fun OnboardScreen(navController: NavController) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = onboardingPages

    // Automatically advance to the next page every 3 seconds
    LaunchedEffect(key1 = currentPage) {
        delay(3000) // 3 seconds delay
        if (currentPage < pages.size - 1) {
            currentPage++
        } else {
            currentPage = 0 // Reset to the first page
        }
    }

    // Main Onboarding screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
    ) {
        // Skip button
        Text(
            text = "Skip",
            fontSize = 18.sp,
            color = GreenTeal21,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp) // Moved down
                .clickable {
                    navController.navigate("login_screen"){
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Main content layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp)) // Adjust space

            // Image, resized to match the proportion of the screen
            Image(
                painter = painterResource(pages[currentPage].image),
                contentDescription = "onboarding",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adjusted to make the image smaller
                    .height(500.dp) // Specific height to avoid stretching too much
            )

            // Title
            Text(
                text = pages[currentPage].title,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 4.dp) // Adjust padding to move it up
            )

            // Description
            Text(
                text = pages[currentPage].desc,
                fontSize = 16.sp,
                color = GreenTeal21,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp) // Adjusted spacing
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Dots Indicator (rectangles in this case) with the "Next" button in the same row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Dot indicators
                Row(
                    modifier = Modifier.weight(2f), // Takes up the available space
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pages.forEachIndexed { index, _ ->
                        RectIndicator(isSelected = index == currentPage)
                    }
                }

                // Next button
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1){
                            currentPage++
//                            currentPage = 0
                        } else {
                            navController.navigate("login_screen"){
                                popUpTo("onboard_screen") {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    shape = CircleShape, // Ensures the button is circular
                    modifier = Modifier
                        .size(50.dp), // This will make the button circular with 56dp diameter
                    contentPadding = PaddingValues(0.dp), // Padding for spacing from the edge
                    colors = ButtonDefaults.buttonColors(containerColor = GreenTeal21)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, // Replace with the appropriate icon for "Next"
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // This ensures the icon fits nicely inside the circular button
                    )
                }
            }
        }
    }
}

@Composable
fun RectIndicator(isSelected: Boolean) {
    // Change the dot to a rectangle
    Box(
        modifier = Modifier
            .size(width = if (isSelected) 20.dp else 10.dp, height = 6.dp)
            .background(
                if (isSelected) DarkTeal21 else Color.Gray,
                shape = RoundedCornerShape(3.dp)
            )
    )
}
