package com.example.weather.fav_maps_screen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.data.ApiKeys
import com.example.weather.data.room_database.LocalSource
import com.example.weather.databinding.ActivityFavMapsBinding
import com.example.weather.fav_maps_screen.viewmodel.FavMapsViewModel
import com.example.weather.fav_maps_screen.viewmodel.FavMapsViewModelFactory
import com.example.weather.home_maps_screen.view.HomeMapsActivity
import com.example.weather.model.WeatherRepo
import com.example.weather.network.RemoteSource
import com.example.weather.pojo.FavouriteCityTable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class FavMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private lateinit var binding: ActivityFavMapsBinding

    private var cameraPosition: CameraPosition? = null

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var trueLocationFlag = false


    private lateinit var setLocation: Button
    private lateinit var progressBar: ProgressBar
    private var toast: Toast? = null
    private var cityName: String? = null

    private lateinit var favMapsViewModel: FavMapsViewModel
    private lateinit var favMapsViewModelFactory: FavMapsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        binding = ActivityFavMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        progressBar = binding.progressIndicator
        setLocation = binding.addToFavBt


        favMapsViewModelFactory = FavMapsViewModelFactory(
            WeatherRepo.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(this),
                this
            ),
            this
        )

        favMapsViewModel =
            ViewModelProvider(this, favMapsViewModelFactory)[FavMapsViewModel::class.java]
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        progressBar.visibility = View.INVISIBLE
        setLocation.isEnabled = false

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        mMap?.setOnMapClickListener {
            marker?.position = it

            ///appear progress bar
            progressBar.visibility = View.VISIBLE
            setLocation.isEnabled = false

            favMapsViewModel.reverseGeocoding(
                it.latitude.toString(),
                it.longitude.toString(),
                ApiKeys.HERE_API_KEY
            )


            favMapsViewModel.cityNameLiveData.observe(this) { city ->
                if (city != null) {
                    if (toast != null) {
                        toast!!.cancel()
                    }

                    if (city.contentEquals("null")) {
                        trueLocationFlag = false

                        toast = Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT)
                        toast?.show()

                        progressBar.visibility = View.INVISIBLE
                        setLocation.isEnabled = false
                    } else {
                        trueLocationFlag = true

                        cityName = city

                        progressBar.visibility = View.INVISIBLE
                        setLocation.isEnabled = true
                    }
                } else {
                    setLocation.isEnabled = false
                    progressBar.visibility = View.INVISIBLE
                    toast = Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT)
                }
            }
            //set button -> save location in database and finish
        }

        setLocation.setOnClickListener {
            if (trueLocationFlag) {
                val favCityData: FavouriteCityTable = FavouriteCityTable(
                    cityName!!,
                    marker!!.position.latitude.toString(),
                    marker!!.position.longitude.toString()
                )

                favMapsViewModel.insertFavouriteCityToDatabase(favCityData)

                finish()
            } else {
                if (toast != null) {
                    toast!!.cancel()
                }

                toast = Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT)
                toast?.show()
            }
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            marker = mMap?.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    )
                                ).draggable(true)
                            )
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(
                            HomeMapsActivity.javaClass.name,
                            "Current location is null. Using defaults."
                        )
                        Log.e(HomeMapsActivity.javaClass.name, "Exception: %s", task.exception)
                        marker = mMap?.addMarker(
                            MarkerOptions().position(defaultLocation).draggable(true)
                        )
                        mMap?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap?.isMyLocationEnabled = false
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

    }

}