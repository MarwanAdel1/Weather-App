package com.example.weather.model

import android.content.Context
import android.util.Log
import com.example.weather.data.room_database.LocalSourceInterface
import com.example.weather.network.RemoteSourceInterface
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.WeatherResponse

class WeatherRepo private constructor(
    var remoteSourceInterface: RemoteSourceInterface,
    var localSourceInterface: LocalSourceInterface,
    var context: Context
) :
    WeatherRepoInterface {

    companion object {
        private var instance: WeatherRepo? = null

        fun getInstance(
            remoteSourceInterface: RemoteSourceInterface,
            localSourceInterface: LocalSourceInterface,
            context: Context
        ): WeatherRepo {
            return instance ?: WeatherRepo(
                remoteSourceInterface, localSourceInterface, context
            )
        }
    }


    override suspend fun getWeatherDataFromApi(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ): WeatherResponse {
        return remoteSourceInterface.getWeatherDataOverNetwork(lat, lon, unit, lang, key)
    }

    override suspend fun getReverseGeocodingFromApi(
        location: String,
        lang: String,
        key: String
    ): ReverseGeocodingResponse {
        return remoteSourceInterface.getReverseGeocodingOverNetwork(location, lang, key)
    }

    override fun insertCityDataToDatabase(city: CityWeatherTable) {
        localSourceInterface.insertCityData(city)
    }

    override fun deleteCityDataFromDatabase(city: CityWeatherTable) {
        localSourceInterface.deleteCityData(city)
    }

    override suspend fun getCityDataFromDatabase(city: String): CityWeatherTable {
        return localSourceInterface.getCityData(city)
    }

    override fun insertFavouriteCityToDatabase(city: FavouriteCityTable) {
        localSourceInterface.insertFavouriteCity(city)
    }

    override fun deleteFavouriteCityFromDatabase(city: FavouriteCityTable) {
        localSourceInterface.deleteFavouriteCity(city)
    }

    override suspend fun getAllFavouriteCitiesFromDatabase(): List<FavouriteCityTable> {
        return localSourceInterface.getAllFavouriteCities()
    }
}