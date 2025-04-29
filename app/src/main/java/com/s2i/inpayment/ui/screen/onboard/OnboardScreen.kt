package com.s2i.inpayment.ui.screen.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.s2i.inpayment.ui.components.Onboard
import com.s2i.inpayment.ui.components.onboardingPages
import com.s2i.inpayment.ui.theme.BrightTeal09
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.GreenTeal21
import com.s2i.inpayment.ui.theme.GreenTeal40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun OnboardScreen(navController: NavController) {
    val pages = onboardingPages
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-advance timer
    LaunchedEffect(key1 = pagerState.currentPage) {
        delay(3000) // 3 seconds delay
        if (pagerState.currentPage < pages.size - 1) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0) // Reset to the first page
            }
        }
    }

    // Main Onboarding screen layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightTeal20)
    ) {
        // Skip button
        Text(
            text = "Skip",
            fontSize = 16.sp,
            color = GreenTeal40,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .clickable {
                    navController.navigate("login_screen"){
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Content area (image, title, desc)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Horizontal Pager for swiping
                HorizontalPager(
                    count = pages.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->
                    OnboardingPage(page = pages[pageIndex])
                }
                // Custom Pager Indicator with larger dots
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    activeColor = Color.Gray,
                    inactiveColor = Color.LightGray,
                    indicatorWidth = 10.dp,
                    indicatorHeight = 10.dp,
                    spacing = 8.dp
                )

                // Next button
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                navController.navigate("login_screen") {
                                    popUpTo("onboard_screen") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Next",
                        fontSize = 16.sp,
                        color = BrightTeal09,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Onboard) {
    // Extract title, desc, and image based on the Onboard sealed class
    val (title, desc, image) = when (page) {
        is Onboard.FirstPages -> Triple(page.title, page.desc, page.image)
        is Onboard.SecondPages -> Triple(page.title, page.desc, page.image)
        // Add any other variants if they exist
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Image(
            painter = painterResource(image),
            contentDescription = "onboarding",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(280.dp)  // Fixed height instead of weight
                .fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Description
        Text(
            text = desc,
            fontSize = 16.sp,
            color = BrightTeal09,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}