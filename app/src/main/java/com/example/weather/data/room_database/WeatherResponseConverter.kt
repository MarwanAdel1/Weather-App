package com.example.weather.data.room_database

import androidx.room.TypeConverter
import com.example.weather.pojo.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherResponseConverter {
    @TypeConverter
    fun toJson(obj: WeatherResponse): String {
        val weatherObj = object : TypeToken<WeatherResponse>() {}.type
        return Gson().toJson(obj, weatherObj)
    }

    /**
     * Convert a json to a list of Images
     */
    @TypeConverter
    fun toObj(json: String): WeatherResponse {
        val weatherObj = object : TypeToken<WeatherResponse>() {}.type
        return Gson().fromJson(json, weatherObj)
    }
}