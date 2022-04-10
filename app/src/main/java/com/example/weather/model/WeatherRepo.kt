package com.example.weather.model

import android.content.Context
import com.example.weather.data.room_database.LocalSourceInterface
import com.example.weather.network.RemoteSourceInterface
import com.example.weather.pojo.*

class WeatherRepo private constructor(
    private var remoteSourceInterface: RemoteSourceInterface,
    private var localSourceInterface: LocalSourceInterface,
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
    ): WeatherResponse? {
        return remoteSourceInterface.getWeatherDataOverNetwork(lat, lon, unit, lang, key)
    }


    override suspend fun getAlertDataFromApi(
        lat: String,
        lon: String,
        lang: String,
        app_id: String
    ): AlertResponse? {
        return remoteSourceInterface.getAlertData(lat, lon, lang, app_id)
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

    override fun insertAlertToDatabase(alert: AlertTable) {
        localSourceInterface.insertAlert(alert)
    }

    override suspend fun getAlert(): List<AlertTable> {
        return localSourceInterface.getAlert()
    }

    override fun deleteAlert(alert: AlertTable) {
        localSourceInterface.deleteAlert(alert)
    }
}