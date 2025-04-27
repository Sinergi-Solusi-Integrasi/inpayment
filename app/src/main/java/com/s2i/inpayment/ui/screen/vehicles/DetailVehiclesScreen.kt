package com.s2i.inpayment.ui.screen.vehicles

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.s2i.common.utils.date.Dates
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
    vehiclesViewModel: VehiclesViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {

    val context = LocalContext.current
    val vehiclesState by vehiclesViewModel.getVehiclesState.collectAsState()
    val isLoading by vehiclesViewModel.loading.collectAsState()
    val imageLoader: ImageLoader = getKoin().get()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val lendSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLendBottomSheet by remember { mutableStateOf(false) }
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

    // Format dates
    val loanedAtFormatted =
        selectedVehicle?.loanedAt?.let { Dates.formatIso8601(Dates.parseIso8601(it)) } ?: "-"
    val loandExpiredAtFormatted =
        selectedVehicle?.loanExpiredAt?.let { Dates.formatIso8601(Dates.parseIso8601(it)) } ?: "-"

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }, // Tutup saat swipe ke bawah
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.3f),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
        ) {
            // Judul Detail Vehicle
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

            // Gambar Kendaraan
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
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Kendaraan (LazyColumn untuk scrolling)
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Memberikan ruang dinamis untuk detail agar bisa scroll
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item {
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
                        DetailItem(label = "Loaned At", value = loanedAtFormatted)
                        DetailItem(label = "Loan Expired At", value = loandExpiredAtFormatted)
                        DetailItem(
                            label = "Borrower User",
                            value = (selectedVehicle.borrowerUserId?.take(10) + "...") ?: "-"
                        )
                    } else {
                        DetailItem(label = "Is Loaned", value = "No")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol tindakan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (selectedVehicle?.isOwner == true) {
                    if (selectedVehicle.isLoaned == false) {
                        // Jika kendaraan BELUM di-loan -> tampilkan 2 tombol
                        SplitButton(
                            icon = Icons.Filled.ChangeCircle,
                            label = "Switch",
                            isLoading = isLoading,
                            isSelected = false,
                            onClick = {
                                coroutineScope.launch {
                                    delay(500)
                                    vehiclesViewModel.changeVehicles(vehicleId)
                                    vehiclesViewModel.enableVehicles(vehicleId)
                                    showBottomSheet = false
                                    Toast.makeText(
                                        context,
                                        "Switching Vehicle...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        )

                        SplitButton(
                            icon = Icons.Filled.Key,
                            label = "Lend",
                            isLoading = isLoading,
                            isSelected = false,
                            onClick = {
                                onDismiss()

                                // Navigasi ke LendVehiclesScreen dengan parameter
                                navController.navigate(
                                    "lend_vehicle_screen/${vehicleId}" +
                                            "?brand=${selectedVehicle?.brand ?: ""}" +
                                            "&model=${selectedVehicle?.model ?: ""}" +
                                            "&plateNumber=${selectedVehicle?.plateNumber ?: ""}"
                                )
                            },
                        )
                    } else {
                        // Jika kendaraan SUDAH di-loan -> tampilkan hanya "Pull Loan"
                        SplitButton(
                            icon = Icons.Filled.SwapVerticalCircle,
                            label = "Pull Loan",
                            isSelected = false,
                            onClick = {
                                coroutineScope.launch {
                                    delay(500)
                                    vehiclesViewModel.pullsLoans(vehicleId)
                                    showBottomSheet = false
                                    Toast.makeText(
                                        context,
                                        "Pulling Loan...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        )
                    }
                }

                if (selectedVehicle?.isOwner == false) {
                    if (selectedVehicle?.isLoaned == true) {
                        SplitButton(
                            icon = Icons.Filled.SwapVerticalCircle,
                            label = "Returns Loan",
                            isSelected = false,
                            onClick = {
                                coroutineScope.launch {
                                    delay(500)
                                    vehiclesViewModel.returnsLoans(vehicleId)
                                    showBottomSheet = false
                                    Toast.makeText(
                                        context,
                                        "Returning Loan...",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showLendBottomSheet) {
        ReusableBottomSheet(
            sheetState = lendSheetState,
            onDismiss = { showLendBottomSheet = false }
        ) {

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
