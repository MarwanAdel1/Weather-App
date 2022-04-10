package com.example.weather.main_activity.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            MainActivityViewModel(context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}