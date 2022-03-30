package com.example.weather.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.pojo.DayWeatherRecycler

class MyDayRecyclerAdapter : RecyclerView.Adapter<MyDayRecyclerAdapter.ViewHolder>() {
    var list: MutableList<DayWeatherRecycler> = mutableListOf()

    init {
        var dayWeather = DayWeatherRecycler("Today", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Tomorrow", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Saturday", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Sunday", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Monday", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Tuesday", 0, "Clear Sky", "12/13", "°C")
        list.add(dayWeather)
        dayWeather = DayWeatherRecycler("Wednesday", 0, "1Clear Sky5", "12/13", "°C")
        list.add(dayWeather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.day_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayTextView.text = list[position].day
        holder.weatherDescriptionTextView.text = list[position].weatherDescription
        holder.tempTextView.text = list[position].temp
        holder.tempUnitTextView.text = list[position].tempUnit
    }

    override fun getItemCount(): Int {
        Log.e("TAG", "List Size : ${list.size}")
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayTextView: TextView = itemView.findViewById(R.id.day_textView)
        var weatherDescriptionTextView: TextView = itemView.findViewById(R.id.weather_desc_textView)
        var tempTextView: TextView = itemView.findViewById(R.id.temp_textView)
        var tempUnitTextView: TextView = itemView.findViewById(R.id.temp_unit_textView)
        var weatherImage: ImageView = itemView.findViewById(R.id.weather_condition_image)
    }

}
