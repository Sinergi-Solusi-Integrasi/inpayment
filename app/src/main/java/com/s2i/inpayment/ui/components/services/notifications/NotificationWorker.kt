//package com.s2i.inpayment.ui.components.services.notifications
//
//import android.app.Application
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.media.RingtoneManager
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.work.CoroutineWorker
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.s2i.domain.usecase.notifications.GetNotificationTrxUseCase
//import com.s2i.inpayment.R
//import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
//import com.s2i.inpayment.utils.NotificationManagerUtil
//import kotlinx.coroutines.delay
//import org.koin.compose.koinInject
//import org.koin.compose.viewmodel.koinViewModel
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//import org.koin.core.context.GlobalContext
//import org.koin.core.parameter.parametersOf
//
//class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params)  {
//
//    override suspend fun doWork(): Result {
//        val title = inputData.getString("title") ?: "No Title"
//        val body = inputData.getString("body") ?: "No Body"
//        val transactionType = inputData.getString("transaction_type")
//
//        Log.d("NotificationWorker", "Received Notification: $title - $body")
//
//        // Menampilkan notifikasi
//        NotificationManagerUtil.showExpandableNotification(
//            applicationContext,
//            title,
//            body,
//            "background_channel",
//            System.currentTimeMillis().toInt()
//        )
//
//        return Result.success()
//    }
//
//
//    private fun showNotification(title: String, messageBody: String) {
//        val channelId = "background_channel"
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.logo_loading)
//            .setContentTitle(title)
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, "Background Channel", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//        notificationManager.notify(1, notificationBuilder.build())
//    }
//}