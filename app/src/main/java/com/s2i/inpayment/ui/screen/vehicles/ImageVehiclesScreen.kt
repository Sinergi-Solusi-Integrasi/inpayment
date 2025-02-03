package com.s2i.inpayment.ui.screen.vehicles

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.common.utils.convert.decodeBase64ToBitmap
import com.s2i.common.utils.convert.saveBitmapToMediaStore
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageVehiclesScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val vehicleImages by vehiclesViewModel.vehicleImageVehiclesState.collectAsState()
    val vehicleUris by vehiclesViewModel.vehicleUrisState.collectAsState() // ✅ Pastikan collectAsState dipakai
    val vehicleDocUris by vehiclesViewModel.docImageVehiclesState.collectAsState() // ✅ Pastikan collectAsState dipakai

    var bitmapDoc by remember { mutableStateOf<Bitmap?>(null) }
    Log.d("ImageVehiclesScreen", "🔥 vehicleUris saat ini: $vehicleUris") // ✅ Tambahkan log
    var uriList by remember { mutableStateOf(emptyList<String>()) }
    var isStartupLoading by remember { mutableStateOf(true) }
    val isLoading by vehiclesViewModel.loading.collectAsState()
    // State untuk bottomsheet
//    val sheetState = rememberModalBottomSheetState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) } // Untuk menampilkan bottom sheet
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

    LaunchedEffect(vehicleUris) {
        if (vehicleUris.isNotEmpty()) {
            val newVehicleImages = mutableListOf<BlobImageModel>()

            vehicleUris.forEach { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uri))
                    val (base64Data, ext, mimeType) = bitmapToBase64WithFormat(bitmap, Bitmap.CompressFormat.JPEG)

                    newVehicleImages.add(
                        BlobImageModel(
                            ext = ext,
                            mimeType = mimeType,
                            data = base64Data
                        )
                    )
                } catch (e: Exception) {
                    Log.e("ImageVehiclesScreen", "❌ Error converting image: ${e.message}")
                }
            }

            if (newVehicleImages.isNotEmpty()) {
                vehiclesViewModel.setVehicleImage(newVehicleImages)
                Log.d("ImageVehiclesScreen", "✅ Vehicle Images Disimpan: ${newVehicleImages.size} images")
            } else {
                Log.e("ImageVehiclesScreen", "❌ No images were converted")
            }
        } else {
            Log.e("ImageVehiclesScreen", "❌ No vehicle URIs found")
        }
    }


    // ✅ Konversi URI ke Bitmap saat pertama kali masuk atau URI berubah
    LaunchedEffect(vehicleDocUris) {
        Log.d("ImageVehiclesScreen", "📸 Converting Base64 to Bitmap...")
        vehicleDocUris?.let { docImages ->
            bitmapDoc = decodeBase64ToBitmap(docImages.data)

        }
        if (bitmapDoc != null) {
            Log.d("ImageVehiclesScreen", "✅ Successfully converted Base64: ${bitmapDoc} to Bitmap")
        } else {
            Log.e("ImageVehiclesScreen", "❌ Failed to convert Base64 to Bitmap")
        }
    }

    // Maksimal 4 gambar yang ditampilkan
    val displayedImages = remember(vehicleUris, bitmapDoc) {
        val images = vehicleUris.take(4).toMutableList() // Ambil maksimal 4 gambar
        if (bitmapDoc != null) images.add("doc_image")
        if (images.size < 5) images.add("add_button") // Jika kurang dari 4, tambahkan tombol tambah
        images
    }

