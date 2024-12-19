package com.s2i.inpayment.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.s2i.inpayment.R

object NotificationManagerUtil {

    private const val CHANNEL_ID = "default_channel"
    const val TRANSACTION_CHANNEL_ID = "transaction_channel"
    private const val CHAT_CHANNEL_ID = "chat_channel"
    private const val REMINDER_CHANNEL_ID = "reminder_channel"

    private const val TRANSACTION_GROUP_KEY = "transaction_notifications"
    private const val CHAT_GROUP_KEY = "chat_notifications"
    private const val REMINDER_GROUP_KEY = "reminder_notifications"

    fun showExpandableNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tentukan group key berdasarkan channel ID
        val groupKey = when (channelId) {
            TRANSACTION_CHANNEL_ID -> TRANSACTION_GROUP_KEY
            CHAT_CHANNEL_ID -> CHAT_GROUP_KEY
            REMINDER_CHANNEL_ID -> REMINDER_GROUP_KEY
            else -> "default_notifications"
        }

        // Buat Notification Channel (untuk Android Oreo ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = when (channelId) {
                TRANSACTION_CHANNEL_ID -> "Transaction Notification"
                CHAT_CHANNEL_ID -> "Chat Notification"
                REMINDER_CHANNEL_ID -> "Reminder Notification"
                else -> "Default Notification"
            }

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Intent untuk aksi saat notifikasi di klik (opsional)
//        val intent = Intent(context, TargetActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        )

        // Gaya BigText untuk expandable notification
        val style = NotificationCompat.BigTextStyle()
            .bigText(message)
            .setBigContentTitle(title)

        // Membuat notifikasi
        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_loading)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas tinggi
            .setGroup(groupKey)
//            .setContentIntent(pendingIntent) // Aksi saat notifikasi di klik
            .setAutoCancel(true) // Hilangkan notifikasi setelah di klik
            .build()

        // Tampilkan notifikasi
        notificationManager.notify(notificationId, notification)
    }
}
