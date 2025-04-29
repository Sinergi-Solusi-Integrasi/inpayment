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
import com.s2i.inpayment.MainActivity
import com.s2i.inpayment.R

object NotificationManagerUtil {

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

        // Ambil qrisCode dan amount dari shared preferences atau sumber lain
        val sharedPrefs = context.getSharedPreferences("qris_data", Context.MODE_PRIVATE)
        val qrisCode = sharedPrefs.getString("qris_code_$trxId", "")
        val amount = sharedPrefs.getInt("amount_$trxId", 0)

        // Buat Intent untuk membuka QrisScreen
        val intent = Intent(context, MainActivity::class.java).apply {
            action = "ACTION_OPEN_QRIS"
            putExtra("EXTRA_QRIS_CODE", qrisCode)
            putExtra("EXTRA_TRX_ID", trxId)
            putExtra("EXTRA_AMOUNT", amount)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            trxId.hashCode(), // Unique request code based on trxId
            intent,
            pendingIntentFlag
        )

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
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(context).notify(trxId.hashCode(), notificationBuilder.build())
    }
    // Tambahkan metode untuk menyimpan data QRIS ke shared preferences
    fun saveQrisData(context: Context, trxId: String, qrisCode: String, amount: Int) {
        val sharedPrefs = context.getSharedPreferences("qris_data", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("qris_code_$trxId", qrisCode)
            putInt("amount_$trxId", amount)
            apply()
        }
    }
}
