package com.example.weather.network

import com.example.weather.pojo.AlertResponse
import com.example.weather.pojo.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiRetrofitInterface {
    @GET("onecall")
    suspend fun getWeatherDataDefault(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String
    ): WeatherResponse


    @GET("onecall")
    suspend fun getAlertDataDefault(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = "current,minutely,hourly,daily",
        @Query("lang") lang: String="ar",
        @Query("APPID") app_id: String
    ): AlertResponse?
}