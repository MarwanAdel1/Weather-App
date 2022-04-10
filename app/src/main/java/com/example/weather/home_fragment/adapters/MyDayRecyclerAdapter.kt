package com.example.weather.home_fragment.adapters

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
import com.example.weather.pojo.SettingData
import java.text.SimpleDateFormat
import java.util.*

class MyDayRecyclerAdapter(private var context: Context) :
    RecyclerView.Adapter<MyDayRecyclerAdapter.ViewHolder>() {
    private var dailyWeatherList: List<Daily> = ArrayList()
    private var settingData: SettingData? = null


    fun setDailyWeatherList(dailyWeatherList: List<Daily>){
        this.dailyWeatherList = dailyWeatherList
    }

    fun setSetting(setting: SettingData) {
        settingData = setting
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.day_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        SimpleDateFormat("dd/MM/yyyy h aa").format(dailyWeatherList[position].dt*1000)
        val date = Date(dailyWeatherList[position].dt * 1000)
        val str: String
        when {
            DateUtils.isToday(date.time) -> {
                str = context.getString(R.string.today)
            }
            DateUtils.isToday(date.time - DateUtils.DAY_IN_MILLIS) -> {
                str = context.getString(R.string.tomorrow)
            }
            else -> {
                val lang = when (settingData!!.language) {
                    0 -> "en"
                    1 -> "ar"
                    else -> "en"
                }
                val locale = Locale(lang)
                str = SimpleDateFormat("EEEE", locale).format(dailyWeatherList[position].dt * 1000)
            }
        }
        holder.dayTextView.text = str
        //SimpleDateFormat("EEEE").format(dailyWeatherList[position].dt * 1000)
        holder.weatherDescriptionTextView.text = dailyWeatherList[position].weather[0].description
        holder.tempTextView.text = when (settingData!!.tempValue) {
            0 -> String.format(
                "%d",
                dailyWeatherList[position].temp.min.toInt()
            ) + "/" + String.format("%d", dailyWeatherList[position].temp.max.toInt())
            1 -> String.format(
                "%d",
                (dailyWeatherList[position].temp.min + 273.15).toInt()
            ) + "/" + String.format("%d", (dailyWeatherList[position].temp.max + 273.15).toInt())
            2 -> String.format(
                "%d",
                ((dailyWeatherList[position].temp.min * 1.8) + 32).toInt()
            ) + "/" + String.format(
                "%d",
                ((dailyWeatherList[position].temp.max * 1.8) + 32).toInt()
            )
            else -> String.format(
                "%d",
                dailyWeatherList[position].temp.min.toInt()
            ) + "/" + String.format("%d", dailyWeatherList[position].temp.max.toInt())
        }

        holder.tempUnitTextView.text = when (settingData!!.tempValue) {
            0 -> context.getString(R.string.celsius)
            1 -> context.getString(R.string.kelvin)
            2 -> context.getString(R.string.fahrenheit)
            else -> context.getString(R.string.celsius)
        }

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
