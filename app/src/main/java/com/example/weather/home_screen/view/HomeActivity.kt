package com.example.weather.home_screen.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.adapters.MyDayRecyclerAdapter
import com.example.weather.adapters.MyTimeRecyclerAdapter
import com.example.weather.network.RetrofitClient
import com.example.weather.network.RetrofitInterface
import com.example.weather.pojo.WeatherResponse
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var timeRecyclerView: RecyclerView
    lateinit var myTimeRecyclerAdapter: MyTimeRecyclerAdapter
    lateinit var dayRecyclerView: RecyclerView
    lateinit var myDayRecyclerAdapter: MyDayRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        timeRecyclerView = findViewById(R.id.time_recyclerview)
        timeRecyclerView.setHasFixedSize(true)
        val timeLinearLayoutManager = LinearLayoutManager(this)
        timeLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        timeRecyclerView.layoutManager = timeLinearLayoutManager

        var myTimeRecyclerAdapter = MyTimeRecyclerAdapter()

        timeRecyclerView.adapter = myTimeRecyclerAdapter


        dayRecyclerView = findViewById(R.id.day_recyclerview)
        dayRecyclerView.setHasFixedSize(true)
        val dayLinearLayoutManager = LinearLayoutManager(this)
        dayLinearLayoutManager.orientation = RecyclerView.VERTICAL
        dayRecyclerView.layoutManager = dayLinearLayoutManager

        var myDayRecyclerAdapter = MyDayRecyclerAdapter()

        dayRecyclerView.adapter = myDayRecyclerAdapter


        var retrofit = RetrofitClient.getInstance().create(RetrofitInterface::class.java)

        var job: Job

        lifecycle.coroutineScope.launch {
            job = CoroutineScope(Dispatchers.IO).launch {
                var response = retrofit.getWeatherDataUnits()

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        var data: WeatherResponse = response.body()!!

                        for(time in data.hourly){
                            val netDate = Date(time.dt*1000)
//                            return netDate.toString()
//                            val date =sdf.format(netDate)

                            Log.e("Tag","Formatted Date Hourly : "+netDate.toString())
                        }

                        for(time in data.minutely){
                            //SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format()
                            val netDate = Date(time.dt*1000)

                            Log.e("Tag","Formatted Date Minutely : "+netDate.toString())
                        }
                        Log.e("TAG", "Hi : " + data.minutely.size)
                    } else {
                        Log.e("TAG", "Bye ")
                    }
                }
            }
        }
    }
}