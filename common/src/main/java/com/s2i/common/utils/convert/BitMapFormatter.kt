package com.s2i.common.utils.convert

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

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