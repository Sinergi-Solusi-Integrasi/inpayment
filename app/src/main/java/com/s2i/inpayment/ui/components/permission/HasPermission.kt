package com.s2i.inpayment.ui.components.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun hasAllPermissions(context: Context): Boolean {
    val permissions = listOfNotNull(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else Manifest.permission.READ_EXTERNAL_STORAGE
    )

    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Log permission yang belum diberikan
            android.util.Log.d("PermissionCheck", "Permission not granted: $permission")
        }

    }
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}


