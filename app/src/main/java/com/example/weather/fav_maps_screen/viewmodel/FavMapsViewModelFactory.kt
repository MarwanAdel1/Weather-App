package com.example.weather.fav_maps_screen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.WeatherRepoInterface

class FavMapsViewModelFactory (
    private val _irepo: WeatherRepoInterface,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavMapsViewModel::class.java)) {
            FavMapsViewModel(_irepo,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}