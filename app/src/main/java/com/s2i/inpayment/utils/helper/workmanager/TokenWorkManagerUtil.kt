package com.s2i.inpayment.utils.helper.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.s2i.inpayment.ui.components.services.token.RefreshTokenWorker
import java.util.concurrent.TimeUnit

object TokenWorkManagerUtil {

    fun startWorkManager(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val workRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "refresh_token_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

    }

    fun stopWorkManager(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("refresh_token_work")
    }
}