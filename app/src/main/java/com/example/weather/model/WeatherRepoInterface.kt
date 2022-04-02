package com.example.weather.model

import androidx.lifecycle.LiveData
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable
import com.example.weather.pojo.WeatherResponse

interface WeatherRepoInterface {
    suspend fun getWeatherDataFromApi(lat: String, lon: String, unit: String, key: String): WeatherResponse


    //room-stored movies
    fun insertCityDataToDatabase(city: CityWeatherTable)
    fun deleteCityDataFromDatabase(city: CityWeatherTable)
    suspend fun getCityDataFromDatabase(city: String): CityWeatherTable

    fun insertFavouriteCityToDatabase(city: FavouriteCityTable)
    fun deleteFavouriteCityFromDatabase(city: FavouriteCityTable)
    suspend fun getAllFavouriteCitiesFromDatabase(): List<FavouriteCityTable>
}