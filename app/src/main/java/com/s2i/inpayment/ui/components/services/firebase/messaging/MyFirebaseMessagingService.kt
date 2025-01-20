package com.s2i.inpayment.ui.components.services.firebase.messaging

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.s2i.domain.entity.model.notification.services.TollPayPayloadModel
import com.s2i.domain.entity.model.notification.services.TopupPayloadModel
import com.s2i.domain.usecase.notifications.services.DevicesTokenUseCase
import com.s2i.inpayment.MainActivity
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.utils.NotificationManagerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message from: ${remoteMessage.from}")

        // Tangani notifikasi otomatis (FCM Notification)
        remoteMessage.notification?.let {
            val title = it.title ?: "Default Title"
            val body = it.body ?: "Default Body"
            Log.d("FCM", "Notification received - Title: $title, Body: $body")
            showNotification(title, body)
        }

        // Tangani Data Payload
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Default Title"
            val body = remoteMessage.data["body"] ?: "Default Body"
            val transactionId = remoteMessage.data["transaction_id"] ?: ""
            val transactionType = remoteMessage.data["transaction_type"] ?: ""
            val transactionAmount = remoteMessage.data["transaction_amount"] ?: ""
            val transactionDatetime = remoteMessage.data["transaction_datetime"] ?: ""

            Log.d("FCM", "Data Payload - Title: $title, Body: $body, Transaction ID: $transactionId")

            showNotification(title, body) // Panggil fungsi manual
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        // Kirim token ke server
        sendTokenToServer(token)
        subscribeToTopic("Payment")
    }

    private fun sendTokenToServer(token: String) {
        Log.d("FCM", "Sending token to server: $token")
        // Kirim token ke backend Anda
    }


    private fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to topic: $topic")
                } else {
                    Log.e("FCM", "Failed to subscribe to topic: $topic", task.exception)
                }
            }
    }


    private fun showNotification(title: String, body: String) {
        val channelId = "transaction_updates_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent untuk membuka aktivitas saat notifikasi diklik
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Buat Notification Channel untuk Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Transaction Updates", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Bangun notifikasi
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo) // Ganti dengan ikon Anda
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }


}


