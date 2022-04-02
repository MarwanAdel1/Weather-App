package com.example.weather.pojo

import androidx.room.*

@Entity(tableName = "fav_city")
data class FavouriteCityTable(

    @PrimaryKey
    @ColumnInfo(name ="City")
    val cityName:String


)
