package com.example.weather.home_fragment.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Looper
import android.text.TextUtils.concat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.ApiKeys
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.ReverseGeocodingResponse
import com.example.weather.pojo.SettingData
import com.example.weather.pojo.WeatherResponse
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    iRepo: WeatherRepoInterface,
    myContext: Context
) : ViewModel() {
    private val weatherRepo: WeatherRepoInterface = iRepo
    private val weatherResponseMutableLiveData = MutableLiveData<WeatherResponse>()
    private val alternativeWeatherResponseMutableLiveData = MutableLiveData<WeatherResponse>()
    private val cityNameMutableLiveData = MutableLiveData<String>()
    private val alternativeCityNameMutableLiveData = MutableLiveData<String>()
    private val myContext = myContext

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

    var weatherResponseLiveData: LiveData<WeatherResponse> = weatherResponseMutableLiveData
    var alternativeWeatherResponseLiveData: LiveData<WeatherResponse> =
        alternativeWeatherResponseMutableLiveData
    var cityNameLiveData: LiveData<String> = cityNameMutableLiveData
    var alternativeCityNameLiveData = alternativeCityNameMutableLiveData


    private val settingMutableLiveData = MutableLiveData<SettingData>()
    var settingLiveData: LiveData<SettingData> = settingMutableLiveData

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var lang: String
    private lateinit var alterLang: String

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)
        sharedPreferences =
            myContext.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun insertWeatherDataToDatabase(cityName: String, cityData: WeatherResponse) {
        val cityWeatherTable: CityWeatherTable = CityWeatherTable(
            cityName,
            cityData.current.dt,
            cityData
        )
        saveCityName(cityName)
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.insertCityDataToDatabase(cityWeatherTable)
        }
    }

    fun deleteCityDataFromDatabase(cityName: String, cityData: WeatherResponse) {
        val cityWeatherTable: CityWeatherTable = CityWeatherTable(
            cityName,
            cityData.current.dt,
            cityData
        )
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.deleteCityDataFromDatabase(cityWeatherTable)
        }
    }

    private fun getWeatherDataFromDatabase() {
        viewModelScope.launch {
            val data = weatherRepo.getCityDataFromDatabase(getCityName())
            withContext(Dispatchers.Main) {
                if (data != null) {
                    cityNameMutableLiveData.postValue(data.cityName)
                    weatherResponseMutableLiveData.postValue(data.weatherData)
                } else {
                    cityNameMutableLiveData.postValue("Unknown")
                    weatherResponseMutableLiveData.postValue(null)
                }
            }
        }
    }

    fun getWeatherDataFromApi(lat: String, lon: String, unit: String, lang: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse: WeatherResponse? =
                weatherRepo.getWeatherDataFromApi(lat, lon, unit, lang, key)
            withContext(Dispatchers.Main) {
                weatherResponseMutableLiveData.postValue(weatherResponse)
                getAlterWeatherDataFromApi(lat, lon, unit, alterLang, key)
            }
        }
    }

    fun getAlterWeatherDataFromApi(
        lat: String,
        lon: String,
        unit: String,
        lang: String,
        key: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse: WeatherResponse? =
                weatherRepo.getWeatherDataFromApi(lat, lon, unit, lang, key)
            withContext(Dispatchers.Main) {
                alternativeWeatherResponseMutableLiveData.postValue(weatherResponse)
            }
        }
    }

    fun reverseGeocoding(lat: String, lon: String, lang: String, key: String) {
        val at: String = concat(lat, ",", lon) as String
        viewModelScope.launch(Dispatchers.IO) {
            val address: ReverseGeocodingResponse =
                weatherRepo.getReverseGeocodingFromApi(at, lang, key)
            withContext(Dispatchers.Main) {
                if (address.items.isNotEmpty()) {
                    cityNameMutableLiveData.postValue(address.items[0].address.district)
                    alterReverseGeocoding(lat, lon, alterLang, key)
                } else {
                    cityNameMutableLiveData.postValue(null)
                }
            }
        }
    }

    fun alterReverseGeocoding(lat: String, lon: String, lang: String, key: String) {
        val at: String = concat(lat, ",", lon) as String
        viewModelScope.launch(Dispatchers.IO) {
            val address: ReverseGeocodingResponse =
                weatherRepo.getReverseGeocodingFromApi(at, lang, key)
            withContext(Dispatchers.Main) {
                if (address.items.isNotEmpty()) {
                    alternativeCityNameMutableLiveData.postValue(address.items[0].address.district)
                } else {
                    alternativeCityNameMutableLiveData.postValue(null)
                }
            }
        }
    }

    fun checkForLocationAndGetDataOffline() {
        if (sharedPreferences.contains("lat") && sharedPreferences.contains("lng")) {
            getWeatherDataFromDatabase()
        } else {
            getWeatherDataFromDatabase()
        }
    }

    fun checkForLocationAndGetDataOnline() {
        if (sharedPreferences.contains("lat") && sharedPreferences.contains("lng")) {
            val lat: String = sharedPreferences.getString("lat", "0").toString()
            val lng: String = sharedPreferences.getString("lng", "0").toString()

            if (!lat.contentEquals("0") && !lng.contentEquals("0")) {
                getWeatherDataFromApi(lat, lng, "metric", lang, ApiKeys.WEATHER_API_KEY)
                reverseGeocoding(lat, lng, lang, ApiKeys.HERE_API_KEY)
            }
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        var task: Task<Location> = fusedLocationClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {

                saveLocation(it)

                location = it

                getWeatherDataFromApi(
                    location!!.latitude.toString(),
                    location!!.longitude.toString(),
                    "metric",
                    lang,
                    ApiKeys.WEATHER_API_KEY
                )

                reverseGeocoding(
                    location!!.latitude.toString(),
                    location!!.longitude.toString(),
                    lang,
                    ApiKeys.HERE_API_KEY
                )
            } else {
                getNewLocation()
            }
        }
        task.addOnFailureListener {
            task = fusedLocationClient.lastLocation
        }
    }


    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation

            saveLocation(lastLocation)

            getWeatherDataFromApi(
                lastLocation.latitude.toString(),
                lastLocation.longitude.toString(),
                "metric",
                lang,
                ApiKeys.WEATHER_API_KEY
            )

            reverseGeocoding(
                lastLocation.latitude.toString(),
                lastLocation.longitude.toString(),
                lang,
                ApiKeys.HERE_API_KEY
            )
        }
    }

    fun saveLocation(location: Location) {
        val shEditor = sharedPreferences.edit()
        shEditor.putString("lat", location.latitude.toString())
        shEditor.putString("lng", location.longitude.toString())
        shEditor.apply()
    }

    private fun saveCityName(city: String) {
        sharedPreferencesEditor = sharedPreferences.edit()

        if (lang.contentEquals("en")) {
            sharedPreferencesEditor.putString(CITY_NAME_EN, city)
        } else {
            sharedPreferencesEditor.putString(CITY_NAME_AR, city)
        }
        sharedPreferencesEditor.apply()
    }

    private fun getCityName(): String {
        var city: String? = null
        if (lang.contentEquals("en")) {
            city = sharedPreferences.getString(CITY_NAME_EN, "Unknown")
        } else {
            city = sharedPreferences.getString(CITY_NAME_AR, "Unknown")
        }
        return city.toString()
    }

    private fun saveSetting() {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt(SettingViewModel.TEMP_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.WIND_SPEED_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.LANGUAGE_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.NOTIFICATION_NAME, 1)

        sharedPreferencesEditor.apply()
    }

    fun getSetting() {
        if (!sharedPreferences.contains(SettingViewModel.TEMP_NAME)) {
            saveSetting()
        }

        val temp = sharedPreferences.getInt(SettingViewModel.TEMP_NAME, 0)
        val wind = sharedPreferences.getInt(SettingViewModel.WIND_SPEED_NAME, 0)
        val language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
        val notification = sharedPreferences.getInt(SettingViewModel.NOTIFICATION_NAME, 1)

        val setting = SettingData(temp, wind, notification, language)
        settingMutableLiveData.postValue(setting)

        lang = when (language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }

        alterLang = when (language) {
            0 -> "ar"
            1 -> "en"
            else -> "ar"
        }
    }

    companion object {
        const val CITY_NAME_EN = "city-en"
        const val CITY_NAME_AR = "city-ar"

    }

}