//package com.s2i.inpayment.ui.components
//
//import android.Manifest
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.view.View
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import java.io.File
//import java.util.UUID
//import androidx.core.graphics.createBitmap
//import java.io.FileOutputStream
//
//// Fungsi untuk mengambil tangkapan layar dari tampilan
//fun captureView(view: View): Bitmap {
//    val width = view.width.takeIf { it > 0 } ?: 100
//    val height = view.height.takeIf { it > 0 } ?: 600
//    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(bitmap)
//    view.draw(canvas)
//    return bitmap
//}
//
//
//// Function to save the bitmap to file
//fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
//    val fileName = "receipt_${System.currentTimeMillis()}.png"
//
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        // ✅ API 29 (Android 10) ke atas: Gunakan MediaStore
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/INPayment")
//            put(MediaStore.Images.Media.IS_PENDING, 1) // Tandai sedang disimpan
//        }
//
//        val resolver = context.contentResolver
//        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        if (uri != null) {
//            try {
//                resolver.openOutputStream(uri)?.use { outputStream ->
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                }
//                contentValues.clear()
//                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
//                resolver.update(uri, contentValues, null, null)
//
//                MediaScannerConnection.scanFile(context, arrayOf(uri.toString()), null, null)
//
//                return uri
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        null
//    } else {
//        // ✅ API 28 (Android 9) ke bawah: Simpan manual ke penyimpanan eksternal
//        if (ContextCompat.checkSelfPermission(
//                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return null // Harus meminta izin dulu
//        }
//
//        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val saveDir = File(picturesDir, "INPayment")
//        if (!saveDir.exists()) saveDir.mkdirs()
//
//        val file = File(saveDir, fileName)
//        return try {
//            val outputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//
//            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/png"), null)
//
//            Uri.fromFile(file)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//}
//
//
//// Function to share a screenshot
//fun shareScreenshot(context: Context, fileUri: Uri) {
//    val shareIntent = Intent(Intent.ACTION_SEND).apply {
//        type = "image/png"
//        putExtra(Intent.EXTRA_STREAM, fileUri)
//        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    }
//    context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
//}