package com.s2i.inpayment.ui.screen.vehicles

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.inpayment.ui.theme.GreenTeal21

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VehiclesCards(
    vehiclesState: VehicleModel?,
    onDisactive: (VehicleModel) -> Unit,
    onActive: (VehicleModel) -> Unit
) {
    // Swipeable state untuk menangani swipe action
    val swipeableState = rememberDismissState(confirmStateChange = {
        if (it == DismissValue.DismissedToStart) {
            vehiclesState?.let { vehicle ->
                if (vehicle.status == "ACTIVE") {
                    // Jika kendaraan aktif, maka dinonaktifkan
                    onDisactive(vehicle)
                } else {
                    // Jika kendaraan tidak aktif, maka diaktifkan
                    onActive(vehicle)
                }
            }
            false // Tidak menghapus kartu, hanya aksi
        } else {
            false
        }
    })

    // SwipeToDismiss dengan background yang sesuai
    SwipeToDismiss(
        state = swipeableState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)) // Sama seperti Card
                    .background(
                        if (vehiclesState?.status == "ACTIVE") Color.Red else GreenTeal21
                    )
                    .padding(8.dp), // Sesuaikan dengan Card
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (vehiclesState?.status == "ACTIVE") Icons.Default.Close else Icons.Default.CheckCircle,
                        contentDescription = "Status Icon",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (vehiclesState?.status == "ACTIVE") "Inactive" else "Active",
                        color = Color.White,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        },
        dismissContent = {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (vehiclesState?.status == "ACTIVE") Color(0xFF00579C) else Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    vehiclesState?.images?.firstOrNull()?.let { imageUrl ->
                        Log.d("ImageDebug", "URL: $imageUrl")
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                        )
                        Image(
                            painter = painter,
                            contentDescription = "Vehicle Image",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10)) // Membuat gambar berbentuk lingkaran
                        )
                    }?: Log.e("ImageDebug", "Image URL is null or empty")

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = vehiclesState?.brand ?: "Unknown Brand",
                            color = if (vehiclesState?.status == "ACTIVE") Color.White else Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = vehiclesState?.varian ?: " ",
                            color = if (vehiclesState?.status == "ACTIVE") Color.White else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = vehiclesState?.plateNumber ?: "Unknown Plate",
                            color = if (vehiclesState?.status == "ACTIVE") Color.White else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = vehiclesState?.rfid ?: " ",
                            color = if (vehiclesState?.status == "ACTIVE") Color.White else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}
