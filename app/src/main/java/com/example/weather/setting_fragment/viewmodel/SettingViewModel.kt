package com.example.weather.setting_fragment.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.pojo.SettingData

class SettingViewModel(myContext: Context) : ViewModel() {
    private var sharedPreferences: SharedPreferences =
        myContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private val settingMutableLiveData = MutableLiveData<SettingData>()
    var settingLiveData: LiveData<SettingData> = settingMutableLiveData

    companion object {
        const val SHARED_PREF_NAME = "setting"
        const val TEMP_NAME = "temp"
        const val WIND_SPEED_NAME = "Wind"
        const val LOCATION_NAME = "location"
        const val LANGUAGE_NAME = "language"
        const val NOTIFICATION_NAME = "notification"
    }


    fun saveSetting(setting: SettingData) {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt(TEMP_NAME, setting.tempValue)
        sharedPreferencesEditor.putInt(WIND_SPEED_NAME, setting.windValue)
        sharedPreferencesEditor.putInt(LANGUAGE_NAME, setting.language)
        sharedPreferencesEditor.putInt(NOTIFICATION_NAME, setting.notification)

        sharedPreferencesEditor.apply()
        sharedPreferencesEditor.commit()
    }

    fun getSetting() {
        val temp = sharedPreferences.getInt(TEMP_NAME, 0)
        val wind = sharedPreferences.getInt(WIND_SPEED_NAME, 0)
        val language = sharedPreferences.getInt(LANGUAGE_NAME, 0)
        val notification = sharedPreferences.getInt(NOTIFICATION_NAME, 1)

        val setting = SettingData(temp, wind, notification, language)
        settingMutableLiveData.postValue(setting)
    }


}