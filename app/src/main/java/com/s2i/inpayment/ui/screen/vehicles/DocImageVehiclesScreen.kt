package com.s2i.inpayment.ui.screen.vehicles

import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.Pager
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.common.utils.convert.decodeBase64ToBitmap
import com.s2i.common.utils.convert.saveBitmapToCache
import com.s2i.common.utils.convert.saveBitmapToMediaStore
import com.s2i.common.utils.convert.uriToBitmap
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.gradientBrushCards
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocImageVehiclesScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val vehicleDocUris by vehiclesViewModel.docImageVehiclesState.collectAsState() // ✅ Pastikan collectAsState dipakai

    Log.d("ImageVehiclesScreen", "🔥 vehicleUris saat ini: $vehicleDocUris") // ✅ Tambahkan log
    var uriList by remember { mutableStateOf(emptyList<String>()) }
    var isStartupLoading by remember { mutableStateOf(true) }
    val isLoading by vehiclesViewModel.loading.collectAsState()

    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    // State untuk bottomsheet
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // Logging untuk debugging
    LaunchedEffect(isLoading ) {
        if (isLoading && isStartupLoading) {
            isStartupLoading = true
        } else if (!isLoading){
            delay(500) // Delay opsional untuk membuat animasi lebih halus
            isStartupLoading = false
        }
    }
    // ✅ Konversi URI ke Bitmap saat pertama kali masuk atau URI berubah
    // ✅ Konversi Base64 ke Bitmap saat pertama kali masuk atau gambar berubah
    LaunchedEffect(vehicleDocUris) {
        vehicleDocUris?.let { image ->
            Log.d("DocImageVehiclesScreen", "📸 Converting Base64 to Bitmap...")
            bitmapImage = decodeBase64ToBitmap(image.data)
            if (bitmapImage != null) {
                Log.d("DocImageVehiclesScreen", "✅ Successfully converted Base64 to Bitmap")
            } else {
                Log.e("DocImageVehiclesScreen", "❌ Failed to convert Base64 to Bitmap")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Konten Utama
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

                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Document Vehicles",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

            }
            if (isStartupLoading) {
                // Tampilkan loading screen
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

//             Card Display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .aspectRatio(1.6f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                RoundedCornerShape(16.dp)
                            ),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                    ) { // ✅ Gunakan Box untuk Menjadikan Gambar Sebagai Background
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // ✅ Menampilkan Gambar Kendaraan sebagai Background
                            if (bitmapImage != null) {
                                Image(
                                    bitmap = bitmapImage!!.asImageBitmap(),
                                    contentDescription = "Vehicle Image",
                                    contentScale = ContentScale.Crop, // ✅ Memastikan gambar memenuhi seluruh Box
                                    modifier = Modifier.matchParentSize() // ✅ Membuat gambar sebagai background
                                )
                                Log.d("ImageVehiclesScreen", "✅ Displaying Image from ViewModel")
                            } else {
                                Text(
                                    text = "No Image Available",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                                Log.e("ImageVehiclesScreen", "❌ No image found in ViewModel")
                            }
//                            // ✅ Menampilkan Logo di Pojok Kiri Atas
//                            Box(
//                                modifier = Modifier
//                                    .size(48.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                                    .background(brush = gradientBrushCards())
//                                    .padding(8.dp)
//                                    .align(Alignment.TopStart) // ✅ Pastikan logo berada di pojok kiri atas
//                                    .offset(x = 8.dp, y = 8.dp) // ✅ Tambahkan offset agar tidak terlalu mepet kiri atas
//                            ) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.logo),
//                                    contentDescription = null,
//                                    modifier = Modifier.fillMaxSize()
//                                )
//                            }
                        }
                    }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Documents Vehicles",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please upload the following documents to verify your vehicle.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Tombol di Bagian Bawah
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding() // Hindari tumpang tindih dengan sistem navigasi
        ) {
            Button(
                onClick = { /* TODO: Handle Button Click */
                    navController.navigate("camera_screen") {
                        popUpTo("doc_vehicle_screen") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp), // RoundedRectangle Shape
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // Tambahkan VehiclesInputSheet
//    VehiclesInputSheet(
//        sheetState = sheetState,
//        onDismiss = {
//            coroutineScope.launch {
//                sheetState.hide() // Sembunyikan BottomSheet
//            }
//        },
//        onSubmit = { vehicleDetails ->
//            coroutineScope.launch {
//                sheetState.hide() // Sembunyikan BottomSheet setelah submit
//            }
//            // Lakukan sesuatu dengan data yang di-submit
//            println(vehicleDetails)
//        }
//    )
}
