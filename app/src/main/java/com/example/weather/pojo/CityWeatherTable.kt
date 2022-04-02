package com.example.weather.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weather.data.room_database.WeatherResponseConverter

@TypeConverters(WeatherResponseConverter::class)
@Entity(tableName = "weather_data")
data class CityWeatherTable(
    @PrimaryKey
    @ColumnInfo(name = "City")
    val cityName: String,
    @ColumnInfo(name = "Requested_Date")
    val requestDate: Long,
    @ColumnInfo(name = "Weather_Data")
    @TypeConverters(WeatherResponseConverter::class)
    val weatherData: WeatherResponse
)
