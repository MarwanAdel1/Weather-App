package com.example.weather.data.room_database

import androidx.lifecycle.LiveData
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable

interface LocalSourceInterface {
    fun insertCityData(city: CityWeatherTable)
    fun deleteCityData(city: CityWeatherTable)
    suspend fun getCityData(city: String): CityWeatherTable

    fun insertFavouriteCity(city: FavouriteCityTable)
    fun deleteFavouriteCity(city: FavouriteCityTable)
    suspend fun getAllFavouriteCities(): List<FavouriteCityTable>
}