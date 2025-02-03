package com.s2i.inpayment.ui.screen.vehicles

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.decodeBase64ToBitmap
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesInputSheet(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val vehicleOCRState by vehiclesViewModel.vehicleOCRState.collectAsState()
    val vehicleImages by vehiclesViewModel.vehicleImageVehiclesState.collectAsState()
    val docImage by vehiclesViewModel.docImageVehiclesState.collectAsState()


    var brand by remember { mutableStateOf(vehicleOCRState["brand"] ?: "") }
    var model by remember { mutableStateOf(vehicleOCRState["model"] ?: "") }
    var variant by remember { mutableStateOf(vehicleOCRState["variant"] ?: "") }
    var color by remember { mutableStateOf(vehicleOCRState["color"] ?: "") }
    var type by remember { mutableStateOf(vehicleOCRState["type"] ?: "") }
    var plateNumber by remember { mutableStateOf(vehicleOCRState["plateNumber"] ?: "") }

    val isBrandValid by remember { derivedStateOf { brand.isNotEmpty() } }
    val isModelValid by remember { derivedStateOf { model.isNotEmpty() } }
    val isVariantValid by remember { derivedStateOf { variant.isNotEmpty() } } // ✅ Variant wajib diisi
    val isColorValid by remember { derivedStateOf { color.isNotEmpty() } }
    val isTypeValid by remember { derivedStateOf { type.isNotEmpty() } }
    val isPlateValid by remember { derivedStateOf { plateNumber.isNotEmpty() } }
    val isFormValid by remember {
        derivedStateOf { isBrandValid && isModelValid && isVariantValid && isColorValid && isTypeValid && isPlateValid }
    }

    val isLoading by vehiclesViewModel.loading.collectAsState()
    val registVehiclesState by vehiclesViewModel.registVehicles.collectAsState()

    // ✅ Pastikan gambar sudah tersedia sebelum tombol ditekan
    LaunchedEffect(Unit) {
        try {
            Log.d("VehiclesInputSheet", "📥 Memuat data gambar kendaraan dari ViewModel...")
            vehiclesViewModel.fetchVehicleImages()
        } catch (e: Exception) {
            Log.e("VehiclesInputSheet", "❌ Gagal memuat gambar kendaraan: ${e.message}", e)
        }
    }

    LaunchedEffect(Unit) {
        if (vehicleImages != null && vehicleImages!!.isNotEmpty() && docImage != null) {
            // Hanya akan dipanggil jika semua data lengkap
            Log.d("VehiclesInputSheet", "📥 Semua data lengkap, siap untuk didaftarkan.")
        }
    }


    // ✅ Menampilkan Toast saat registrasi berhasil
    LaunchedEffect(registVehiclesState) {
        registVehiclesState?.let {
            Toast.makeText(context, "Vehicle Registered Successfully!", Toast.LENGTH_SHORT).show()
            vehiclesViewModel.clearVehicleData()  // Membersihkan data lama
            navController.navigate("vehicles_screen") {
                popUpTo("vehicles_input_screen") { inclusive = true }
                launchSingleTop = true
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Details") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
                    CustomLinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                // Form Input dengan ScrollView
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Agar form memenuhi layar dan tetap bisa di-scroll
                    verticalArrangement = Arrangement.Top
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        VehicleInputFields(
                            brand = brand, onBrandChange = { brand = it }, isBrandValid = isBrandValid,
                            model = model, onModelChange = { model = it }, isModelValid = isModelValid,
                            variant = variant, onVariantChange = { variant = it }, isVariantValid = isVariantValid,
                            color = color, onColorChange = { color = it }, isColorValid = isColorValid,
                            type = type, onTypeChange = { type = it }, isTypeValid = isTypeValid,
                            plateNumber = plateNumber, onPlateChange = { plateNumber = it }, isPlateValid = isPlateValid
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Tombol Submit
                Button(
                    onClick = {
                        if (docImage != null && vehicleImages != null && vehicleImages!!.isNotEmpty()) {
                            val documentBitmap = decodeBase64ToBitmap(docImage!!.data)
                            val vehicleBitmaps = vehicleImages!!.mapNotNull { decodeBase64ToBitmap(it.data) }

                            val selectedFormat = Bitmap.CompressFormat.JPEG // Default format
                            if (documentBitmap != null && vehicleBitmaps.isNotEmpty()) {
                                vehiclesViewModel.registVehicles(
                                    brand = brand,
                                    model = model,
                                    varian = variant,
                                    color = color,
                                    type = type,
                                    plateNumber = plateNumber,
                                    documentBitmap = documentBitmap,
                                    vehicleBitmaps = vehicleBitmaps,
                                    imageFormat = selectedFormat
                                )
                            } else {
                                Toast.makeText(context, "Error converting images.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please upload document and vehicle images.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = isFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(text = "Submit", fontSize = 16.sp)
                    }
                }
            }
        }
    )
}

@Composable
fun VehicleInputFields(
    brand: String, onBrandChange: (String) -> Unit, isBrandValid: Boolean,
    model: String, onModelChange: (String) -> Unit, isModelValid: Boolean,
    variant: String, onVariantChange: (String) -> Unit, isVariantValid: Boolean,
    color: String, onColorChange: (String) -> Unit, isColorValid: Boolean,
    type: String, onTypeChange: (String) -> Unit, isTypeValid: Boolean,
    plateNumber: String, onPlateChange: (String) -> Unit, isPlateValid: Boolean
) {
    Column {
        InputField(value = brand, onValueChange = onBrandChange, label = "Brand", isValid = isBrandValid)
        InputField(value = model, onValueChange = onModelChange, label = "Model", isValid = isModelValid)
        InputField(value = variant, onValueChange = onVariantChange, label = "Variant", isValid = isVariantValid) // ✅ Variant wajib diisi
        InputField(value = color, onValueChange = onColorChange, label = "Color", isValid = isColorValid)
        InputField(value = type, onValueChange = onTypeChange, label = "Type", isValid = isTypeValid)
        InputField(value = plateNumber, onValueChange = onPlateChange, label = "Plate Number", isValid = isPlateValid)
    }
}

@Composable
fun InputField(value: String, onValueChange: (String) -> Unit, label: String, isValid: Boolean) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = !isValid,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (!isValid) Icon(Icons.Filled.Error, contentDescription = "Error", tint = Color.Red)
            }
        )
        if (!isValid) {
            Text(
                text = "$label is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

