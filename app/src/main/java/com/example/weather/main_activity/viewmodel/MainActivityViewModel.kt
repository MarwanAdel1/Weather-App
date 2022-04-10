package com.example.weather.main_activity.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.weather.pojo.SettingData
import com.example.weather.setting_fragment.viewmodel.SettingViewModel

class MainActivityViewModel(myContext: Context) : ViewModel() {
    private var sharedPreferences: SharedPreferences =
        myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun getSetting(): SettingData {
        val temp = sharedPreferences.getInt(SettingViewModel.TEMP_NAME, 0)
        val wind = sharedPreferences.getInt(SettingViewModel.WIND_SPEED_NAME, 0)
        val location = sharedPreferences.getInt(SettingViewModel.LOCATION_NAME, 0)
        val language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
        val notification = sharedPreferences.getInt(SettingViewModel.NOTIFICATION_NAME, 1)

        return SettingData(temp, wind, notification, language)
    }
}