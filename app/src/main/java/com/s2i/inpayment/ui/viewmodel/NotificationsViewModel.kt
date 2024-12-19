package com.s2i.inpayment.ui.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.balance.HistoryBalanceModel
import com.s2i.domain.usecase.notifications.GetNotificationTrxUseCase
import com.s2i.domain.usecase.notifications.services.DevicesTokenUseCase
import com.s2i.inpayment.utils.NotificationManagerUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Tag

class NotificationsViewModel(
    private val notifyTrxUseCase: GetNotificationTrxUseCase,
    private val devicesTokenUseCase: DevicesTokenUseCase,
    private val sessionManager: SessionManager,
    application: Application
): AndroidViewModel(application) {

    private val _getNotifyTrx = MutableStateFlow<HistoryBalanceModel?>(null)
    val getNotifyTrx: MutableStateFlow<HistoryBalanceModel?> = _getNotifyTrx

    private val _error = MutableStateFlow<String?>(null)
    val error: MutableStateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> = _loading

    private var lastTransactionId: String? = null

    // get notifications
//    fun fetchNotifyTrx() {
//        Log.d("NotificationsViewModel", "Fetching transaction notifications")
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                val result = notifyTrxUseCase()
//                _getNotifyTrx.value = result.data
//
//                val currentTransactions = result.data
//                val currentTransactionId = currentTransactions?.transactionId
//                if (currentTransactionId != lastTransactionId) {
//
//                    // Trigger push notification here
//                    val title = "Transaction Notification"
//                    val message = when (currentTransactions?.trxType) {
//                        "TOP_UP" -> "${currentTransactions.title} sebesar ${currentTransactions.amount} berhasil top-up"
//                        "TOLL_PAYMENT" -> "Pembayaran ${currentTransactions.title} sebesar ${currentTransactions.amount} berhasil bayar tol"
//                        else -> "${currentTransactions?.title} sebesar ${currentTransactions?.amount} telah berhasil"
//                    }
//
//                    // Show notification
//                    NotificationManagerUtil.showExpandableNotification(
//                        getApplication(),
//                        title,
//                        message,
//                        NotificationManagerUtil.TRANSACTION_CHANNEL_ID,
//                        currentTransactionId.hashCode()
//                    )
//
//                    // Kirim data ke FCM jika dibutuhkan
//                    sendTransactionNotificationToFCM(title, message)
//
//                    // Update transaksi terakhir
//                    lastTransactionId = currentTransactionId
//                    Log.d("NotificationsViewModel", "Transaction ID updated: $currentTransactionId")
//                }
//
//            } catch (e: Exception) {
//                _error.value = e.message
//            } finally {
//                _loading.value = false
//            }
//        }
//
//    }

    fun registerDevices(
        brand: String,
        model: String,
        osType: String,
        platform: String,
        sdkVersion: String,
        tokenFirebase: String

    ) {
        Log.d("NotificationsViewModel", "Device details - Brand: $brand, Model: $model, OS: $osType, Platform: $platform, SDK: $sdkVersion, Token: $tokenFirebase")
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = devicesTokenUseCase(
                    brand = brand,
                    model = model,
                    osType = osType,
                    platform = platform,
                    sdkVersion = sdkVersion,
                    tokenFirebase = tokenFirebase
                )
                // Save devices info
                sessionManager.createDeviceToken(
                    devicesId = result.data.devicesId,
                    deviceBrand = brand,
                    deviceModel = model,
                    osType = osType,
                    devicePlatform = platform,
                    sdkVersion = sdkVersion,
                    deviceToken = tokenFirebase
                )
                // Log device info
                Log.d(
                    "NotificationsViewModel",
                    "Device info successfully saved: ID=${result.data.devicesId}, Brand=$brand, Model=$model, OS=$osType, Platform=$platform, SDK=$sdkVersion, Token=$tokenFirebase"
                )
                Log.d(
                    "NotificationsViewModel",
                    "Device token registered successfully: ${result.message}"
                )
            } catch (e: Exception) {
                Log.e("NotificationsViewModel", "Error registering device token: ${e.message}")
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun sendTransactionNotificationToFCM(title: String, message: String) {
        // Kirim data ke FCM (Anda bisa menggunakan Firebase Admin SDK di server untuk mengirimkan notifikasi)
        // FCM push message API (untuk pengiriman notifikasi ke client)
        // Di sisi server, Anda akan mengirimkan notifikasi melalui FCM API.
       FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
           if (!task.isSuccessful){
               Log.w(TAG, "Feetching FCM Registration token faileD", task.exception)
               return@OnCompleteListener
           }
       })

        // Kirimkan pesan menggunakan API FCM (gunakan FCM REST API atau Admin SDK di server)
        // Gunakan HTTP request untuk mengirimkan data ini ke FCM
        Log.d("NotificationsViewModel", "FCM message sent: $title - $message")
    }


}