package com.example.weather.home_screen.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.adapters.MyDayRecyclerAdapter
import com.example.weather.adapters.MyTimeRecyclerAdapter
import com.example.weather.data.room_database.LocalSource
import com.example.weather.home_screen.viewmodel.HomeViewModel
import com.example.weather.home_screen.viewmodel.HomeViewModelFactory
import com.example.weather.model.WeatherRepo
import com.example.weather.network.RemoteSource
import com.example.weather.pojo.WeatherResponse
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var timeRecyclerView: RecyclerView
    private lateinit var myTimeRecyclerAdapter: MyTimeRecyclerAdapter
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var myDayRecyclerAdapter: MyDayRecyclerAdapter
    private lateinit var weatherResponse: WeatherResponse
    private lateinit var currentTemp: TextView
    private lateinit var currentTempDescription: TextView
    private lateinit var currentWeatherImage: ImageView
    private lateinit var currentDate: TextView
    private lateinit var currentCity: TextView
    private lateinit var currentPressure: TextView
    private lateinit var currentHumidity: TextView
    private lateinit var currentWind: TextView
    private lateinit var currentCloud: TextView
    private lateinit var currentUV: TextView
    private lateinit var currentVisibility: TextView
    private lateinit var noInternetImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        currentTemp = findViewById(R.id.temp_card_textView)
        currentTempDescription = findViewById(R.id.condition_card_textView)
        currentWeatherImage = findViewById(R.id.weather_image)
        currentDate = findViewById(R.id.date_textView)
        currentCity = findViewById(R.id.city_textView)
        currentPressure = findViewById(R.id.pressure_text)
        currentHumidity = findViewById(R.id.humidity_text)
        currentWind = findViewById(R.id.wind_text)
        currentCloud = findViewById(R.id.cloud_text)
        currentUV = findViewById(R.id.uv_text)
        currentVisibility = findViewById(R.id.visibility_text)
        noInternetImage = findViewById(R.id.no_internet_image)
        progressBar = findViewById(R.id.progress_indicator)
        scrollView = findViewById(R.id.scrollView)

        scrollView.visibility = View.INVISIBLE
        noInternetImage.visibility = View.INVISIBLE


        timeRecyclerView = findViewById(R.id.time_recyclerview)
        timeRecyclerView.setHasFixedSize(true)
        val timeLinearLayoutManager = LinearLayoutManager(this)
        timeLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        timeRecyclerView.layoutManager = timeLinearLayoutManager

        myTimeRecyclerAdapter = MyTimeRecyclerAdapter(this)

        timeRecyclerView.adapter = myTimeRecyclerAdapter

        dayRecyclerView = findViewById(R.id.day_recyclerview)
        dayRecyclerView.setHasFixedSize(true)
        val dayLinearLayoutManager = LinearLayoutManager(this)
        dayLinearLayoutManager.orientation = RecyclerView.VERTICAL
        dayRecyclerView.layoutManager = dayLinearLayoutManager

        myDayRecyclerAdapter = MyDayRecyclerAdapter(this)

        dayRecyclerView.adapter = myDayRecyclerAdapter


        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepo.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(this),
                this
            ),
            this
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        checkLocationPermission()

        if (isNetworkAvailable(this)) {
            if (isInternetAvailable()) {
                Log.e("TAG", "Testt: 1")
                homeViewModel.getLocation()
                Log.e("TAG", "Testt: 2")
            } else {
                Log.e("TAG", "Testt: 3")
                homeViewModel.getWeatherDataFromDatabase("Alexandria Governorate")
                Log.e("TAG", "Testt: 4")
            }
        } else {
            Log.e("TAG", "Testt: 5")
            homeViewModel.getWeatherDataFromDatabase("Alexandria Governorate")
            Log.e("TAG", "Testt: 6")
        }


        homeViewModel.weatherResponseLiveData.observe(this) {
            progressBar.visibility = View.INVISIBLE
            if (it != null) {
                weatherResponse = it

                noInternetImage.visibility = View.INVISIBLE
                scrollView.visibility = View.VISIBLE

                myDayRecyclerAdapter.setDailyWeatherList(weatherResponse.daily)
                myDayRecyclerAdapter.notifyDataSetChanged()

                myTimeRecyclerAdapter.setHourlyWeatherList(weatherResponse.hourly)
                myTimeRecyclerAdapter.notifyDataSetChanged()

                currentTemp.text = weatherResponse.current.temp.toBigDecimal().setScale(
                    1,
                    RoundingMode.FLOOR
                ).toString()
                currentTempDescription.text = weatherResponse.current.weather[0].description

                Log.e("TAG", "Hi : ${weatherResponse.timezone}")

                val iconUrl =
                    "https://openweathermap.org/img/wn/${weatherResponse.current.weather[0].icon}@2x.png"
                Log.i("TAG", "setWeather: iconUrl   $iconUrl")
                Glide.with(applicationContext).load(iconUrl)
                    .centerCrop()
                    .into(currentWeatherImage)


                currentDate.text = SimpleDateFormat("E, ddMMM").format(Date())


                currentPressure.text = weatherResponse.current.pressure.toString()
                currentHumidity.text = weatherResponse.current.humidity.toString()
                currentWind.text = weatherResponse.current.wind_speed.toString()
                currentCloud.text = weatherResponse.current.clouds.toString()
                currentUV.text = weatherResponse.current.uvi.toString()
                currentVisibility.text = weatherResponse.current.visibility.toString()

                homeViewModel.insertWeatherDataToDatabase(
                    currentCity.text.toString(),
                    weatherResponse
                )
            } else {
                scrollView.visibility = View.INVISIBLE
                noInternetImage.visibility = View.VISIBLE
            }
        }

        homeViewModel.cityNameLiveData.observe(this) {
            currentCity.text = it
        }
    }


    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> homeViewModel.getLocation()
                PackageManager.PERMISSION_DENIED -> checkLocationPermission()
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun isInternetAvailable(): Boolean {
        val isConnected: Boolean
        val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
        isConnected = process.waitFor() == 0
        return isConnected
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}