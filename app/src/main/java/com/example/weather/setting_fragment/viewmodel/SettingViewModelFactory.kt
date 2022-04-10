package com.example.weather.setting_fragment.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            SettingViewModel(context) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}