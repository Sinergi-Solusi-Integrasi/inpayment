package com.s2i.common.utils.convert

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileManager(
    private val context: Context
) {

    suspend fun saveImages(
        contentUri: Uri,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    context
                        .openFileOutput(fileName, Context.MODE_PRIVATE)
                        .use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                }
        }
    }

    suspend fun saveImage(bytes: ByteArray, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, fileName)
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                    outputStream.write(bytes)
                }
                file // ✅ Return file setelah disimpan
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}