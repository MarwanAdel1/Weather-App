package com.example.weather.home_screen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.ApiData
import com.example.weather.model.WeatherRepoInterface
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable
import com.example.weather.pojo.WeatherResponse
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

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

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun insertWeatherDataToDatabase(cityName: String, cityData: WeatherResponse) {
        var cityWeatherTable: CityWeatherTable = CityWeatherTable(
            cityName,
            cityData.current.dt,
            cityData
        )
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

    fun getWeatherDataFromDatabase(city: String) {
        viewModelScope.launch {
            var data = weatherRepo.getCityDataFromDatabase(city)
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

    fun getWeatherDataFromApi(lat: String, lon: String, unit: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var weatherResponse: WeatherResponse =
                weatherRepo.getWeatherDataFromApi(lat, lon, unit, key)

            withContext(Dispatchers.Main) {
                weatherResponseMutableLiveData.postValue(weatherResponse)
            }
        }
    }

    fun reverseGeocoding() {
        val mGeocoder = Geocoder(context.applicationContext, Locale.getDefault())
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
        cityNameMutableLiveData.postValue(addressString)
    }


    @SuppressLint("MissingPermission")
    fun getLocation() {
        Log.e("TAG", "Test Location 1:")
        val task: Task<Location> = fusedLocationClient.lastLocation
        Log.e("TAG", "Test Location 2:")
        task.addOnSuccessListener {
            Log.e("TAG", "Test Location 3:$it")
            if (it != null) {
                Log.e("TAG", "Test Location : ${it.latitude} - ${it.longitude}")
                location = it

                getWeatherDataFromApi(
                    location!!.latitude.toString(),
                    location!!.longitude.toString(),
                    "metric",
                    ApiData.API_KEY
                )

                reverseGeocoding()
            } else {
                getNewLocation()
            }
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
                ApiData.API_KEY
            )

            reverseGeocoding()
        }
    }

}