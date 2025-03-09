package com.s2i.inpayment.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.common.utils.date.Dates
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.inpayment.ui.viewmodel.BalanceViewModel
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.core.graphics.createBitmap

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailTrxCard(
    transactionDetail: HistoryBalanceModel?,
) {
    val imageLoader: ImageLoader = getKoin().get()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showImage by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val density = LocalDensity.current


    // Konten scrollable di bawah header
    transactionDetail?.let { detail ->
        val formattedTime = Dates.formatTimeDifference(
            startTime = Dates.parseIso8601(detail.trxDate),
            endTime = System.currentTimeMillis()
        )
        val formattedDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(
            Date(Dates.parseIso8601(detail.trxDate))
        )
        val shortenedTransactionId = detail.transactionId.take(10) + "..."

        Column(
            modifier = Modifier
                .fillMaxSize()
                .layoutId("captureView")
                .padding(top = 72.dp) // Beri padding agar tidak menimpa header
        ) {
            // Card untuk Detail Transaksi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ikon Sukses
                    val iconImageVector = if (detail.status.lowercase() == "failed") Icons.Default.Close else Icons.Default.CheckCircle
                    val iconColor = if (detail.status.lowercase() == "failed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    val statusMessage = if (detail.status.lowercase() == "failed") "Transaction Failed" else "Transaction Successful"
                    val statusDescription = if (detail.status.lowercase() == "failed") "Your transaction ${detail.title} failed" else "Your transaction ${detail.title} was successful"

                    Icon(
                        imageVector = iconImageVector,
                        contentDescription = statusMessage,
                        tint = iconColor,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Pesan Sukses
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.titleLarge,
                        color = iconColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = statusDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Jumlah Transaksi
                    Text(
                        text = RupiahFormatter.formatToRupiah(detail.amount),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Detail Transaksi
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        TransactionDetailRow(
                            label = "Status",
                            value = if (detail.status.lowercase() == "failed") "Failed ❌" else "Completed ✅"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionDetailRow("Payment Method", detail.paymentMethod)
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionDetailRow("Time", formattedTime)
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionDetailRow("Date", formattedDate)
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionDetailRowWithCopy(
                            label = "Transaction ID",
                            value = detail.transactionId,
                            onCopy = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Transaction ID has been copied")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TransactionDetailRow("Amount", RupiahFormatter.formatToRupiah(detail.amount))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total Payment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = RupiahFormatter.formatToRupiah(detail.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    detail.tollPayment?.vehicleCaptures?.firstOrNull()?.let { imageUrl ->
                        Log.d("ImageDebug", "URL: $imageUrl")
                        if (!showImage) {
                            Button(
                                onClick = {
                                    showImage = true
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(text = "See More")
                            }
                        } else {
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
                                    .clickable {
                                        selectedImageUri = imageUrl
                                        showPreview = true
                                    }
                            )
                        }
                    }?: Log.e("ImageDebug", "Image URL is null or empty")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // SnackbarHost untuk menampilkan snackbar
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }  ?: run {
        // Tampilkan jika datanya null
        Text(
            text = "Transaction details not available",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.error
        )
    }

    // Dialog untuk pratinjau foto
    if (showPreview && selectedImageUri != null) {
        PreviewPhotoDialog(imageUrl = selectedImageUri!!) {
            showPreview = false
        }
    }
}

// Fungsi untuk mengambil tangkapan layar dari tampilan
fun captureView(view: View): Bitmap {
    val width = view.width.takeIf { it > 0 } ?: 100
    val height = view.height.takeIf { it > 0 } ?: 600
    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}


// Function to save the bitmap to file
fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
    val fileName = "receipt_${System.currentTimeMillis()}.png"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // ✅ API 29 (Android 10) ke atas: Gunakan MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/INPayment")
            put(MediaStore.Images.Media.IS_PENDING, 1) // Tandai sedang disimpan
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                MediaScannerConnection.scanFile(context, arrayOf(uri.toString()), null, null)

                return uri
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        null
    } else {
        // ✅ API 28 (Android 9) ke bawah: Simpan manual ke penyimpanan eksternal
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null // Harus meminta izin dulu
        }

        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = File(picturesDir, "INPayment")
        if (!saveDir.exists()) saveDir.mkdirs()

        val file = File(saveDir, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/png"), null)

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


// Function to share a screenshot
fun shareScreenshot(context: Context, fileUri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
}



@Composable
fun TransactionDetailRowWithCopy(label: String, value: String, onCopy: () -> Unit) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column {
        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.take(10) + "...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(value))
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Transaction ID has been copied")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Transaction ID",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionDetailRow(label: String, value: String) {
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PreviewPhotoDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val imageLoader: ImageLoader = getKoin().get()
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader
                )

                Text(
                    text = "Vehicles Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Image(
                    painter = painter,
                    contentDescription = "Vehicles Image",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss) {
                    Text(text = "Close")
                }
            }
        }
    }
}
