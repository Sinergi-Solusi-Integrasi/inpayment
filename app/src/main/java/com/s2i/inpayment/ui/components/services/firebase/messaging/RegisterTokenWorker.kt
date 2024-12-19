package com.s2i.inpayment.ui.components.services.firebase.messaging

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.s2i.domain.usecase.notifications.services.DevicesTokenUseCase

class RegisterTokenWorker(
    context: Context,
    params: WorkerParameters,
    private val devicesTokenUseCase: DevicesTokenUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("RegisterTokenWorker", "Started worker for token registration.")
        val token = inputData.getString("token") ?: return Result.failure()
        Log.d("RegisterTokenWorker", "Registering token: $token")
        try {
            devicesTokenUseCase(
                brand = Build.BRAND,
                model = Build.MODEL ?: "Unknown",
                osType = Build.VERSION.RELEASE ?: "Unknown",
                platform = "Android",
                sdkVersion = "Android API ${Build.VERSION.SDK_INT}",
                tokenFirebase = token
            )
            Log.d("RegisterTokenWorker", "Token registered successfully.")
            return Result.success()
        } catch (e: Exception) {
            Log.e("RegisterTokenWorker", "Error: ${e.message}")
            return Result.retry()
        }
    }

}
