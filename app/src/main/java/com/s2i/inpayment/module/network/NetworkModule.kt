package com.s2i.inpayment.module.network

import android.util.Log
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.google.gson.GsonBuilder
import com.s2i.data.BuildConfig
import com.s2i.data.local.auth.SessionManager
import com.s2i.data.remote.client.ApiServices
import com.s2i.data.remote.client.WalletServices
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
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
}

val retrofitModule = module {

    //Qris
    single(named("default")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Qris
    single(named("qris")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.QRIS_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>(named("default")).create(ApiServices::class.java)
    }

    //qris url
    single {
        get<Retrofit>(named("qris")).create(WalletServices::class.java)
    }

    single {
        val okHttpClient: OkHttpClient = get()  // Inject OkHttpClient
        ImageLoader.Builder(get())
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))  // Menggunakan OkHttpClient yang sudah dikonfigurasi
            }
            .build()
    }

}
