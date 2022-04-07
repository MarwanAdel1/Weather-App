package com.example.weather.home_screen.viewmodel

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
import com.example.weather.pojo.*
import com.example.weather.setting_screen.viewmodel.SettingViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    iRepo: WeatherRepoInterface,
    context: Context
) : ViewModel() {
    private val weatherRepo: WeatherRepoInterface = iRepo
    private val weatherResponseMutableLiveData = MutableLiveData<WeatherResponse>()
    private val cityNameMutableLiveData = MutableLiveData<String>()
    private val favCitiesMutableLiveData = MutableLiveData<List<FavouriteCityTable>>()
    private val context = context

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

    var weatherResponseLiveData: LiveData<WeatherResponse> = weatherResponseMutableLiveData
    var cityNameLiveData: LiveData<String> = cityNameMutableLiveData
    var favCitiesLiveData: LiveData<List<FavouriteCityTable>> = favCitiesMutableLiveData

    private val settingMutableLiveData = MutableLiveData<SettingData>()
    var settingLiveData: LiveData<SettingData> = settingMutableLiveData

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var lang: String

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        sharedPreferences =
            context.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun insertWeatherDataToDatabase(cityName: String, cityData: WeatherResponse) {
        var cityWeatherTable: CityWeatherTable = CityWeatherTable(
            cityName,
            cityData.current.dt,
            cityData
        )
        saveCityName(cityName)
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.insertCityDataToDatabase(cityWeatherTable)
        }
    }

    fun insertFavouriteCityToDatabase(cityName: String, cityData: WeatherResponse) {
        var favCity: FavouriteCityTable = FavouriteCityTable(cityName)
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.insertFavouriteCityToDatabase(favCity)
        }
    }

    fun deleteCityDataFromDatabase(cityName: String, cityData: WeatherResponse) {
        var cityWeatherTable: CityWeatherTable = CityWeatherTable(
            cityName,
            cityData.current.dt,
            cityData
        )
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.deleteCityDataFromDatabase(cityWeatherTable)
        }
    }

    fun deleteFavCity(cityName: String, cityData: WeatherResponse) {
        var favCity: FavouriteCityTable = FavouriteCityTable(cityName)
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepo.insertFavouriteCityToDatabase(favCity)
        }
    }

    fun getFavCityFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            var data = weatherRepo.getAllFavouriteCitiesFromDatabase()
            withContext(Dispatchers.Main) {
                favCitiesMutableLiveData.postValue(data)
            }
        }
    }

    fun getWeatherDataFromDatabase() {
        viewModelScope.launch {
            var data = weatherRepo.getCityDataFromDatabase(getCityName())
            withContext(Dispatchers.Main) {
                if (data != null) {
                    cityNameMutableLiveData.postValue(data.cityName)
                    weatherResponseMutableLiveData.postValue(data.weatherData)
                } else {
                    weatherResponseMutableLiveData.postValue(null)
                }
            }
        }
    }

    fun getWeatherDataFromApi(lat: String, lon: String, unit: String, lang: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var weatherResponse: WeatherResponse =
                weatherRepo.getWeatherDataFromApi(lat, lon, unit, lang, key)

            withContext(Dispatchers.Main) {
                weatherResponseMutableLiveData.postValue(weatherResponse)
            }
        }
    }

    fun reverseGeocoding(lat: String, lon: String, lang: String, key: String) {
        var at: String = concat(lat,",",lon) as String
        viewModelScope.launch(Dispatchers.IO) {
            var address: ReverseGeocodingResponse =
                weatherRepo.getReverseGeocodingFromApi(at, lang, key)
            withContext(Dispatchers.Main) {
                cityNameMutableLiveData.postValue(address.items[0].address.city)
            }
        }


        /*val mGeocoder = Geocoder(context.applicationContext, Locale.getDefault())
        var addressString = ""
        if (location != null) {
            val addressList: List<Address> =
                mGeocoder.getFromLocation(
                    location!!.latitude.toDouble(),
                    location!!.longitude.toDouble(),
                    1
                )

            // use your lat, long value here
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                addressString = address.adminArea
            }
        }
        cityNameMutableLiveData.postValue(addressString)*/
    }


    @SuppressLint("MissingPermission")
    fun getLocation() {
        Log.e("TAG", "Test Location 1:")
        var task: Task<Location> = fusedLocationClient.lastLocation
        Log.e("TAG", "Test Location 2:")
        task.addOnSuccessListener {
            Log.e("TAG", "Test Location 3: $it")
            if (it != null) {
                Log.e("TAG", "Test Location : ${it.latitude} - ${it.longitude}")
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
        var locationRequest: LocationRequest
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
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
            Log.e("TAG", "onLocationResult: ${lastLocation.longitude}")

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

    fun saveCityName(city: String) {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putString(CITY_NAME, city)

        sharedPreferencesEditor.apply()
        sharedPreferencesEditor.commit()
    }

    fun getCityName(): String {
        var city = sharedPreferences.getString(CITY_NAME, "Unknown")
        return city.toString()
    }

    companion object {
        val CITY_NAME = "city"
    }

    fun saveSetting() {
        sharedPreferencesEditor = sharedPreferences.edit()

        sharedPreferencesEditor.putInt(SettingViewModel.TEMP_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.WIND_SPEED_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.LOCATION_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.LANGUAGE_NAME, 0)
        sharedPreferencesEditor.putInt(SettingViewModel.NOTIFICATION_NAME, 1)

        sharedPreferencesEditor.apply()
        sharedPreferencesEditor.commit()
    }

    fun getSetting() {
        if (!sharedPreferences.contains(SettingViewModel.TEMP_NAME)) {
            saveSetting()
        }

        var temp = sharedPreferences.getInt(SettingViewModel.TEMP_NAME, 0)
        var wind = sharedPreferences.getInt(SettingViewModel.WIND_SPEED_NAME, 0)
        var location = sharedPreferences.getInt(SettingViewModel.LOCATION_NAME, 0)
        var language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
        var notification = sharedPreferences.getInt(SettingViewModel.NOTIFICATION_NAME, 1)

        var setting: SettingData = SettingData(temp, wind, location, notification, language)
        settingMutableLiveData.postValue(setting)

        lang = when (language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }
    }

}