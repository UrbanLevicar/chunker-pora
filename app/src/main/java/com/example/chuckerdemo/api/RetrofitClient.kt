package com.example.chuckerdemo.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080/api/"

    fun createApiService(context: Context): ApiService {

        val chuckerCollector = ChuckerCollector(
            context = context, // Kontekst aplikacije
            showNotification = false, // Prikaži obvestilo ko kaj prestrežemo
            retentionPeriod = RetentionManager.Period.ONE_HOUR // Kako dolgo hraniti prestrežene podatke
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector) // Da ve kam shranjevati podatke
            .maxContentLength(250_000L) // 250 KB
            .alwaysReadResponseBody(true) // Vedno prebere telo odgovora
            .build()

        // OkHttp z Chuckerjem
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)  // CHUCKER
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}