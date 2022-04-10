package com.example.weather.favourite_fragment.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.main_activity.view.FragmentsCommunicator
import com.example.weather.R
import com.example.weather.data.room_database.LocalSource
import com.example.weather.fav_maps_screen.view.FavMapsActivity
import com.example.weather.favourite_fragment.adapters.FavouriteAdapter
import com.example.weather.favourite_fragment.viewmodel.FavouriteViewModel
import com.example.weather.favourite_fragment.viewmodel.FavouriteViewModelFactory
import com.example.weather.model.WeatherRepo
import com.example.weather.network.RemoteSource
import com.example.weather.pojo.FavouriteCityTable
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FavouriteFragment(
    private var myContext: Context,
    private var communicator: FragmentsCommunicator
) : Fragment(), FavouriteCommunicator {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noFavImage: ImageView
    private lateinit var addFavCity: FloatingActionButton

    private lateinit var favouriteAdapter: FavouriteAdapter

    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        noFavImage = view.findViewById(R.id.no_fav)
        recyclerView = view.findViewById(R.id.recyclerView)
        addFavCity = view.findViewById(R.id.add_fav_bt)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        favouriteAdapter = FavouriteAdapter(this)
        recyclerView.adapter = favouriteAdapter


        favouriteViewModelFactory = FavouriteViewModelFactory(
            WeatherRepo.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(myContext),
                myContext
            ),
            myContext
        )

        favouriteViewModel =
            ViewModelProvider(this, favouriteViewModelFactory)[FavouriteViewModel::class.java]

        favouriteViewModel.getFavCityFromDatabase()
        favouriteViewModel.favCitiesLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                favouriteAdapter.setCitiesList(it)
                favouriteAdapter.notifyDataSetChanged()
            }
        }

        addFavCity.setOnClickListener {
            val intent: Intent = Intent(myContext, FavMapsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onClickDeleteCityFromFavouriteCities(city: FavouriteCityTable) {
        favouriteViewModel.deleteFavCityFromDatabase(city)
    }

    override fun onClickCityView(city: FavouriteCityTable) {
        val latLng: LatLng = LatLng(city.latitude.toDouble(), city.longitude.toDouble())
        favouriteViewModel.saveLocation(latLng)
        communicator.goToHomeFragment()
    }
}