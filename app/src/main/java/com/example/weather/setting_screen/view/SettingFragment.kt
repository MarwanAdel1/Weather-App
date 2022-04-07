package com.example.weather.setting_screen.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.FragmentsCommunicator
import com.example.weather.R
import com.example.weather.pojo.SettingData
import com.example.weather.setting_screen.viewmodel.SettingViewModel
import com.example.weather.setting_screen.viewmodel.SettingViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class SettingFragment(
    private var myContext: Context,
    private var communicator: FragmentsCommunicator
) : Fragment() {
    private lateinit var tempRadioGroup: RadioGroup
    private lateinit var windSpeedRadioGroup: RadioGroup
    private lateinit var locationRadioGroup: RadioGroup
    private lateinit var notificationRadioGroup: RadioGroup
    private lateinit var languageRadioGroup: RadioGroup
    private lateinit var doneBt: FloatingActionButton

    private var tempValue: Int = 0
    private var locationValue: Int = 0
    private var languageValue: Int = 0
    private var notificationValue: Int = 0
    private var windSpeedValue: Int = 0


    private lateinit var settingViewModel: SettingViewModel
    private lateinit var settingViewModelFactory: SettingViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tempRadioGroup = view.findViewById(R.id.temp_radio_group)
        windSpeedRadioGroup = view.findViewById(R.id.wind_radio_group)
        locationRadioGroup = view.findViewById(R.id.location_radio_group)
        notificationRadioGroup = view.findViewById(R.id.notification_radio_group)
        languageRadioGroup = view.findViewById(R.id.language_radio_group)
        doneBt = view.findViewById(R.id.done_setting_bt)


        settingViewModelFactory = SettingViewModelFactory(
            myContext
        )

        settingViewModel =
            ViewModelProvider(this, settingViewModelFactory).get(SettingViewModel::class.java)

        settingViewModel.getSetting()

        settingViewModel.settingLiveData.observe(viewLifecycleOwner) {
            (tempRadioGroup.getChildAt(it.tempValue) as RadioButton).isChecked = true
            (windSpeedRadioGroup.getChildAt(it.windValue) as RadioButton).isChecked = true
            (locationRadioGroup.getChildAt(it.location) as RadioButton).isChecked = true
            (notificationRadioGroup.getChildAt(it.notification) as RadioButton).isChecked = true
            (languageRadioGroup.getChildAt(it.language) as RadioButton).isChecked = true
        }


        doneBt.setOnClickListener {
            tempValue = getTempRadioGroupValue(tempRadioGroup.checkedRadioButtonId)
            locationValue = getLocationRadioGroupValue(locationRadioGroup.checkedRadioButtonId)
            languageValue = getLanguageRadioGroupValue(languageRadioGroup.checkedRadioButtonId)
            notificationValue =
                getNotificationRadioGroupValue(notificationRadioGroup.checkedRadioButtonId)
            windSpeedValue = getWindSpeedRadioGroupValue(windSpeedRadioGroup.checkedRadioButtonId)


            var data: SettingData = SettingData(
                tempValue,
                windSpeedValue,
                locationValue,
                notificationValue,
                languageValue
            )

            settingViewModel.saveSetting(data)


            languageSettings()
            communicator.settingSavedAndGoToHomeScreen()
        }


    }

    fun getTempRadioGroupValue(id: Int): Int {
        var temp = when (id) {
            R.id.celsius_radio_button -> 0
            R.id.kelvin_radio_button -> 1
            R.id.fahrenheit_radio_button -> 2
            else -> 0
        }

        return temp
    }

    fun getLocationRadioGroupValue(id: Int): Int {
        var location = when (id) {
            R.id.gps_radio_button -> 0
            R.id.map_radio_button -> 1
            else -> 0
        }

        return location
    }

    fun getLanguageRadioGroupValue(id: Int): Int {
        var language = when (id) {
            R.id.english_radio_button -> 0
            R.id.arabic_radio_button -> 1
            else -> 0
        }

        return language
    }

    fun getNotificationRadioGroupValue(id: Int): Int {
        var notification = when (id) {
            R.id.enable_notification_radio_button -> 0
            R.id.disable_notification_radio_button -> 1
            else -> 0
        }

        return notification
    }

    fun getWindSpeedRadioGroupValue(id: Int): Int {
        var wind = when (id) {
            R.id.meter_per_second_radio_button -> 0
            R.id.mile_per_hour_radio_button -> 1
            else -> 0
        }

        return wind
    }


    fun languageSettings() {
        val config = resources.configuration
        val lang = when (languageValue) {
            0 -> "en"
            1 -> "ar"
            else -> "en"
        }


        var locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            myContext.createConfigurationContext(config)
        }

        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            requireActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            requireActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        Toast.makeText(myContext, "Saved", Toast.LENGTH_SHORT).show()
    }
}