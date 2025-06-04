package com.s2i.inpayment.ui.screen.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.KYCOptions
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.theme.gradientBrushCards
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun IntroAddVehiclesScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrightTeal20)
    ) {
        // Content of the screen goes here
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Vehicles",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Ilusitration Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.car3d1),
                    contentDescription = "Car image",
                    modifier =
                        Modifier
                            .width(400.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
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
                leadingIcon = painterResource(id = R.drawable.ic_cars),
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
                descriptions = "Loan vehicles from your friends or families",
                leadingIcon = painterResource(id = R.drawable.ic_loans),
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = { /* TODO: Navigasi untuk menambah kendaraan */
                    coroutineScope.launch {
                        showBottomSheet = true
                    }
                },
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

    if (showBottomSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.ic_loans_intro,
            message = "Loan vehicles from your friends or families ",
            sheetState = bottomSheetState,
            onDismiss = {
                coroutineScope.launch { bottomSheetState.hide() }
                showBottomSheet = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoansVehiclesScreen(
                    navController,
                    vehiclesViewModel
                )
            }
        }
    }
}