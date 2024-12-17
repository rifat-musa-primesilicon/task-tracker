package com.r_mit.taskt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://graph.microsoft.com/v1.0/"

    // Create and provide a Retrofit instance for the API service
    val apiService: GraphApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GraphApiService::class.java)
    }
}
