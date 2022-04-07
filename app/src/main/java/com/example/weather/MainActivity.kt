package com.example.weather

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.weather.favourite_screen.view.FavouriteFragment
import com.example.weather.home_screen.view.HomeFragment
import com.example.weather.setting_screen.view.SettingFragment
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), FragmentsCommunicator {
    private lateinit var openNavigation: ImageView
    private lateinit var title: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    var fragmentManager: FragmentManager? = null

    var homeFragment: HomeFragment? = null
    var settingFragment: SettingFragment? = null
    var favouriteFragment: FavouriteFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

        title.text = "Home"

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


    }

    fun initViews() {
        openNavigation = findViewById(R.id.open_navigation_drawer)
        title = findViewById(R.id.title)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
    }

    fun setNavigationDrawerListener() {
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
                    }
                    drawerLayout.close()
                }
                R.id.menu_item2 -> {
                    favouriteFragment =
                        fragmentManager!!.findFragmentByTag("fav") as FavouriteFragment?
                    if (favouriteFragment == null) {
                        favouriteFragment = FavouriteFragment()
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, favouriteFragment!!, "fav")
                            .commit()

                        title.text = getString(R.string.favourite)
                    }
                    drawerLayout.close()
                }
                R.id.menu_item3 -> {
//                    val intent = Intent(this, SecondActivity::class.java)
//                    startActivity(intent)
                }
                R.id.menu_item4 -> {
                    settingFragment =
                        fragmentManager!!.findFragmentByTag("setting") as SettingFragment?
                    if (settingFragment == null) {
                        settingFragment = SettingFragment(this,this)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, settingFragment!!, "setting")
                            .commit()

                        title.text = getString(R.string.setting_text)
                    }

                    drawerLayout.close()
                }
            }

            true
        }
    }

    fun checkLocationPermission() {
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
        } else if (requestCode == PERMISSION_REQUEST_LOCATION_SETTING) {

        }
    }


    companion object {
        private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
        private val PERMISSION_REQUEST_LOCATION_SETTING = 200
    }

    override fun settingSavedAndGoToHomeScreen() {
        Toast.makeText(this, "Setting Saved", Toast.LENGTH_SHORT).show()
        homeFragment = fragmentManager!!.findFragmentByTag("home") as HomeFragment?
        if (homeFragment == null) {
            homeFragment = HomeFragment(this)
            fragmentManager!!.beginTransaction()
                .replace(R.id.fragment_container, homeFragment!!, "home")
                .commit()

            title.text = getString(R.string.home_textview)
        }
    }
}