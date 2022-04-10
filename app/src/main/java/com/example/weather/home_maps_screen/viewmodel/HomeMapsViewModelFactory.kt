package com.example.weather.home_maps_screen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.WeatherRepoInterface

class HomeMapsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeMapsViewModel::class.java)) {
            HomeMapsViewModel(context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}