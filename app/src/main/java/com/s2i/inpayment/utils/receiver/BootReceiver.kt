//package com.s2i.inpayment.utils.receiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.OneTimeWorkRequest
//import androidx.work.PeriodicWorkRequest
//import androidx.work.WorkManager
//import com.s2i.inpayment.ui.components.services.notifications.NotificationWorker
//import java.util.concurrent.TimeUnit
//
//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent?) {
//        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
//            // BootReceiver - setelah reboot, pastikan channel sudah ada
//            val notificationRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 15, TimeUnit.MINUTES).build()
//            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                "NotificationWorker",
//                ExistingPeriodicWorkPolicy.KEEP,
//                notificationRequest
//            )
//
//        }
//    }
//}