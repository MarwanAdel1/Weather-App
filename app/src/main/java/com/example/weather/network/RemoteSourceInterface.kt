package com.example.weather.network

import com.example.weather.pojo.AlertResponse
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.WeatherResponse
import retrofit2.http.Query

interface RemoteSourceInterface {
    suspend fun getWeatherDataOverNetwork(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ): WeatherResponse?

    suspend fun getAlertData(
        lat: String,
        lon: String,
        lang: String,
        app_id: String
    ):AlertResponse?

    suspend fun getReverseGeocodingOverNetwork(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse
}