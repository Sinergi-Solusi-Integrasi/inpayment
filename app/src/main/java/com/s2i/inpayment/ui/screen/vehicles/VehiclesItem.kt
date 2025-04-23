package com.s2i.inpayment.ui.screen.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import com.s2i.inpayment.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    Column(modifier = Modifier
        .fillMaxSize()
    ) {
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
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Card berbentuk pill dengan latar belakang putih
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .width(90.dp)
                    .height(50.dp)
                    .clickable { onAddVehicle() }
            ) {
                // Container untuk ikon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Pastikan R.drawable.ic_add_vehicles adalah ikon mobil dengan plus hijau
                    Image(
                        painter = painterResource(id = R.drawable.ic_add_vehicles),
                        contentDescription = "Tambah Kendaraan",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}