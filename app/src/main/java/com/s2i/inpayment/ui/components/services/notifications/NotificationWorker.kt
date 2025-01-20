package com.s2i.inpayment.ui.components.services.notifications

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.s2i.domain.usecase.wallet.OrderQueryQrisUseCase
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.utils.NotificationManagerUtil
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {

    private val orderQueryUseCase: OrderQueryQrisUseCase by inject()

    override suspend fun doWork(): Result {
        val trxId = inputData.getString("trxId") ?: return Result.failure()

        return try {
            val orderResponse = orderQueryUseCase.invoke(trxId)

            val statusMessage = when (orderResponse.rCode) { // Sesuaikan nama properti
                "00" -> "Pembayaran Berhasil"
                "99" -> "Pembayaran Pending"
                else -> "Pembayaran Gagal: ${orderResponse.message}"
            }

            // Panggil NotificationHelper untuk menampilkan notifikasi
            NotificationManagerUtil.showNotification(applicationContext, trxId, orderResponse.message, statusMessage)

            //Jadwalkan ulang worker untuk 5 detik ke depan
//            scheduleOrderQueryWorker(applicationContext, trxId)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(trxId: String, title: String, messageBody: String) {
        val channelId = "CheckStatusChannel"
        val channelName = "Check Status Notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Pengecekan izin
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationWorker", "Permission not granted for notifications.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title.ifBlank { "Status Pembayaran" })
            .setContentText("trxId: $trxId - $messageBody")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        NotificationManagerCompat.from(applicationContext).notify(trxId.hashCode(), notificationBuilder.build())
    }

}