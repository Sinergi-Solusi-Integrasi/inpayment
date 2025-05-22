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
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.s2i.common.utils.convert.RupiahFormatter
import com.s2i.common.utils.date.Dates
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.inpayment.ui.theme.Success
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.DarkGreen


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailTrxCard(
    transactionDetail: HistoryBalanceModel?,
    usersState: ProfileModel?,
    excludeImage: Boolean,
) {
    val imageLoader: ImageLoader = getKoin().get()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showImage by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Simpan dalam variable yang bisa digunakan di seluruh fungsi
    var plateNumber by remember { mutableStateOf("-") }
    var receiptNumber by remember { mutableStateOf("-") }

    val context = LocalContext.current
    val density = LocalDensity.current

// Ambil plate number dari usersState dan tollPayment
    usersState?.selectVehicle?.let { vehicle ->
        Log.d(
            "ProfileCard",
            "Vehicle data: brand=${vehicle.brand}, model=${vehicle.model}, plateNumber=${vehicle.plateNumber}"
        )
        plateNumber = vehicle.plateNumber ?: "-"
    }

    // Konten scrollable di bawah header
    transactionDetail?.let { detail ->
        // Prioritaskan plateNumber dari detail transaksi jika ada
        detail.tollPayment?.plateNumber?.let { tollPlateNumber ->
            Log.d("DetailTrxCard", "Using plate number from transaction: $tollPlateNumber")
            plateNumber = tollPlateNumber
        }
        detail.tollPayment?.receiptNumber?.let { tollReceiptNumber ->
            receiptNumber = tollReceiptNumber
        }

//        val formattedTime = Dates.formatTimeDifference(
//            startTime = Dates.parseIso8601(detail.trxDate),
//            endTime = System.currentTimeMillis()
//        )
        val formattedTime = Dates.formatTimeFromIso8601(detail.trxDate)
        val formattedDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(
            Date(Dates.parseIso8601(detail.trxDate))
        )
//        val shortenedTransactionId = detail.transactionId.take(10) + "..."

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Card untuk Detail Transaksi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(Color.White),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.matermark2),
                        contentDescription = "matermark",
                        modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Transaction Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val isFailed = detail.status.lowercase() == "failed"
                            val statusText = when {
                                isFailed -> "TRANSAKSI GAGAL"
                                detail.title.lowercase().contains("top up") -> "Top Up Successful"
                                else -> "Toll Payment Successful"
                            }

                            val statusColor = if (isFailed) Color.Red else Color(0xFF2E7D32)

                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (!isFailed) {
                                    // Lingkaran dengan ikon ceklis di tengah
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(
                                                color = statusColor.copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ceklis),
                                            contentDescription = "Berhasil",
                                            tint = statusColor,
                                            modifier = Modifier.size(70.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                )
                            }

                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        // Amount Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = RupiahFormatter.formatToRupiah(detail.amount),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Transaction Details Section
                        Text(
                            text = "TRANSAKSI DETAIL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Details - Simple Row Implementation
                        if (detail.title.lowercase() != "top up") {
                            TransactionDetailRow("Lokasi Gerbang Tol", detail.title)
                            TransactionDetailRow(
                                "Metode Pembayaran",
                                when (detail.paymentMethod) {
                                    "WALLET_CASH" -> "Saldo Bablas"
                                    else -> detail.paymentMethod
                                }
                            )
                            TransactionDetailRow("Nomor Kendaraan", plateNumber)
                            TransactionDetailRow("Nomor Struk", receiptNumber)
                        } else {
                            TransactionDetailRow("Jenis Transaksi", "Top Up Saldo Bablas")
                            detail.topUp?.let { topUp ->
                                TransactionDetailRow("Nama Penerbit", topUp.issuerName ?: "-")
                                TransactionDetailRow("PAN Penerbit", topUp.issuerPan ?: "-")
                                TransactionDetailRow("PAN Pelanggan", topUp.customerPan ?: "-")
                                TransactionDetailRow(
                                    "Biaya Administrasi",
                                    RupiahFormatter.formatToRupiah(detail.fee)
                                )
                            }
                        }

                        TransactionDetailRow("Tanggal Transaksi", formattedDate)
                        TransactionDetailRow("Waktu Transaksi", formattedTime)

                        // Transaction ID dengan copy button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ID Transaksi",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = detail.transactionId.take(20) + if (detail.transactionId.length > 20) "..." else "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreen,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("ID Transaksi berhasil disalin")
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Transaction ID",
                                        tint = DarkGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total Section
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PAYMENT",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = RupiahFormatter.formatToRupiah(detail.amount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Vehicle Image Section
                        if (!excludeImage && detail.title.lowercase() != "top up") {
                            detail.tollPayment?.vehicleCaptures?.firstOrNull()?.let { imageUrl ->
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "DOKUMENTASI KENDARAAN",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedButton(
                                    onClick = {
                                        selectedImageUri = imageUrl
                                        showPreview = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF4CAF50)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "LIHAT FOTO KENDARAAN",
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Footer
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Struk ini merupakan bukti pembayaran yang sah",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Harap simpan sebagai bukti transaksi",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Powered by BABLAS - Electronic Toll Payment System",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Simple Detail Row Implementation - Add this as a separate composable function
            @Composable
            fun SimpleDetailRow(label: String, value: String) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ": $value",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // SnackbarHost untuk menampilkan snackbar
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    } ?: run {
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
fun captureView(view: View, excludeImage: (Boolean) -> Unit): Bitmap {
    excludeImage(true)
    val width = view.width.takeIf { it > 0 } ?: 100
    val height = view.height.takeIf { it > 0 } ?: 600
    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    excludeImage(false)
    return bitmap.copy(Bitmap.Config.ARGB_8888, true)
}

// Function to save the bitmap to file
fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
    val fileName = "receipt_${System.currentTimeMillis()}.png"
    val safeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // ✅ API 29 (Android 10) ke atas: Gunakan MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/INPayment"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1) // Tandai sedang disimpan
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    safeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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

        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = File(picturesDir, "INPayment")
        if (!saveDir.exists()) saveDir.mkdirs()

        val file = File(saveDir, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/png"),
                null
            )

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(value))
                    onCopy()
                },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = Success
                )
            }
        }

        Text(
            text = if (value.length > 20) {
                value.substring(0, 20) + "\n" + value.substring(20)
            } else {
                value
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.widthIn(max = 200.dp),
            textAlign = TextAlign.End
        )
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