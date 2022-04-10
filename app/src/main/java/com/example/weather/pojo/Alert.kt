package com.example.weather.pojo

data class Alert(
    var sender_name: String,
    var event: String,
    var start: Int,
    var end: Int,
    var description: String,
    var tags: List<String>
)
