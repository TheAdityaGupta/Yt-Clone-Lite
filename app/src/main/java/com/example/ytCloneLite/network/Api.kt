package com.assignment.ytCloneLite.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private const val BASE_URL = "https://gypfcdkgsrkwyiulmhjv.supabase.co"
    private const val API_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd5cGZjZGtnc3Jrd3lpdWxtaGp2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTQ5ODAwMTgsImV4cCI6MjAzMDU1NjAxOH0.lpueAndJLHZS5pZ7Gy_qmez1v-N3roy-RyaiR6UCmnM" // Ensure this is kept secure

    private fun getHttpClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader("apikey", API_KEY).build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().client(getHttpClient()).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val supabaseService: SupabaseService = getRetrofit().create(SupabaseService::class.java)
}
