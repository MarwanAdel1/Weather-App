package com.example.weather.fav_maps_screen.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.FavouriteCityTable
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.SettingData
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavMapsViewModel(
    iRepo: WeatherRepoInterface,
    myContext: Context
) : ViewModel() {
    private val favRepo: WeatherRepoInterface = iRepo

    private val cityNameMutableLiveData = MutableLiveData<String>()


    var cityNameLiveData: LiveData<String> = cityNameMutableLiveData


    private val settingMutableLiveData = MutableLiveData<SettingData>()

    private var sharedPreferences: SharedPreferences =
        myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var lang: String


    fun reverseGeocoding(lat: String, lon: String, key: String) {
        getSetting()
        val at: String = TextUtils.concat(lat, ",", lon) as String
        viewModelScope.launch(Dispatchers.IO) {
            val address: ReverseGeocodingResponse =
                favRepo.getReverseGeocodingFromApi(at, lang, key)
            withContext(Dispatchers.Main) {
                if (address.items.isNotEmpty()) {
                    cityNameMutableLiveData.postValue(address.items[0].address.district)
                } else {
                    cityNameMutableLiveData.postValue("null")
                }
            }
        }
    }

    fun insertFavouriteCityToDatabase(favouriteCityTable: FavouriteCityTable) {
        viewModelScope.launch(Dispatchers.IO) {
            favRepo.insertFavouriteCityToDatabase(favouriteCityTable)
        }
    }


    private fun saveSetting() {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt(SettingViewModel.TEMP_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.WIND_SPEED_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.LOCATION_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.LANGUAGE_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.NOTIFICATION_NAME, 1)

        sharedPreferencesEditor.apply()
    }

    private fun getSetting() {
        if (!sharedPreferences.contains(SettingViewModel.TEMP_NAME)) {
            saveSetting()
        }

        val temp = sharedPreferences.getInt(SettingViewModel.TEMP_NAME, 0)
        val wind = sharedPreferences.getInt(SettingViewModel.WIND_SPEED_NAME, 0)
        val location = sharedPreferences.getInt(SettingViewModel.LOCATION_NAME, 0)
        val language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
        val notification = sharedPreferences.getInt(SettingViewModel.NOTIFICATION_NAME, 1)

        val setting = SettingData(temp, wind, notification, language)
        settingMutableLiveData.postValue(setting)

        lang = when (language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }
    }

}