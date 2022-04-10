package com.example.weather.home_maps_screen.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.SettingData
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import com.google.android.gms.maps.model.LatLng

class HomeMapsViewModel(
    private var myContext: Context
) : ViewModel() {
    private val settingMutableLiveData = MutableLiveData<SettingData>()

    private var sharedPreferences: SharedPreferences =
        myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)


    fun saveLocation(location: LatLng) {
        val shEditor = sharedPreferences.edit()
        shEditor.putString("lat", location.latitude.toString())
        shEditor.putString("lng", location.longitude.toString())
        shEditor.apply()
    }

    companion object

}