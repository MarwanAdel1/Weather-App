package com.example.weather.pojo

import com.example.weather.pojo.Weather

data class Hourly(
    var dt: Long,
    var temp: Double,
    var feels_like: Double,
    var pressure: Long,
    var humidity: Long,
    var dew_point: Double,
    var uvi: Double,
    var clouds: Long,
    var visibility: Long,
    var wind_speed: Double,
    var wind_deg: Long,
    var wind_gust: Double,
    var weather: ArrayList<Weather>,
    var pop: Double
)
