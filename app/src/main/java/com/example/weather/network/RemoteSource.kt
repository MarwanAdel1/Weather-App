package com.example.weather.network

import android.util.Log
import com.example.weather.pojo.AlertResponse
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.WeatherResponse

class RemoteSource : RemoteSourceInterface {
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
    ): WeatherResponse? {
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
        return response

    }


    override suspend fun getAlertData(
        lat: String,
        lon: String,
        lang: String,
        app_id: String
    ): AlertResponse? {
        weatherApiRetrofit =
            WeatherApiRetrofitClient.getInstance().create(WeatherApiRetrofitInterface::class.java)

        var response :AlertResponse? = weatherApiRetrofit.getAlertDataDefault(lat, lon, "", lang, app_id)
        return response
    }

    override suspend fun getReverseGeocodingOverNetwork(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse {
        reverseGeocodingApiRetrofit = HereReverseGeocodingApiRetrofitClient.getInstance()
            .create(HereReverseGeocodingApiRetrofitInterface::class.java)

        return reverseGeocodingApiRetrofit.getAddressFromHereApi(location, lang, key)
    }
}