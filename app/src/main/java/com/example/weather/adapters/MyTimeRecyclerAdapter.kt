package com.example.weather.adapters

import android.content.Context
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
import com.example.weather.pojo.Hourly
import java.text.SimpleDateFormat

class MyTimeRecyclerAdapter(private var context: Context) : RecyclerView.Adapter<MyTimeRecyclerAdapter.ViewHolder>() {
    private var hourlyWeatherList: List<Hourly> = ArrayList()

    fun setHourlyWeatherList(hourlyWeatherList: List<Hourly>) {
        this.hourlyWeatherList = hourlyWeatherList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.time_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timeTextView.text= SimpleDateFormat("h aa").format(hourlyWeatherList[position].dt*1000)
        holder.tempTextView.text=hourlyWeatherList[position].temp.toInt().toString()
        holder.tempUnitTextView.text="°C"

        val iconUrl =
            "https://openweathermap.org/img/wn/${hourlyWeatherList[position].weather[0].icon}@2x.png"
        Log.i("TAG", "setWeather: iconUrl   $iconUrl")
        Glide.with(context).load(iconUrl)
            .apply(
                RequestOptions().override(20, 20)
            )
            .into(holder.weatherImage)
    }

    override fun getItemCount(): Int {
        Log.e("TAG","List Size : ${hourlyWeatherList.size}")
        return hourlyWeatherList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeTextView: TextView = itemView.findViewById(R.id.time_textView)
        var tempTextView: TextView = itemView.findViewById(R.id.temp_textView)
        var tempUnitTextView: TextView = itemView.findViewById(R.id.temp_unit_textView)
        var weatherImage: ImageView = itemView.findViewById(R.id.weather_condition_image)
    }

}
