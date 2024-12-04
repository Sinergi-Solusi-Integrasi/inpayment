package com.s2i.inpayment.ui.screen.vehicles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s2i.domain.entity.model.vehicle.VehicleModel

@Composable
fun VehiclesItem(
    vehiclesState: List<VehicleModel>,
    onAddVehicle: () -> Unit,
    onDisactive: (String) -> Unit,
    onActive: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        vehiclesState.forEach { vehicle ->
            VehiclesCards(
                vehiclesState = vehicle,
                onDisactive = onDisactive,
                onActive = onActive
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = onAddVehicle) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vehicle")
        }
    }
}