package com.s2i.inpayment.ui.components.services.token

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.usecase.auth.TokenUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RefreshTokenWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params), KoinComponent{

    private val sessionManager: SessionManager by inject()
    private val tokenUseCase: TokenUseCase by inject()

    override suspend fun doWork(): Result{
        return withContext(Dispatchers.IO){
            try {
                Log.d("RefreshTokenWorker", "Refreshing token...")

                if(sessionManager.isAccessTokenExpired()){
                    Log.d("RefreshTokenWorker", "Access token expired. Attempting to refresh token...")

                    val result = tokenUseCase.refreshAccessTokenIfNeeded()

                    return@withContext if (result.isSuccess) {
                        Log.d("RefreshTokenWorker", "Token refreshed successfully.")
                        Result.success()
                    } else {
                        Log.e("RefreshTokenWorker", "Failed to refresh token: ${result.exceptionOrNull()?.message}")
                        sessionManager.logout()
                        Result.failure()
                    }
                } else {
                    Log.d("RefreshTokenWorker", "Access token is still valid.")
                    Result.success()
                }
            } catch (e: Exception){
                Log.e("RefreshTokenWorker", "Error during token refresh: ${e.message}", e)
                Result.retry() // Jika terjadi error, coba lagi nanti
            }
        }
    }


}