package com.s2i.inpayment.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
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
import com.s2i.inpayment.ui.theme.GreenTea
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailTrxCard(
    transactionDetail: HistoryBalanceModel?
) {
    val imageLoader: ImageLoader = getKoin().get()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
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
                .padding(top = 72.dp) // Beri padding agar tidak menimpa header
                .verticalScroll(rememberScrollState()) // Membuat konten scrollable
        ) {
            // Card untuk Detail Transaksi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
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
                        tint = GreenTea,
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

                    // Detail Penerima
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        detail.tollPayment?.vehicleCaptures?.firstOrNull()?.let { imageUrl ->
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
                    }

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
}

// Fungsi untuk mengambil tangkapan layar dari tampilan
fun captureView(view: View): Bitmap {
    val width = view.width.takeIf { it > 0 } ?: 1080
    val height = view.height.takeIf { it > 0 } ?: 1920
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}


// Function to save the bitmap to file
fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "INPayment")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val fileName = "receipt_${UUID.randomUUID()}.png"
    val file = File(directory, fileName)

    return try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf("image/png")
        ) { _, _ -> }

        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
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