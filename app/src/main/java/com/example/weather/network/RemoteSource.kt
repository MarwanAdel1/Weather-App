package com.example.weather.network

import android.util.Log
import com.example.weather.pojo.WeatherResponse

class RemoteSource() : RemoteSourceInterface {
    private lateinit var retrofit: RetrofitInterface

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
        key: String
    ): WeatherResponse {
        retrofit = ApiRetrofitClient.getInstance().create(RetrofitInterface::class.java)
        var response: WeatherResponse?=null
        try {
            response = retrofit.getWeatherDataDefault(
                lat,
                lon,
                unit,
                key
            )
        } catch (e: Exception) {
            Log.e("TAG", "getWeatherDataOverNetwork: $e")
        }
        return response!!

//        if (response.code()<=399){
//            return response.body()!!
//        }else{
//            return response.
//        }


    }
}