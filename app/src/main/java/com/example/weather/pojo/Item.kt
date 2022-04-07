package com.example.weather.pojo

data class Item(
    var title: String,
    var id: String,
    var resultType: String,
    var houseNumberType: String,
    var address: Address,
    var position: Position,
    var access: List<Access>,
    var distance: Int,
    var mapView: MapView,
)
