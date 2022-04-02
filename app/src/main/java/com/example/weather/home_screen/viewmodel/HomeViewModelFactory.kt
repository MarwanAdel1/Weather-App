package com.example.weather.home_screen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.WeatherRepoInterface

class HomeViewModelFactory(
    private val _irepo: WeatherRepoInterface,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_irepo,context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}