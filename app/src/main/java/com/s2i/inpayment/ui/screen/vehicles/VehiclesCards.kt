package com.s2i.inpayment.ui.screen.vehicles

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.CachePolicy
import coil3.request.crossfade
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.decode.DataSource
import coil3.network.httpHeaders
import coil3.request.crossfade
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.vehicle.VehicleModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.GreenTeal21
import kotlinx.coroutines.launch
import org.koin.core.Koin
import kotlin.math.roundToInt
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VehiclesCards(
    vehiclesState: VehicleModel?,
    onDisactive: (String) -> Unit,
    onActive: (String) -> Unit
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
                                swipeOffsetX.value = (swipeOffsetX.value + dragAmount)
                                    .coerceIn(maxFullSwipe, 0f)
                            },
                            onDragEnd = {
                                if (swipeOffsetX.value <= maxPartialSwipe) {
                                    coroutineScope.launch {
                                        isPartialSwipe.value = true
                                        swipeOffsetX.value = maxPartialSwipe
                                    }
                                } else {
                                    coroutineScope.launch {
                                        isPartialSwipe.value = false
                                        swipeOffsetX.value = 0f
                                    }
                                }
                            }
                        )
                    }
                    .clickable { vehiclesState?.let { toggleVehicleStatus(it.vehicleId!!) } }
                    .offset { IntOffset(swipeOffsetX.value.toInt(), 0) },
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
                        val sizeResolver = rememberConstraintsSizeResolver()
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                // Menambahkan header Authorization
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true) // Opsional: crossfade untuk transisi yang mulus
//                                .placeholder(R.drawable.placeholder) // Placeholder saat gambar belum dimuat
//                                .error(R.drawable.error_image) // Gambar fallback jika terjadi error
                                .build(),
                            imageLoader = imageLoader,

                            )
                        Image(
                            painter = painter,
                            contentDescription = "Vehicle Image",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10)) // Membuat gambar berbentuk lingkaran
                                .background(Color.Gray)
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
