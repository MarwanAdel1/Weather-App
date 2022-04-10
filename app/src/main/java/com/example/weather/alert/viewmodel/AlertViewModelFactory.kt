package com.example.weather.alert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.fav_maps_screen.viewmodel.FavMapsViewModel
import com.example.weather.model.WeatherRepoInterface

class AlertViewModelFactory(
    private val _irepo: WeatherRepoInterface,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(_irepo,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }

}