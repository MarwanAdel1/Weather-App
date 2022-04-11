package com.example.weather.home_fragment.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.data.room_database.LocalSource
import com.example.weather.home_fragment.adapters.MyDayRecyclerAdapter
import com.example.weather.home_fragment.adapters.MyTimeRecyclerAdapter
import com.example.weather.home_fragment.viewmodel.HomeViewModel
import com.example.weather.home_fragment.viewmodel.HomeViewModelFactory
import com.example.weather.model.WeatherRepo
import com.example.weather.network.RemoteSource
import com.example.weather.pojo.SettingData
import com.example.weather.pojo.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment(private var myContext: Context) : Fragment() {
    private lateinit var timeRecyclerView: RecyclerView
    private lateinit var myTimeRecyclerAdapter: MyTimeRecyclerAdapter
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var myDayRecyclerAdapter: MyDayRecyclerAdapter
    private lateinit var weatherResponse: WeatherResponse
    private lateinit var currentTemp: TextView
    private lateinit var currentTempUnit: TextView
    private lateinit var currentTempDescription: TextView
    private lateinit var currentWeatherImage: ImageView
    private lateinit var currentDate: TextView
    private lateinit var currentCity: TextView
    private lateinit var currentPressure: TextView
    private lateinit var currentHumidity: TextView
    private lateinit var currentWind: TextView
    private lateinit var currentWindUnit: TextView
    private lateinit var currentCloud: TextView
    private lateinit var currentUV: TextView
    private lateinit var currentVisibility: TextView
    private lateinit var noInternetImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var locationImage: ImageView
    private lateinit var lastUpdateTimeTx: TextView

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private var setting: SettingData? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews(view)

        myTimeRecyclerAdapter = MyTimeRecyclerAdapter(myContext)
        timeRecyclerView.adapter = myTimeRecyclerAdapter


        myDayRecyclerAdapter = MyDayRecyclerAdapter(myContext)
        dayRecyclerView.adapter = myDayRecyclerAdapter



        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepo.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(myContext),
                myContext
            ),
            myContext
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        homeViewModel.getSetting()

        if (isNetworkAvailable(myContext)) {
            lifecycle.coroutineScope.launch(Dispatchers.IO) {
                val connected = isInternetAvailable()
                withContext(Dispatchers.Main) {
                    if (connected) {
                        homeViewModel.checkForLocationAndGetDataOnline()
                    } else {
                        homeViewModel.checkForLocationAndGetDataOffline()
                    }
                }
            }
        } else {
            homeViewModel.checkForLocationAndGetDataOffline()
        }


        homeViewModel.settingLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                setting = it
            }
        }

        homeViewModel.weatherResponseLiveData.observe(viewLifecycleOwner) {
            progressBar.visibility = View.INVISIBLE
            if (it != null) {
                updateWeatherUi(it)
            } else {
                constraintLayout.visibility = View.INVISIBLE
                noInternetImage.visibility = View.VISIBLE
            }
        }

        homeViewModel.cityNameLiveData.observe(viewLifecycleOwner) {
            if (it == null) {
                currentCity.text = getString(R.string.unknown_place)
            } else {
                currentCity.text = it
            }
        }

        locationImage.setOnClickListener {
            if (isNetworkAvailable(myContext)) {
                lifecycle.coroutineScope.launch(Dispatchers.IO) {
                    val connected = isInternetAvailable()
                    withContext(Dispatchers.Main) {
                        if (connected) {
                            homeViewModel.getLocation()
                        } else {
                            Toast.makeText(myContext, "No Internet Connection", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(myContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews(view: View) {
        currentTemp = view.findViewById(R.id.temp_card_textView)
        currentTempUnit = view.findViewById(R.id.temp_unit_card_textView)
        currentTempDescription = view.findViewById(R.id.condition_card_textView)
        currentWeatherImage = view.findViewById(R.id.weather_image)
        currentDate = view.findViewById(R.id.date_textView)
        currentCity = view.findViewById(R.id.city_textView)
        currentPressure = view.findViewById(R.id.pressure_text)
        currentHumidity = view.findViewById(R.id.humidity_text)
        currentWind = view.findViewById(R.id.wind_text)
        currentWindUnit = view.findViewById(R.id.wind_unit_text)
        currentCloud = view.findViewById(R.id.cloud_text)
        currentUV = view.findViewById(R.id.uv_text)
        currentVisibility = view.findViewById(R.id.visibility_text)
        noInternetImage = view.findViewById(R.id.no_internet_image)
        progressBar = view.findViewById(R.id.progress_indicator)
        scrollView = view.findViewById(R.id.scrollView)
        constraintLayout = view.findViewById(R.id.weather_details_card_constraint)
        locationImage = view.findViewById(R.id.location_icon)
        lastUpdateTimeTx = view.findViewById(R.id.last_update_value_tx)


        constraintLayout.visibility = View.INVISIBLE
        noInternetImage.visibility = View.INVISIBLE


        timeRecyclerView = view.findViewById(R.id.time_recyclerview)
        timeRecyclerView.setHasFixedSize(true)
        val timeLinearLayoutManager = LinearLayoutManager(myContext)
        timeLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        timeRecyclerView.layoutManager = timeLinearLayoutManager


        dayRecyclerView = view.findViewById(R.id.day_recyclerview)
        dayRecyclerView.setHasFixedSize(true)
        val dayLinearLayoutManager = LinearLayoutManager(myContext)
        dayLinearLayoutManager.orientation = RecyclerView.VERTICAL
        dayRecyclerView.layoutManager = dayLinearLayoutManager
    }

    private fun updateWeatherUi(data: WeatherResponse) {
        weatherResponse = data

        progressBar.visibility = View.INVISIBLE
        noInternetImage.visibility = View.INVISIBLE
        constraintLayout.visibility = View.VISIBLE

        lastUpdateTimeTx.text = SimpleDateFormat("h:mm:ss aa").format(Date())

        myDayRecyclerAdapter.setDailyWeatherList(weatherResponse.daily)
        myDayRecyclerAdapter.setSetting(setting!!)
        myDayRecyclerAdapter.notifyDataSetChanged()

        myTimeRecyclerAdapter.setHourlyWeatherList(weatherResponse.hourly)
        myTimeRecyclerAdapter.setSetting(setting!!)
        myTimeRecyclerAdapter.notifyDataSetChanged()


        currentTemp.text = when (setting!!.tempValue) {
            0 -> String.format("%.1f", weatherResponse.current.temp)
            1 -> String.format("%.1f", weatherResponse.current.temp + 273.15)
            2 -> String.format("%.1f", ((weatherResponse.current.temp * 1.8) + 32))
            else -> String.format("%.1f", weatherResponse.current.temp)
        }

        currentTempUnit.text = when (setting!!.tempValue) {
            0 -> getString(R.string.celsius)
            1 -> getString(R.string.kelvin)
            2 -> getString(R.string.fahrenheit)
            else -> getString(R.string.celsius)
        }

        currentTempDescription.text = weatherResponse.current.weather[0].description

        val iconUrl =
            "https://openweathermap.org/img/wn/${weatherResponse.current.weather[0].icon}@2x.png"

        Glide.with(myContext).load(iconUrl)
            .centerCrop()
            .into(currentWeatherImage)

        val lang = when (setting!!.language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }
        val locale = Locale(lang)
        currentDate.text = SimpleDateFormat("E, d MMM", locale).format(Date())


        currentPressure.text = String.format("%d", weatherResponse.current.pressure)
        currentHumidity.text = String.format("%d", weatherResponse.current.humidity)
        currentWind.text = when (setting!!.windValue) {
            0 -> String.format("%.2f", weatherResponse.current.wind_speed)
            1 -> String.format("%.2f", (weatherResponse.current.wind_speed * 2.236936))
            else -> String.format("%.2f", weatherResponse.current.wind_speed)
        }

        currentWindUnit.text = when (setting!!.windValue) {
            0 -> getString(R.string.mpers)
            1 -> getString(R.string.mph)
            else -> getString(R.string.mpers)
        }



        currentCloud.text = String.format("%d", weatherResponse.current.clouds)
        currentUV.text = String.format("%.2f", weatherResponse.current.uvi)
        currentVisibility.text = String.format("%d", weatherResponse.current.visibility)

        homeViewModel.insertWeatherDataToDatabase(
            currentCity.text.toString(),
            weatherResponse
        )

    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun isInternetAvailable(): Boolean {
        val isConnected: Boolean
        val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
        isConnected = process.waitFor() == 0
        return isConnected
    }


}