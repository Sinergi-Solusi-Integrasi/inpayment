package com.s2i.common.utils.convert

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Converts a Bitmap to a Base64 string.
 *
 * @param bitmap The bitmap to be converted.
 * @param format The image format for compression (default: PNG).
 * @return Base64 encoded string of the bitmap.
 */
fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(format, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

/**
 * Converts a Bitmap to Base64 string and determines its MIME type and file extension.
 *
 * @param bitmap The bitmap to be converted.
 * @param format The image format for compression.
 * @return A Pair of Base64 string and MIME type.
 * @throws IllegalArgumentException If the provided format is unsupported.
 */
fun bitmapToBase64WithFormat(bitmap: Bitmap, format: Bitmap.CompressFormat): Triple<String, String, String> {
    val ext = when (format) {
        Bitmap.CompressFormat.PNG -> ".png"
        Bitmap.CompressFormat.JPEG -> ".jpg" // Mendukung .jpeg juga
        else -> throw IllegalArgumentException("Unsupported image format")
    }

    val mimeType = when (format) {
        Bitmap.CompressFormat.PNG -> "image/png"
        Bitmap.CompressFormat.JPEG -> "image/jpeg"
        else -> throw IllegalArgumentException("Unsupported MIME type")
    }

    val base64Data = bitmapToBase64(bitmap, format)
    Log.d("BitmapConversion", "Bitmap converted: ext=$ext, mimeType=$mimeType")
    return Triple(base64Data, ext, mimeType)
}

/**
 * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
 */
fun correctImageOrientation(
    context: Context,
    filePath: String? = null,
    imageUri: Uri? = null
): Bitmap? {
    var bitmap: Bitmap? = null
    var orientation: Int = ExifInterface.ORIENTATION_NORMAL

    try {
        if (filePath != null) {
            // 🔹 Ambil bitmap dari file path
            bitmap = BitmapFactory.decodeFile(filePath)

            // 🔹 Baca Exif dari file path
            val exif = ExifInterface(filePath)
            orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } else if (imageUri != null) {
            // 🔹 Ambil bitmap dari URI
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 🔹 Baca Exif dari URI
            val exifInputStream = contentResolver.openInputStream(imageUri)
            val exif = exifInputStream?.use { ExifInterface(it) }
            orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL
        }


        // 🔹 Pastikan bitmap tidak null sebelum rotasi
        if (bitmap != null) {
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }

    return bitmap
}

// Fungsi untuk rotasi bitmap
fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

// Helper function to decode base64 string to Bitmap
fun decodeBase64ToBitmap(base64: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

// save bitmap to cache
fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val cacheDir = File(context.cacheDir, "images")
    cacheDir.mkdirs()

    val file = File(cacheDir, "${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    outputStream.flush()
    outputStream.close()

    return Uri.fromFile(file)
}

fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/INPayment") // Simpan ke folder khusus
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return try {
        if (uri != null) {
            val outputStream = resolver.openOutputStream(uri)
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }
            uri
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("ImageSave", "❌ Error saving to MediaStore: ${e.message}", e)
        null
    }
}


fun compressBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Bitmap {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(format, quality, outputStream)
    val byteArray = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}


/**
 * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
 */

fun uriToBitmap(context: Context, imageUri: Uri?): Bitmap? {
    if (imageUri == null) {
        Log.e("ImageVehiclesScreen", "❌ URI yang diberikan null")
        return null
    }

    return try {
        val contentResolver: ContentResolver = context.contentResolver
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> { // Android 9+ (Pie)
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
            else -> { // Android 8.1 (Oreo) ke bawah
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }
        }
    } catch (e: FileNotFoundException) {
        Log.e("ImageVehiclesScreen", "❌ File tidak ditemukan: ${e.message}")
        null
    } catch (e: SecurityException) {
        Log.e("ImageVehiclesScreen", "❌ Tidak memiliki izin membaca gambar: ${e.message}")
        null
    } catch (e: IOException) {
        Log.e("ImageVehiclesScreen", "❌ Kesalahan I/O saat membaca gambar: ${e.message}")
        null
    } catch (e: Exception) {
        Log.e("ImageVehiclesScreen", "❌ Gagal mengubah URI ke Bitmap: ${e.message}")
        null
    }
}