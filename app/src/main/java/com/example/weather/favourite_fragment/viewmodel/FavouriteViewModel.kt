package com.example.weather.favourite_fragment.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.FavouriteCityTable
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(
    iRepo: WeatherRepoInterface,
    myContext: Context
) : ViewModel() {
    private val favRepo: WeatherRepoInterface = iRepo

    private val favCitiesMutableLiveData = MutableLiveData<List<FavouriteCityTable>>()
    var favCitiesLiveData: LiveData<List<FavouriteCityTable>> = favCitiesMutableLiveData

    private var sharedPreferences: SharedPreferences =
        myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun getFavCityFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = favRepo.getAllFavouriteCitiesFromDatabase()
            withContext(Dispatchers.Main) {
                favCitiesMutableLiveData.postValue(data)
            }
        }
    }


    fun deleteFavCityFromDatabase(city: FavouriteCityTable) {
        viewModelScope.launch(Dispatchers.IO) {
            favRepo.deleteFavouriteCityFromDatabase(city)
        }
        getFavCityFromDatabase()
    }

    fun saveLocation(location: LatLng) {
        val shEditor = sharedPreferences.edit()
        shEditor.putString("lat", location.latitude.toString())
        shEditor.putString("lng", location.longitude.toString())
        shEditor.apply()
    }

}