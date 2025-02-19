package com.s2i.common.utils.convert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class ImageCompressor(private val context: Context) {

    /**
     * Fungsi untuk mengompresi gambar berdasarkan target ukuran (dalam byte).
     *
     * @param contentUri Uri dari gambar yang akan dikompresi
     * @param compressionThreshold Ukuran maksimum dalam byte (misal 700 * 1024 untuk 700 KB)
     * @return ByteArray hasil kompresi atau null jika gagal
     */
    suspend fun compressImage(
        contentUri: Uri,
        compressionThreshold: Long
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val mimeType = context.contentResolver.getType(contentUri)
            val inputBytes = context.contentResolver.openInputStream(contentUri)?.use { it.readBytes() }
                ?: return@withContext null

            var bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
            bitmap = resizeBitmapIfNeeded(bitmap, maxWidth = 1280, maxHeight = 1280)

            val compressFormat = when (mimeType) {
                "image/png" -> Bitmap.CompressFormat.PNG
                "image/webp" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSLESS
                } else {
                    Bitmap.CompressFormat.WEBP
                }
                else -> Bitmap.CompressFormat.JPEG
            }

            var outputBytes: ByteArray
            var quality = 90

            do {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(compressFormat, quality, outputStream)
                outputBytes = outputStream.toByteArray()
                outputStream.close()

                quality -= 5
            } while (outputBytes.size > compressionThreshold && quality > 10)

            return@withContext outputBytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) return bitmap

        val aspectRatio = width.toFloat() / height.toFloat()
        val (newWidth, newHeight) = if (aspectRatio > 1) {
            maxWidth to (maxWidth / aspectRatio).toInt()
        } else {
            (maxHeight * aspectRatio).toInt() to maxHeight
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
