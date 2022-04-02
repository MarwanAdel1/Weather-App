package com.example.weather.data.room_database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weather_data WHERE City LIKE :city " + "LIMIT 1")
    suspend fun getWeatherDataByCityName(city: String): CityWeatherTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherDataToCity(city: CityWeatherTable)

    @Query("SELECT * FROM fav_city")
    suspend fun getAllFavouriteCityName(): List<FavouriteCityTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteCity(city: FavouriteCityTable)

    @Delete
    fun deleteWeatherDataForCity(city: CityWeatherTable)

    @Delete
    fun deleteFavouriteCity(city: FavouriteCityTable)

}