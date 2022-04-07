package com.example.weather.network

import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.WeatherResponse

interface RemoteSourceInterface {
    suspend fun getWeatherDataOverNetwork(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ): WeatherResponse

    suspend fun getReverseGeocodingOverNetwork(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse
}