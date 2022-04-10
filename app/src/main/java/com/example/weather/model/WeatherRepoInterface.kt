package com.example.weather.model

import com.example.weather.pojo.*

interface WeatherRepoInterface {
    suspend fun getWeatherDataFromApi(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ): WeatherResponse?


    suspend fun getAlertDataFromApi(
        lat: String,
        lon: String,
        lang: String,
        app_id: String
    ): AlertResponse?


    suspend fun getReverseGeocodingFromApi(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse


    //room-stored movies
    fun insertCityDataToDatabase(city: CityWeatherTable)
    fun deleteCityDataFromDatabase(city: CityWeatherTable)
    suspend fun getCityDataFromDatabase(city: String): CityWeatherTable

    fun insertFavouriteCityToDatabase(city: FavouriteCityTable)
    fun deleteFavouriteCityFromDatabase(city: FavouriteCityTable)
    suspend fun getAllFavouriteCitiesFromDatabase(): List<FavouriteCityTable>

    fun insertAlertToDatabase(alert: AlertTable)
    suspend fun getAlert(): List<AlertTable>
    fun deleteAlert(alert: AlertTable)
}