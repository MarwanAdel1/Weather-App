package com.example.weather.network

import com.example.weather.pojo.WeatherResponse
import retrofit2.Response

interface RemoteSourceInterface {
    suspend fun getWeatherDataOverNetwork(lat: String, lon: String, unit: String, key: String): WeatherResponse
}