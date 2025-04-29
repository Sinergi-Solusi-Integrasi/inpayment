package com.s2i.inpayment

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.module.network.networkModule
import com.s2i.inpayment.module.app.appModule
import com.s2i.inpayment.module.network.retrofitModule
import com.s2i.inpayment.utils.helper.workmanager.TokenWorkManagerUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MyApplication : Application()  {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    retrofitModule
                )
            )
        }

        // jika user masih login pastikan work manager berjalan
        Log.d("MyApplication", "Setting up WorkManager for token refresh...")
        val sessionManager = SessionManager(this)
        if (sessionManager.isUserLogin()) {
            TokenWorkManagerUtil.startWorkManager(this)
        }

    }

}