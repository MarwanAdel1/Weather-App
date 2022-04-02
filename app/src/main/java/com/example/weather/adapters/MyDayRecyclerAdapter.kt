package com.example.weather.adapters

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.R
import com.example.weather.pojo.Daily
import java.text.SimpleDateFormat
import java.util.*

class MyDayRecyclerAdapter(private var context: Context) :
    RecyclerView.Adapter<MyDayRecyclerAdapter.ViewHolder>() {
    private var dailyWeatherList: List<Daily> = ArrayList()


    fun setDailyWeatherList(dailyWeatherList: List<Daily>): Unit {
        this.dailyWeatherList = dailyWeatherList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.day_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        SimpleDateFormat("dd/MM/yyyy h aa").format(dailyWeatherList[position].dt*1000)
        var date = Date(dailyWeatherList[position].dt * 1000)
        var str: String
        if (DateUtils.isToday(date.time)) {
            str = "Today"
        } else if (DateUtils.isToday(date.time - DateUtils.DAY_IN_MILLIS)) {
            str = "Tomorrow"
        } else {
            str = SimpleDateFormat("EEEE").format(dailyWeatherList[position].dt * 1000)
        }
        holder.dayTextView.text = str
        //SimpleDateFormat("EEEE").format(dailyWeatherList[position].dt * 1000)
        holder.weatherDescriptionTextView.text = dailyWeatherList[position].weather[0].description
        holder.tempTextView.text =
            "${dailyWeatherList[position].temp.min.toInt()}/${dailyWeatherList[position].temp.max.toInt()}"
        holder.tempUnitTextView.text = "°C"

        val iconUrl =
            "https://openweathermap.org/img/wn/${dailyWeatherList[position].weather[0].icon}@2x.png"
        Log.i("TAG", "setWeather: iconUrl   $iconUrl")
        Glide.with(context).load(iconUrl)
            .apply(
                RequestOptions().override(100, 60)
            )
            .into(holder.weatherImage)
    }

    override fun getItemCount(): Int {
        Log.e("TAG", "List Size : ${dailyWeatherList.size}")
        return dailyWeatherList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayTextView: TextView = itemView.findViewById(R.id.day_textView)
        var weatherDescriptionTextView: TextView = itemView.findViewById(R.id.weather_desc_textView)
        var tempTextView: TextView = itemView.findViewById(R.id.temp_textView)
        var tempUnitTextView: TextView = itemView.findViewById(R.id.temp_unit_textView)
        var weatherImage: ImageView = itemView.findViewById(R.id.weather_condition_image)
    }

}
