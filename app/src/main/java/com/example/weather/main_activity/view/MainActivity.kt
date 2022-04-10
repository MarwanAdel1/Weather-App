package com.example.weather.main_activity.view

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.alert.view.AlertFragment
import com.example.weather.favourite_fragment.view.FavouriteFragment
import com.example.weather.home_fragment.view.HomeFragment
import com.example.weather.home_maps_screen.view.HomeMapsActivity
import com.example.weather.main_activity.viewmodel.MainActivityViewModel
import com.example.weather.main_activity.viewmodel.MainActivityViewModelFactory
import com.example.weather.setting_fragment.view.SettingFragment
import com.google.android.material.navigation.NavigationView
import java.util.*


class MainActivity : AppCompatActivity(), FragmentsCommunicator {
    private lateinit var openNavigation: ImageView
    private lateinit var mapImage: ImageView
    private lateinit var title: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private var fragmentManager: FragmentManager? = null

    private var homeFragment: HomeFragment? = null
    private var settingFragment: SettingFragment? = null
    private var favouriteFragment: FavouriteFragment? = null
    private var alertFragment: AlertFragment? = null

    private var fragmentNumber: Int = 1


    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityViewModelFactory = MainActivityViewModelFactory(
            this
        )

        mainActivityViewModel =
            ViewModelProvider(
                this,
                mainActivityViewModelFactory
            )[MainActivityViewModel::class.java]

        val setting = mainActivityViewModel.getSetting()
        val lang = when (setting.language) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }
        changeLanguage(lang)

        setContentView(R.layout.activity_main)


        initViews()
        checkLocationPermission()

        fragmentManager = supportFragmentManager
        homeFragment = fragmentManager!!.findFragmentByTag("home") as HomeFragment?
        if (homeFragment == null) {
            homeFragment = HomeFragment(this)
            fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, homeFragment!!, "home")
                .commit()

            fragmentNumber = 1
        }

        title.text = "Home"
        mapImage.visibility = View.VISIBLE

        openNavigation.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.close()
            } else {
                drawerLayout.open()
            }
        }

        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.home_textview)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setNavigationDrawerListener()


        mapImage.setOnClickListener {
            val intent: Intent = Intent(this, HomeMapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (fragmentNumber == 1) {
            homeFragment = HomeFragment(this)
            fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, homeFragment!!, "home")
                .commit()

            title.text = getString(R.string.home_textview)
            mapImage.visibility = View.VISIBLE
        } else if (fragmentNumber == 2) {
            favouriteFragment = FavouriteFragment(this, this)
            fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, favouriteFragment!!, "fav")
                .commit()

            title.text = getString(R.string.favourite)
            mapImage.visibility = View.INVISIBLE
        }
    }

    private fun initViews() {
        openNavigation = findViewById(R.id.open_navigation_drawer)
        title = findViewById(R.id.title)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        mapImage = findViewById(R.id.map_icon)
    }

    private fun setNavigationDrawerListener() {
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_item1 -> {
                    homeFragment = fragmentManager!!.findFragmentByTag("home") as HomeFragment?
                    if (homeFragment == null) {
                        homeFragment = HomeFragment(this)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, homeFragment!!, "home")
                            .commit()

                        title.text = getString(R.string.home_textview)
                        mapImage.visibility = View.VISIBLE
                    }
                    fragmentNumber = 1
                    drawerLayout.close()
                }
                R.id.menu_item2 -> {
                    favouriteFragment =
                        fragmentManager!!.findFragmentByTag("fav") as FavouriteFragment?
                    if (favouriteFragment == null) {
                        favouriteFragment = FavouriteFragment(this, this)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, favouriteFragment!!, "fav")
                            .commit()

                        title.text = getString(R.string.favourite)
                        mapImage.visibility = View.INVISIBLE
                    }
                    fragmentNumber = 2
                    drawerLayout.close()
                }
                R.id.menu_item3 -> {
                    alertFragment =
                        fragmentManager!!.findFragmentByTag("alert") as AlertFragment?
                    if (alertFragment == null) {
                        alertFragment = AlertFragment(this)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, alertFragment!!, "alert")
                            .commit()

                        title.text = getString(R.string.alert)
                        mapImage.visibility = View.INVISIBLE
                    }
                    fragmentNumber = 3
                    drawerLayout.close()
                }
                R.id.menu_item4 -> {
                    settingFragment =
                        fragmentManager!!.findFragmentByTag("setting") as SettingFragment?
                    if (settingFragment == null) {
                        settingFragment = SettingFragment(this, this)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, settingFragment!!, "setting")
                            .commit()

                        title.text = getString(R.string.setting_text)
                        mapImage.visibility = View.INVISIBLE
                    }
                    fragmentNumber = 4
                    drawerLayout.close()
                }
            }

            true
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> {}
                PackageManager.PERMISSION_DENIED -> checkLocationPermission()
            }
        }
    }


    private fun changeLanguage(language: String) {
        val config = resources.configuration

        val locale = Locale(language)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        createConfigurationContext(config)

        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
        private const val PERMISSION_REQUEST_LOCATION_SETTING = 200
    }

    override fun goToHomeFragment() {
        homeFragment = fragmentManager!!.findFragmentByTag("home") as HomeFragment?
        if (homeFragment == null) {
            homeFragment = HomeFragment(this)
            fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, homeFragment!!, "home")
                .commit()

            title.text = getString(R.string.home_textview)
            mapImage.visibility = View.VISIBLE
        }
    }

    override fun restartApp() {
        val packetManager: PackageManager = packageManager
        val intent: Intent = packetManager.getLaunchIntentForPackage(packageName)!!
        val componentName: ComponentName = intent.component!!

        val mainIntent: Intent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)

        Runtime.getRuntime().exit(0)
    }
}