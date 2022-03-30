package com.example.weather.pojo

data class Daily(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var moonrise: Long,
    var moonset: Long,
    var moon_phase: Double,
    var temp: Temperature,
    var feels_like: FeelsLike,
    var pressure: Long,
    var humidity: Int,
    var dew_point: Double,
    var wind_speed: Double,
    var wind_deg: Long,
    var wind_gust: Double,
    var weather: ArrayList<Weather>,
    var clouds: Int,
    var pop: Double,
    var uvi: Double
)
