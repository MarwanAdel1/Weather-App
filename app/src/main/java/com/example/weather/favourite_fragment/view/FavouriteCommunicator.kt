package com.example.weather.favourite_fragment.view

import com.example.weather.pojo.FavouriteCityTable

interface FavouriteCommunicator {
    fun onClickDeleteCityFromFavouriteCities(city:FavouriteCityTable)
    fun onClickCityView(city: FavouriteCityTable)
}