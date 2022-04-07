package com.example.weather.network

import android.util.Log
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.WeatherResponse

class RemoteSource() : RemoteSourceInterface {
    private lateinit var weatherApiRetrofit: WeatherApiRetrofitInterface
    private lateinit var reverseGeocodingApiRetrofit: HereReverseGeocodingApiRetrofitInterface

    companion object {
        private var instance: RemoteSource? = null
        fun getInstance(): RemoteSource {
            return instance ?: RemoteSource()
        }
    }

    override suspend fun getWeatherDataOverNetwork(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ): WeatherResponse {
        weatherApiRetrofit =
            WeatherApiRetrofitClient.getInstance().create(WeatherApiRetrofitInterface::class.java)
        var response: WeatherResponse? = null
        try {
            response = weatherApiRetrofit.getWeatherDataDefault(
                lat,
                lon,
                unit,
                lang,
                key
            )
        } catch (e: Exception) {
            Log.e("TAG", "getWeatherDataOverNetwork: $e")
        }
        return response!!

    }

    override suspend fun getReverseGeocodingOverNetwork(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse {
        reverseGeocodingApiRetrofit = HereReverseGeocodingApiRetrofitClient.getInstance()
            .create(HereReverseGeocodingApiRetrofitInterface::class.java)

        var response = reverseGeocodingApiRetrofit.getAddressFromHereApi(location, lang, key)

        return response
    }
}