package com.example.weather.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HereReverseGeocodingApiRetrofitClient() {

    companion object {
        private val API_URL = "https://revgeocode.search.hereapi.com/v1/"
        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit!!
        }
    }
}