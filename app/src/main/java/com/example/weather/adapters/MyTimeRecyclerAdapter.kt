package com.example.weather.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.pojo.TimeWeatherRecycler

class MyTimeRecyclerAdapter() : RecyclerView.Adapter<MyTimeRecyclerAdapter.ViewHolder>() {
    var list: MutableList<TimeWeatherRecycler> = mutableListOf()

    init {
        var timeWeather = TimeWeatherRecycler("1 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("2 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("3 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("4 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("5 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("6 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("7 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("8 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("9 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("10 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("11 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("12 AM",0,"15","°C")
        list.add(timeWeather)
        timeWeather = TimeWeatherRecycler("1 PM",0,"15","°C")
        list.add(timeWeather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.time_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timeTextView.text=list[position].time
        holder.tempTextView.text=list[position].temp
        holder.tempUnitTextView.text=list[position].tempUnit
    }

    override fun getItemCount(): Int {
        Log.e("TAG","List Size : ${list.size}")
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeTextView: TextView = itemView.findViewById(R.id.time_textView)
        var tempTextView: TextView = itemView.findViewById(R.id.temp_textView)
        var tempUnitTextView: TextView = itemView.findViewById(R.id.temp_unit_textView)
        var weatherImage: ImageView = itemView.findViewById(R.id.weather_condition_image)
    }

}
