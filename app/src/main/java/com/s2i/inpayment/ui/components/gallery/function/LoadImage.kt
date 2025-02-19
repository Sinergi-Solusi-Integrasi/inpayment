package com.s2i.inpayment.ui.components.gallery.function

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun loadImagesFromDevice(context: Context): List<String> {
    val recentImages = mutableListOf<String>()
    val olderImage = mutableListOf<String>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN
    )
    val currentTime = Calendar.getInstance().timeInMillis
    val sevenDaysAgo = currentTime - TimeUnit.DAYS.toMillis(7)

    // Query for image taken in the last 7 days
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Images.Media.DATE_TAKEN} >= ?",
        arrayOf(sevenDaysAgo.toString()),
        "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            recentImages.add(contentUri.toString())
        }
    }

    // Query for image taken older than 7 days
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Images.Media.DATE_TAKEN} < ?",
        arrayOf(sevenDaysAgo.toString()),
        "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            olderImage.add(contentUri.toString())
        }
    }
    return recentImages + olderImage
}