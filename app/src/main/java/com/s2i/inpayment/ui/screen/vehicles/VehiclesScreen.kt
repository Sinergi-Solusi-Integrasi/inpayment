package com.s2i.inpayment.ui.screen.vehicles

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.date_time.DateTimeDialog
import com.maxkeppeler.sheets.date_time.models.DateTimeConfig
import com.maxkeppeler.sheets.date_time.models.DateTimeSelection
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun VehiclesScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {
    val vehiclesState = vehiclesViewModel.getVehiclesState.collectAsState()
    val loading by vehiclesViewModel.loading.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var filterType by remember { mutableStateOf("ALL") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


    // Memperlihatkan loading atau refreshing

    val showLoading = loading || isRefreshing

    // Memanggil fetchVehicles
    LaunchedEffect(Unit) {
        if (loading && isStartupLoading) {
            isStartupLoading = true
        } else if (!loading) {
            delay(500)
            isStartupLoading = false
            vehiclesViewModel.fetchVehicles()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isStartupLoading = false
                isRefreshing = true
                vehiclesViewModel.fetchVehicles()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    LaunchedEffect(Unit) {
        vehiclesViewModel.fetchVehicles()
    }

    // 🔥 Urutkan kendaraan: Aktif > Baru > Tidak aktif

    val filteredVehicles = when (filterType) {
        "All" -> vehiclesState.value
        "Active" -> vehiclesState.value.filter { it.status == "ACTIVE" }
        "Inactive" -> vehiclesState.value.filter { it.status == "INACTIVE" }
        "Owner" -> vehiclesState.value.filter { it.isOwner == true}
        "Loans" -> vehiclesState.value.filter { it.isLoaned == true }
        "Expired" -> vehiclesState.value.filter { it.loanExpiredAt == "EXPIRED" }
        else -> vehiclesState.value
    }.sortedWith(
        compareByDescending<VehicleModel> { it.status == "ACTIVE" } // Kendaraan aktif di atas
            .thenByDescending { it.updatedAt?.toLongOrNull() ?: it.createdAt?.toLongOrNull() ?: 0L } // Baru aktif di atas
            .thenByDescending { it.createdAt?.toLongOrNull() ?: 0L } // Kendaraan terbaru di atas
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .pullRefresh(state = pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            ) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "Vehicles",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isStartupLoading || showLoading) {
                CustomLinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val filters = mutableListOf("All", "Active", "Inactive", "Owner", "Loans")

                // Pastikan pengecekan null-safe sebelum menambahkan "EXPIRED"
                if (vehiclesState.value.any { it.loanExpiredAt?.equals("EXPIRED", ignoreCase = true) == true }) {
                    filters.add("Expired")
                }
                items(filters) { filter ->
                   Text(
                       text = filter,
                       modifier = Modifier
                           .background(
                           if (filterType.equals(filter, ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                           shape = CircleShape
                           )
                           .padding(horizontal = 16.dp, vertical = 8.dp)
                           .clickable {
                               filterType = filter
                           },
                       style = MaterialTheme.typography.bodyMedium,
                       color = if (filterType.equals (filter, ignoreCase = true)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                       fontWeight = if (filterType.equals(filter, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal
                   )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // Memastikan vehiclesState tidak null dan mengakses data kendaraan
            VehiclesItem(
                vehiclesState = filteredVehicles,  // Menggunakan List<VehicleModel> yang benar
                onAddVehicle = { /* aksi tambah kendaraan */
                    navController.navigate("intro_vehicle_screen") {
                        // Pop up semua screen yang ada di atas HomeScreen (termasuk profile_screen)
                        popUpTo("vehicle_screen") { inclusive = false }
                    }
                },
                onDisactive = { vehicleId ->
                    vehiclesViewModel.disableVehicles(vehicleId) // Panggil disable
                },
                onActive = { vehicleId ->
                    vehiclesViewModel.enableVehicles(vehicleId) // Panggil enable
                },
                onShowDetail = { vehicleId ->
                    selectedVehicleId = vehicleId
                    showBottomSheet = true
                }
            )
        }
    }

    if (showBottomSheet && selectedVehicleId != null) {
        DetailVehiclesScreen(
            navController,
            selectedVehicleId!!,
            vehiclesViewModel,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    delay(300)
                    showBottomSheet = false
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewVehiclesScreen() {
    VehiclesScreen(navController = NavController(LocalContext.current))
}