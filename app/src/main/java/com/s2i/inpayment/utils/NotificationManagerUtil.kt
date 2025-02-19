package com.s2i.inpayment.utils

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
import androidx.core.app.NotificationManagerCompat
import com.s2i.inpayment.R

object NotificationManagerUtil {

//    private const val CHANNEL_ID = "default_channel"
//    const val TRANSACTION_CHANNEL_ID = "transaction_channel"
//    private const val CHAT_CHANNEL_ID = "chat_channel"
//    private const val REMINDER_CHANNEL_ID = "reminder_channel"
//
//    private const val TRANSACTION_GROUP_KEY = "transaction_notifications"
//    private const val CHAT_GROUP_KEY = "chat_notifications"
//    private const val REMINDER_GROUP_KEY = "reminder_notifications"
//
//    fun showExpandableNotification(
//        context: Context,
//        title: String,
//        message: String,
//        channelId: String,
//        notificationId: Int
//    ) {
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Tentukan group key berdasarkan channel ID
//        val groupKey = when (channelId) {
//            TRANSACTION_CHANNEL_ID -> TRANSACTION_GROUP_KEY
//            CHAT_CHANNEL_ID -> CHAT_GROUP_KEY
//            REMINDER_CHANNEL_ID -> REMINDER_GROUP_KEY
//            else -> "default_notifications"
//        }
//
//        // Buat Notification Channel (untuk Android Oreo ke atas)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelName = when (channelId) {
//                TRANSACTION_CHANNEL_ID -> "Transaction Notification"
//                CHAT_CHANNEL_ID -> "Chat Notification"
//                REMINDER_CHANNEL_ID -> "Reminder Notification"
//                else -> "Default Notification"
//            }
//
//            val notificationChannel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//
//        // Intent untuk aksi saat notifikasi di klik (opsional)
////        val intent = Intent(context, TargetActivity::class.java).apply {
////            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////        }
////        val pendingIntent: PendingIntent = PendingIntent.getActivity(
////            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
////        )
//
//        // Gaya BigText untuk expandable notification
//        val style = NotificationCompat.BigTextStyle()
//            .bigText(message)
//            .setBigContentTitle(title)
//
//        // Membuat notifikasi
//        val notification: Notification = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.logo_loading)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setStyle(style)
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas tinggi
//            .setGroup(groupKey)
////            .setContentIntent(pendingIntent) // Aksi saat notifikasi di klik
//            .setAutoCancel(true) // Hilangkan notifikasi setelah di klik
//            .build()
//
//        // Tampilkan notifikasi
//        notificationManager.notify(notificationId, notification)
//    }

    fun showNotification(context: Context, trxId: String, title: String, messageBody: String) {
        val channelId = "CheckStatusChannel"
        val channelName = "Check Status Notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Pengecekan izin untuk Android Tiramisu (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationHelper", "Permission not granted for notifications.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Format Pesan notifikasi
        val formattedMessage = when {
            messageBody.contains("Pembayaran Berhasil",  ignoreCase = true) -> "Pembayaran Anda telah berhasil. Terima kasih!"
            messageBody.contains("Pembayaran Pending",  ignoreCase = true) ->  "Pembayaran Anda sedang diproses. Mohon tunggu."
            else -> "Pembayaran Anda gagal: $messageBody"

        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title.ifBlank { "Status Pembayaran" })
            .setContentText(formattedMessage)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        NotificationManagerCompat.from(context).notify(trxId.hashCode(), notificationBuilder.build())
    }
}
