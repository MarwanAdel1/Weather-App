package com.example.weather.data.room_database

import android.content.Context
import com.example.weather.pojo.AlertTable
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable

class LocalSource(context: Context) : LocalSourceInterface {
    private val weatherDAO: WeatherDAO

    init {
        val db: RoomDb = RoomDb.getInstance(context)
        weatherDAO = db.weatherDAO()
    }


    companion object {
        private var instance: LocalSource? = null
        fun getInstance(context: Context): LocalSource {
            return instance ?: LocalSource(context)
        }
    }

    override fun insertCityData(city: CityWeatherTable) {
        weatherDAO.insertWeatherDataToCity(city)
    }

    override fun deleteCityData(city: CityWeatherTable) {
        weatherDAO.deleteWeatherDataForCity(city)
    }

    override fun insertFavouriteCity(city: FavouriteCityTable) {
        weatherDAO.insertFavouriteCity(city)
    }

    override fun deleteFavouriteCity(city: FavouriteCityTable) {
        weatherDAO.deleteFavouriteCity(city)
    }

    override suspend fun getCityData(city: String): CityWeatherTable {
        return weatherDAO.getWeatherDataByCityName(city)
    }

    override suspend fun getAllFavouriteCities(): List<FavouriteCityTable> {
        return weatherDAO.getAllFavouriteCityName()
    }

    override fun insertAlert(alert: AlertTable) {
        weatherDAO.insertAlert(alert)
    }

    override suspend fun getAlert(): List<AlertTable> {
        return weatherDAO.getAlert()
    }

    override fun deleteAlert(alert: AlertTable) {
        weatherDAO.deleteAlert(alert)
    }

    override fun deleteAllAlert() {
        weatherDAO.deleteAllAlert()
    }
}