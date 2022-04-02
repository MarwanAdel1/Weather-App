package com.example.weather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRetrofitClient {
    companion object {
        private val API_URL = "https://api.openweathermap.org/data/2.5/"
        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }


}