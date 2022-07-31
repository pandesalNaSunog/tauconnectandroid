package com.example.tauconnect

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retro by lazy{
        Retrofit.Builder()
            .baseUrl("https://tauconnect.online")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Requests::class.java)
    }
}