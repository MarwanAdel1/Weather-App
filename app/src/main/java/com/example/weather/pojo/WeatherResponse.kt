package com.example.weather.pojo

data class WeatherResponse(
    var lat: Double,
    var lon: Double,
    var timezone: String,
    var timezone_offset: Double,
    var current: Current,
    var minutely: List<Minutely>,
    var hourly: List<Hourly>,
    var daily: List<Daily>
)
