package com.example.weather.favourite_fragment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.favourite_fragment.view.FavouriteCommunicator
import com.example.weather.pojo.FavouriteCityTable

class FavouriteAdapter(private var communicator: FavouriteCommunicator) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    private var cities: List<FavouriteCityTable> = ArrayList()

    fun setCitiesList(data: List<FavouriteCityTable>) {
        this.cities = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.fav_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityTx.text = cities[position].cityName
        holder.deleteCity.setOnClickListener {
            communicator.onClickDeleteCityFromFavouriteCities(cities[position])
        }
        holder.cityView.setOnClickListener {
            communicator.onClickCityView(cities[position])
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityTx: TextView = itemView.findViewById(R.id.fav_city_name)
        var deleteCity: ImageView = itemView.findViewById(R.id.delete_fav_city)
        var cityView: ConstraintLayout = itemView.findViewById(R.id.view)
    }
}