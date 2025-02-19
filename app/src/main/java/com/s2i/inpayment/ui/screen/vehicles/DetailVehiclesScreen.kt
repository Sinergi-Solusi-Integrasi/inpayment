package com.s2i.inpayment.ui.screen.vehicles

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.button.SplitButton
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailVehiclesScreen(
    navController: NavController,
    vehicleId: String,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val vehiclesState by vehiclesViewModel.getVehiclesState.collectAsState()
    val isLoading by vehiclesViewModel.loading.collectAsState()
    val imageLoader: ImageLoader = getKoin().get()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val selectedVehicle = vehiclesState.find { it.vehicleId == vehicleId }

    val vehicleImages = selectedVehicle?.images ?: emptyList()
    val displayedImages = remember(vehicleImages) {
        val images = vehicleImages.take(4).toMutableList()
        images
    }

    Log.d("VehicleImages", "Gambar yang ditampilkan: $displayedImages") // Debugging

    val pagerState = rememberPagerState(pageCount = { displayedImages.size })
    val currentUserId = selectedVehicle?.ownerUserId // Mendapatkan ID user saat ini

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            contentPadding = PaddingValues(bottom = 100.dp) // Tambahkan padding bawah untuk menghindari overlap dengan tombol
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detail Vehicle",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (displayedImages.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                    ) { page ->
                        val imageUrl = displayedImages[page]
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true)
                                .build(),
                            imageLoader = imageLoader,
                            contentDescription = "Vehicle Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        pageCount = vehicleImages.size,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menampilkan detail kendaraan
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DetailItem(label = "Brand", value = selectedVehicle?.brand ?: "-")
                    DetailItem(label = "Model", value = selectedVehicle?.model ?: "-")
                    DetailItem(label = "Varian", value = selectedVehicle?.varian ?: "-")
                    DetailItem(label = "Color", value = selectedVehicle?.color ?: "-")
                    DetailItem(label = "Plate Number", value = selectedVehicle?.plateNumber ?: "-")
                    DetailItem(label = "Status", value = selectedVehicle?.status ?: "-")
                    DetailItem(
                        label = "Is Owner",
                        value = if (selectedVehicle?.isOwner == true) "Yes" else "No"
                    )

                    if (selectedVehicle?.isLoaned == true) {
                        DetailItem(label = "Is Loaned", value = "Yes")
                        DetailItem(label = "Loaned At", value = selectedVehicle.loanedAt ?: "-")
                        DetailItem(label = "Loan Expired At", value = selectedVehicle.loanExpiredAt ?: "-")
                        DetailItem(label = "Borrower User ID", value = selectedVehicle.borrowerUserId ?: "-")
                    } else {
                        DetailItem(label = "Is Loaned", value = "No")
                    }
                }
            }
        }

        // Mengganti BottomAppBar dengan Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    SplitButton(
                        icon = Icons.Filled.ChangeCircle,
                        label = "Switch Vehicles",
                        isLoading = isLoading,
                        isSelected = false,
                        onClick = {
                            coroutineScope.launch {
                                delay(500)
                                vehiclesViewModel.changeVehicles(vehicleId)
                                Toast.makeText(
                                    context,
                                    "Switch Vehicles...",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    SplitButton(
                        icon = Icons.Filled.Key,
                        label = "Lend Vehicles",
                        isLoading = isLoading,
                        isSelected = false,
                        onClick = {
//                            coroutineScope.launch {
//                                delay(500)
//                                navController.navigate("lend_vehicles/$vehicleId") {
//                                    popUpTo("vehicles_screen") { inclusive = true }
//                                }
//                            }
                            showBottomSheet = true
                        }
                    )
                }
                if (selectedVehicle?.isLoaned == true) {
                    Box(modifier = Modifier.weight(1f)) {
                        SplitButton(
                            icon = Icons.Filled.SwapVerticalCircle,
                            label = "Pull Loan",
                            isSelected = false,
                            onClick = {
                                coroutineScope.launch {
                                    delay(500)
                                    vehiclesViewModel.returnsLoans(vehicleId)
                                    Toast.makeText(
                                        context,
                                        "Pull Loan...",
                                        Toast.LENGTH_SHORT,
                                    )
                                        .show()
                                    showBottomSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ReusableBottomSheet(
            sheetState = sheetState,
            onDismiss = { showBottomSheet = false }
        ) {
            LendVehiclesScreen(
                navController = navController,
                vehicleId = vehicleId,
                vehiclesViewModel = vehiclesViewModel
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
