package com.example.weather.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_city")
data class FavouriteCityTable(
    @PrimaryKey
    @ColumnInfo(name = "City")
    val cityName: String,
    @ColumnInfo(name = "lat")
    val latitude: String,
    @ColumnInfo(name = "long")
    val longitude: String
)
