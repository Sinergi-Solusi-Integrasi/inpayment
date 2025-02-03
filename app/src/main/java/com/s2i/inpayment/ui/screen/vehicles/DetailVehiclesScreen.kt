package com.s2i.inpayment.ui.screen.vehicles

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.common.utils.convert.decodeBase64ToBitmap
import com.s2i.common.utils.convert.saveBitmapToMediaStore
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.button.SplitReceiptBottomBar
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailVehiclesScreen(
    navController: NavController,
    vehicleId: String,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val vehiclesState by vehiclesViewModel.getVehiclesState.collectAsState()
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

    val pagerState = rememberPagerState(pageCount = {displayedImages.size})
    val currentUserId = selectedVehicle?.ownerUserId // Mendapatkan ID user saat ini

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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

                    Log.d("ImageLoader", "Memuat gambar: $imageUrl")

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

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailItem(label = "Brand", value = selectedVehicle?.brand ?: "-")
                DetailItem(label = "Model", value = selectedVehicle?.model ?: "-")
                DetailItem(label = "Varian", value = selectedVehicle?.varian ?: "-")
                DetailItem(label = "Color", value = selectedVehicle?.color ?: "-")
                DetailItem(label = "Plate Number", value = selectedVehicle?.plateNumber ?: "-")
                DetailItem(label = "Status", value = selectedVehicle?.status ?: "-")
                DetailItem(label = "Is Owner", value = if (selectedVehicle?.isOwner == true) "Yes" else "No")

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

        // Mengganti BottomAppBar dengan Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding() // Menambahkan padding untuk navigasi bar
        ) {
            SplitReceiptBottomBar() // Menempatkan komponen di bagian bawah
        }
    }

    if (showBottomSheet) {
        ReusableBottomSheet(sheetState = sheetState) {
            VehiclesInputSheet(
                navController = navController,
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
