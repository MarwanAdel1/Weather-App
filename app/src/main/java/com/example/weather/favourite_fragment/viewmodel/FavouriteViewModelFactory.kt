package com.example.weather.favourite_fragment.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.WeatherRepoInterface

class FavouriteViewModelFactory(
    private val _irepo: WeatherRepoInterface,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(_irepo, context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}