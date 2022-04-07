package com.example.weather.setting_screen.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.pojo.SettingData

class SettingViewModel(private var myContext: Context) : ViewModel() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private val settingMutableLiveData = MutableLiveData<SettingData>()
    var settingLiveData: LiveData<SettingData> = settingMutableLiveData

    companion object {
        val SHARED_PREF_NAME = "setting"
        val TEMP_NAME = "temp"
        val WIND_SPEED_NAME = "Wind"
        val LOCATION_NAME = "location"
        val LANGUAGE_NAME = "language"
        val NOTIFICATION_NAME = "notification"
    }


    init {
        sharedPreferences = myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }


    fun saveSetting(setting: SettingData) {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt(TEMP_NAME, setting.tempValue)
        sharedPreferencesEditor.putInt(WIND_SPEED_NAME, setting.windValue)
        sharedPreferencesEditor.putInt(LOCATION_NAME, setting.location)
        sharedPreferencesEditor.putInt(LANGUAGE_NAME, setting.language)
        sharedPreferencesEditor.putInt(NOTIFICATION_NAME, setting.notification)

        sharedPreferencesEditor.apply()
        sharedPreferencesEditor.commit()
    }

    fun getSetting() {
        var temp = sharedPreferences.getInt(TEMP_NAME, 0)
        var wind = sharedPreferences.getInt(WIND_SPEED_NAME, 0)
        var location = sharedPreferences.getInt(LOCATION_NAME, 0)
        var language = sharedPreferences.getInt(LANGUAGE_NAME, 0)
        var notification = sharedPreferences.getInt(NOTIFICATION_NAME, 1)

        var setting: SettingData = SettingData(temp, wind, location, notification, language)
        settingMutableLiveData.postValue(setting)
    }


}