package com.example.weather.pojo

data class SettingData(
    var tempValue: Int,  // 0 celsius - 1 Kelvin - 2 Fahrenheit
    var windValue: Int,  // 0 m/s - 1 mile/hr
    var location: Int,   // 0 gps - 1 map
    var notification: Int, // 0 enable - 1 disable
    var language: Int     // 0 english - 1 arabic
)
