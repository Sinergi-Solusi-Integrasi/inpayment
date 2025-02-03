package com.s2i.inpayment.ui.components.gallery

import android.content.Context
import android.database.ContentObserver
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.inpayment.ui.components.gallery.function.loadImagesFromDevice
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryContent(
    context: Context = LocalContext.current, // ✅ Default: Ambil dari LocalContext
    preSelectedImages: List<String> = emptyList(), // ✅ Default: Kosongkan list
    onImageSelected: (List<String>) -> Unit = {},
    vehiclesViewModel: VehiclesViewModel = koinViewModel(),
    navController: NavController,
    onDismiss: () -> Unit = { navController.popBackStack() }
) {

    val savedImages by vehiclesViewModel.vehicleUrisState.collectAsState() // ✅ Ambil data dari ViewModel
    val galleryImages = remember { mutableStateOf(emptyList<String>()) }

    val selectedImages = remember {
        mutableStateListOf<String>().apply {
            addAll(preSelectedImages + savedImages) // Sinkronisasi dengan preSelectedImages
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // Sinkronisasi data saat pertama kali masuk atau saat data diubah

    DisposableEffect(Unit) {
        val contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                val newImages = loadImagesFromDevice(context)
                galleryImages.value = newImages

                // Sinkronisasi badge dengan selectedImages
//                selectedImages.clear()
//                selectedImages.addAll(preSelectedImages.filter { newImages.contains(it) })
                selectedImages.retainAll { it in newImages || it  in savedImages}

                // Sinkronisasi selectedImages agar tetap ada meskipun ada data baru
                // Pastikan hanya gambar baru yang ditambahkan
//                newImages.forEach { imagePath ->
//                    if (!selectedImages.contains(imagePath) && selectedImages.size < 4) {
//                        selectedImages.add(imagePath)
//                    }
//                }
            }
        }
        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )

        galleryImages.value = loadImagesFromDevice(context)

        // Sinkronisasi preSelectedImages pada awal
//        preSelectedImages.forEach { imagePath ->
//            if (!selectedImages.contains(imagePath) && selectedImages.size < 4) {
//                selectedImages.add(imagePath)
//            }
//        }


        // Bersihkan observer saat komposisi keluar
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Gallery",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
//                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(galleryImages.value.size) { index ->
                    val imagePath = galleryImages.value[index]
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)) // Rounded corner for the entire Box
                            .clickable {
                                if (selectedImages.contains(imagePath)) {
                                    selectedImages.remove(imagePath)
                                } else if (selectedImages.size < 4) {
                                    selectedImages.add(imagePath)
                                }
                                // Periksa apakah semua gambar dihapus
                                // Panggil callback setiap ada perubahan pada selectedImages
                                vehiclesViewModel.setVehicleUri(selectedImages)
                                onImageSelected(selectedImages)
                            }
                            .padding(4.dp)
                    ) {
                        // Tampilkan gambar disini
                        Image(
                            painter = rememberAsyncImagePainter(imagePath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
//                                .border(
//                                    2.dp,
//                                    if (selectedImages.contains(imagePath)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
//                                    RoundedCornerShape(8.dp)
//                                )
                        )
                        if (selectedImages.contains(imagePath)) {
                            val number = selectedImages.indexOf(imagePath) + 1
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = number.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }

                    }
                }
            }
        }
        if (selectedImages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        onImageSelected(selectedImages)
                        coroutineScope.launch {
                            vehiclesViewModel.setVehicleUri(selectedImages)
                            delay(500)
                            navController.navigate("image_vehicle_screen")
                            Log.d(
                                "GalleryContent",
                                "\uD83D\uDEE0 setVehicleUri Dipanggil dengan: $selectedImages"
                            )

                        }
                    },
                    enabled = selectedImages.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
//                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Text("Next (${selectedImages.size}/4)")
                }
            }
        }
    }
}