package com.s2i.inpayment.ui.screen.vehicles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s2i.domain.entity.model.vehicle.VehicleModel

@Composable
fun VehiclesItem(
    vehiclesState: List<VehicleModel>,
    onAddVehicle: () -> Unit,
    onDisactive: (String) -> Unit,
    onActive: (String) -> Unit,
    onShowDetail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f), // Biar konten mengisi layar
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 72.dp) // Agar tombol tambah tidak ketutupan
        ) {
            items(vehiclesState) { vehicle ->
                VehiclesCards(
                    vehiclesState = vehicle,
                    onDisactive = onDisactive,
                    onActive = onActive,
                    onShowDetail = onShowDetail // Tambahkan parameter untuk membuka detail kendaraan
                )
            }
        }

        // Tombol Tambah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onAddVehicle) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vehicle")
            }
        }
    }
}