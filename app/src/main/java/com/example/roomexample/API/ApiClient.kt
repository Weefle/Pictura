package com.example.roomexample.API

import com.example.errorhandling.exception.ResultCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL: String = "https://api.unsplash.com/"

    private val gson : Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val httpClient : OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()
    }

    val apiService :  ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
}