//    LaunchedEffect(vehicleImages, vehicleDocUris) {
//        if (vehicleImages != null && vehicleImages!!.isNotEmpty() && vehicleDocUris != null) {
//            Log.d("ImageVehiclesScreen", "✅ Semua data gambar tersedia, membuka VehiclesInputSheet...")
//            showBottomSheet = true
//        } else {
//            Log.e("ImageVehiclesScreen", "❌ Data gambar belum lengkap, tidak membuka VehiclesInputSheet")
//            showBottomSheet = false
//        }
//    }

    // Pager State untuk navigasi gambar dengan swipe
    val pagerState = rememberPagerState(pageCount = {displayedImages.size})



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
                    text = "Vehicles Images",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

            }
            if (isStartupLoading) {
                // Tampilkan loading screen
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            // Card Display
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .aspectRatio(1.6f)
//                            .clip(RoundedCornerShape(16.dp))
//                            .border(
//                                2.dp,
//                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//                                RoundedCornerShape(16.dp)
//                            ),
//                        elevation = CardDefaults.elevatedCardElevation(6.dp),
//                    ) { // ✅ Gunakan Box untuk Menjadikan Gambar Sebagai Background
//                        Box(
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            // ✅ Menampilkan Gambar Kendaraan sebagai Background
//                            if (vehicleUris.isNotEmpty()) {
//                                val imageUri = Uri.parse(vehicleUris.first())
//                                AsyncImage(
//                                    model = ImageRequest.Builder(LocalContext.current)
//                                        .data(imageUri)
//                                        .crossfade(true)
//                                        .build(),
//                                    contentDescription = "Vehicle Image",
//                                    contentScale = ContentScale.Crop, // ✅ Memastikan gambar memenuhi seluruh Box
//                                    modifier = Modifier.matchParentSize() // ✅ Membuat gambar sebagai background
//                                )
//                            }
//
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
//                        }
//                    }

            // Pager untuk menampilkan gambar dengan swipe
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) { page ->
                val imageUri = displayedImages[page]
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        ),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            imageUri == "add_button" ->
                                // Tombol Tambah Jika Kurang dari 4 Gambar
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { navController.navigate("camera_screen") },
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Image",
                                            tint = Color.White
                                        )
                                    }
                                }
                            imageUri == "doc_image" && bitmapDoc != null -> {
                                // Menampilkan gambar dokumen (Base64 -> Bitmap)
                                Image(
                                    bitmap = bitmapDoc!!.asImageBitmap(),
                                    contentDescription = "Document Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            else -> {
                                val uri = Uri.parse(imageUri)
                                val correctedUri by produceState<Uri?>(initialValue = null) {
                                    val correctedBitmap = correctImageOrientation(context, imageUri = uri)
                                    if (correctedBitmap != null) {
                                        try {
                                            val tempUri = saveBitmapToMediaStore(context, correctedBitmap)

                                            val file = File(tempUri?.path ?: "")

                                            if (file.exists()) {
                                                value = tempUri
                                            } else {
                                                Log.e("ImageVehiclesScreen", "⚠️ File cache tidak ditemukan: $tempUri")
                                                value = uri
                                            }
                                        } catch (e: Exception) {
                                            Log.e("ImageVehiclesScreen", "❌ Gagal menyimpan ke cache: ${e.message}", e)
                                            value = uri // Jika gagal, pakai URI asli
                                        }
                                    } else {
                                        Log.e("ImageVehiclesScreen", "⚠️ Gagal memperbaiki orientasi, menggunakan gambar asli.")
                                        value = uri
                                    }
                                }
//                                val correctedBitmap = remember(uri) {
//                                    correctImageOrientation(context, imageUri = uri)
//                                }
                                if (correctedUri != null) {
                                    // Menampilkan Gambar Kendaraan
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(correctedUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Vehicle Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                } else {
                                    Log.e("ImageVehiclesScreen", "❌ Gagal memuat gambar dari URI: $imageUri")
                                }

                            }
                        }
                    }
                }
            }

            // Indikator Titik
            HorizontalPagerIndicator(
                pagerState = pagerState,
                pageCount = displayedImages.size,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vehicles Images",
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
                    coroutineScope.launch {
                        try {
                            vehiclesViewModel.fetchVehicleImages() // ✅ Ambil data sebelum membuka sheet
                            delay(500) // ✅ Beri waktu agar state diperbarui
                            sheetState.show()
                            showBottomSheet = true
                        } catch (e: Exception) {
                            Log.e("ImageVehiclesScreen", "❌ Gagal membuka VehiclesInputSheet: ${e.message}", e)
                        }
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
    if (showBottomSheet) {
        ReusableBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showBottomSheet = false
            }
        ) {
            VehiclesInputSheet(
                navController = navController,
                vehiclesViewModel = vehiclesViewModel
            )
        }
    }
}
