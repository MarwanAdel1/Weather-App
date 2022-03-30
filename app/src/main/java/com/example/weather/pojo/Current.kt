package com.example.weather.pojo

data class Current(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
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
    var weather: ArrayList<Weather>
)
