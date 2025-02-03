package com.s2i.inpayment.ui.screen.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.KYCOptions
import com.s2i.inpayment.ui.theme.gradientBrushCards

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun IntroAddVehiclesScreen(
    navController: NavController
){

    val bottomeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Content of the screen goes here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Vehicles",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

            }
            Spacer(modifier = Modifier.height(16.dp))

            // Ilusitration Box

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = gradientBrushCards()),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(62.dp)) // Move Down (Y = 36)
                    // Outer Box for the icon
                    Box(
                        modifier = Modifier
                            .width(183.dp) // Lebih besar dari 183dp
                            .height(320.dp) // Lebih besar dari 202dp
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Inner Box for the car icon
                        Box(
                            modifier = Modifier
                                .width(178.dp)
                                .height(105.dp)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(brush = gradientBrushCards()),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cars),
                                contentDescription = "Icon Cars",
                                tint = Color.White,
                                modifier = Modifier
                                    .width(111.dp)
                                    .height(74.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // Jarak antara box dan teks

                        // Text under the icon
                        // Teks di bawah Box Hijau
                        Text(
                            text = "BABLAS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(top = 140.dp) // Geser teks ke bawah sesuai tinggi box hitam
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subtitle
            Text(
                text = "Add vehicles or loans vehicles with your friends and families to Bablas to make enjoy your trip",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Options
            KYCOptions(
                title = "Add Vehicles",
                descriptions = "Add a vehicles",
                leadingIcon = painterResource( id = R.drawable.ic_cars),
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = { /* TODO: Navigasi untuk menambah kendaraan */
                    navController.navigate("doc_camera_screen") {
                        popUpTo("intro_vehicle_screen") { inclusive = true }
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            KYCOptions(
                title = "Loans Vehicles",
                descriptions = "Loan vehicles or lend your vehicle",
                leadingIcon = painterResource( id = R.drawable.ic_loans),
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = { /* TODO: Navigasi untuk menambah kendaraan */ },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = "Read our Terms of Service and Terms of Use",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}