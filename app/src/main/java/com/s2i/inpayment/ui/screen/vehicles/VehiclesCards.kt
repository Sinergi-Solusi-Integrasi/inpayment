package com.s2i.inpayment.ui.screen.vehicles

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.inpayment.ui.theme.GreenTeal21
import com.s2i.inpayment.ui.theme.Red500
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VehiclesCards(
    vehiclesState: VehicleModel?,
    onDisactive: (String) -> Unit,
    onActive: (String) -> Unit,
    onShowDetail: (String) -> Unit // Tambahkan parameter untuk membuka detail kendaraan
) {
    val imageLoader: ImageLoader = getKoin().get()
    val swipeOffsetX = remember { mutableFloatStateOf(0f) }
    val maxPartialSwipe = -400f // Jarak berhenti swipe pertama
    val maxFullSwipe = -700f    // Jarak swipe penuh untuk menghapus kartu
    val isPartialSwipe = remember { mutableStateOf(false) }
    val isFullySwiped = remember { mutableStateOf(false) } // Status kartu sudah di-swipe penuh

    val coroutineScope = rememberCoroutineScope()
    // Cek jika status kendaraan aktif atau tidak
    val isActive = vehiclesState?.status == "ACTIVE"

    // Fungsi untuk toggle status kendaraan
    fun toggleVehicleStatus(vehicleId: String) {
        if (isActive) {
            onDisactive(vehicleId) // Panggil disable
        } else {
            onActive(vehicleId) // Panggil enable
        }
    }


    // Swipeable state untuk menangani swipe action
    val swipeableState = rememberDismissState(confirmStateChange = {
        if (it == DismissValue.DismissedToStart) {
            vehiclesState?.let { vehicle ->
                if (vehicle.status == "ACTIVE") {
                    // Jika kendaraan aktif, maka dinonaktifkan
                    onDisactive(vehicle.vehicleId!!)
                } else {
                    // Jika kendaraan tidak aktif, maka diaktifkan
                    onActive(vehicle.vehicleId!!)
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

            if (!isFullySwiped.value) {
                // Background untuk Active/Inactive

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)) // Sama seperti Card
                        .background(
                            if (vehiclesState?.status == "ACTIVE") Color.Red else GreenTeal21
                        )
                        .padding(8.dp) // Sesuaikan dengan Card
                        .clickable { // Menambahkan clickable di sini
                            vehiclesState?.let { toggleVehicleStatus(it.vehicleId!!) }
                        },
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
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { vehiclesState?.let { toggleVehicleStatus(it.vehicleId!!) } } // Kirimkan vehicleId
                        )
                        Text(
                            text = if (vehiclesState?.status == "ACTIVE") "Inactive" else "Active",
                            color = Color.White,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { vehiclesState?.let { toggleVehicleStatus(it.vehicleId!!) } } // Kirimkan vehicleId
                        )
                    }
                }
            }
        },
        dismissContent = {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                swipeOffsetX.floatValue = (swipeOffsetX.floatValue + dragAmount)
                                    .coerceIn(maxFullSwipe, 0f)
                            },
                            onDragEnd = {
                                if (swipeOffsetX.floatValue <= maxPartialSwipe) {
                                    coroutineScope.launch {
                                        isPartialSwipe.value = true
                                        swipeOffsetX.floatValue = maxPartialSwipe
                                    }
                                } else {
                                    coroutineScope.launch {
                                        isPartialSwipe.value = false
                                        swipeOffsetX.floatValue = 0f
                                    }
                                }
                            }
                        )
                    }
                    .clickable {
                        vehiclesState?.let {
                            if (isPartialSwipe.value) {
                                toggleVehicleStatus(it.vehicleId!!)
                                coroutineScope.launch {
                                    isPartialSwipe.value = false
                                    swipeOffsetX.floatValue = 0f
                                }
                            } else {
                                onShowDetail(it.vehicleId!!)
                            }
                        }
                    }
                    .offset { IntOffset(swipeOffsetX.floatValue.toInt(), 0) },
                colors = CardDefaults.cardColors(
                    containerColor = if (vehiclesState?.status == "ACTIVE") Color.White else Color.White
                )
            ) {
                // Status badge (smaller size)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp, top = 12.dp)
                ) {
                    val statusColor = when {
                        vehiclesState?.isLoaned == true -> Red500
                        vehiclesState?.status == "ACTIVE" -> Color(0xFF1B5E20) // Darker green
                        else -> Color.Gray
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .height(28.dp) // Smaller height
                            .width(80.dp), // Fixed width
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = statusColor)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (vehiclesState?.isLoaned == true) "Loans" else vehiclesState?.status ?: "ACTIVE",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, // Center the text
                        modifier = Modifier.width(120.dp) // Match image width
                    ) {
                        vehiclesState?.images?.firstOrNull()?.let { imageUrl ->
                            Log.d("ImageDebug", "URL: $imageUrl")
                            // Gunakan request tanpa menentukan size spesifik
                            val request = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true)
                                .build()

                            val painter = rememberAsyncImagePainter(
                                model = request,
                                imageLoader = imageLoader
                            )

                            // Tentukan ukuran landscape pada Box langsung
                            Box(
                                modifier = Modifier
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(10))
                                    .background(Color.Gray)
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Vehicle Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Add spacing between image and text
                            Spacer(modifier = Modifier.height(8.dp))

                            // Brand text centered below the image
                            Text(
                                text = vehiclesState.brand ?: "Unknown Brand",
                                style = MaterialTheme.typography.titleMedium,
                                color = Black,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        } ?: Log.e("ImageDebug", "Image URL is null or empty")
                    }

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = vehiclesState?.model ?: "Unknown Brand",
                            color = if (vehiclesState?.status == "ACTIVE") Black else Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = vehiclesState?.varian ?: " ",
                            color = if (vehiclesState?.status == "ACTIVE") Black else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = vehiclesState?.plateNumber ?: "Unknown Plate",
                            color = if (vehiclesState?.status == "ACTIVE") Black else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = vehiclesState?.rfid ?: " - ",
                            color = if (vehiclesState?.status == "ACTIVE") Black else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
            }
        }
    )
}