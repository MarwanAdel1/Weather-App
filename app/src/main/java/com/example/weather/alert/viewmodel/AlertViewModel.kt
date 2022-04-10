package com.example.weather.alert.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.ApiKeys
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.AlertResponse
import com.example.weather.pojo.AlertTable
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertViewModel(
    iRepo: WeatherRepoInterface,
    myContext: Context
) : ViewModel() {
    private val weatherRepo: WeatherRepoInterface = iRepo

    private var alertResponseMutableLiveData = MutableLiveData<AlertResponse>()
    var alertResponseLiveData: LiveData<AlertResponse> = alertResponseMutableLiveData

    private var alertMutableLiveData = MutableLiveData<List<AlertTable>>()
    var alertLiveData: LiveData<List<AlertTable>> = alertMutableLiveData

    private lateinit var lang: String
    private lateinit var lat: String
    private lateinit var lng: String

    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences =
            myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        getSetting()
    }


    fun getWeatherDataFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("TAG", "onViewCreated7:")
            val alertResponse: AlertResponse? =
                weatherRepo.getAlertDataFromApi(lat, lng, lang, ApiKeys.WEATHER_API_KEY)
            Log.e("TAG", "onViewCreated8:")
            withContext(Dispatchers.Main) {
                alertResponseMutableLiveData.postValue(alertResponse)
                Log.e("TAG", "onViewCreated5: $alertResponse")

            }
        }
    }


    fun getSetting() {
        val language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
        lat = sharedPreferences.getString("lat", "0")!!
        lng = sharedPreferences.getString("lng", "0")!!

        lang = when (language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }
    }


    fun insertAlertToDb(alert: AlertTable) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.insertAlertToDatabase(alert)
            getAlert()
        }
    }

    fun getAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepo.getAlert()
            withContext(Dispatchers.Main) {
                alertMutableLiveData.postValue(data)
            }
        }
    }

    fun deleteAlert(alert: AlertTable) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.deleteAlert(alert)
            getAlert()
        }
    }

}