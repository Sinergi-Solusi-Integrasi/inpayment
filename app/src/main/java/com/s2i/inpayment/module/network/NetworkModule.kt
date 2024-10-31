package com.s2i.inpayment.module.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.s2i.data.BuildConfig
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.client.ApiServices
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { SessionManager(get()) }

    single {
        val sessionManager: SessionManager = get()
        Interceptor { chain ->
            val token = sessionManager.accessToken
            val original = chain.request()
            val request = if (!token.isNullOrEmpty()) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.e("AuthInterceptor", "Error: Token is null or empty $token")
                original
            }
            chain.proceed(request)
        }
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)  // Increased connect timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // Increased write timeout
            .readTimeout(60, TimeUnit.SECONDS)     // Increased read timeout
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    single {
        get<Retrofit>().create(ApiServices::class.java)
    }
}
