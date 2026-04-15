package com.example.passpoint.data.remote

import com.example.passpoint.domain.UserRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserApiService {
    private const val URL = "https://tzeqggnubimyfappfuab.supabase.co"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR6ZXFnZ251YmlteWZhcHBmdWFiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ1NDk5NjYsImV4cCI6MjA5MDEyNTk2Nn0.5vZDFy-TcQuRLueb1073R9Pxnpr0vWqMSXyNT2ym6ig"

    fun create(): UserApi {
        val auth = { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer ${UserRepository.user_token}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(requestWithHeaders)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UserApi::class.java)
    }
}