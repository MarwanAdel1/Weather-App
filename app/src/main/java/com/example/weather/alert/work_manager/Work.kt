package com.example.weather.alert.work_manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.data.ApiKeys
import com.example.weather.data.room_database.LocalSource
import com.example.weather.main_activity.view.MainActivity
import com.example.weather.model.WeatherRepo
import com.example.weather.network.RemoteSource
import com.example.weather.setting_fragment.viewmodel.SettingViewModel
import java.util.*


class Work(var context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    var weatherRepo: WeatherRepo = WeatherRepo.getInstance(
        RemoteSource.getInstance(),
        LocalSource.getInstance(context),
        context
    )

    var sharedPreferences =
        context.getSharedPreferences(SettingViewModel.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    val lat: String = sharedPreferences.getString("lat", "0").toString()
    val lng: String = sharedPreferences.getString("lng", "0").toString()
    val language = sharedPreferences.getInt(SettingViewModel.LANGUAGE_NAME, 0)
    var lang = when (language) {
        0 -> "en"
        1 -> "ar"
        else -> "en"
    }

    override suspend fun doWork(): Result {
        var response = weatherRepo.getAlertDataFromApi(lat, lng, lang, ApiKeys.WEATHER_API_KEY)

        if (response?.alerts == null) {
            showNotification("Hurray!! It's a good News", "The weather is good")
        } else {


            var calendar = Calendar.getInstance()

            val startDate = response!!.alerts!![0].start.toLong()
            val endDate = response!!.alerts!![0].end.toLong()

            if (checkDateIsInBetween(startDate, endDate)) {
                showNotification(
                    "Opps!! Bad News",
                    response!!.alerts!![0].event
                )
            } else {
                showNotification("Hurray!! It's a good News", "The weather is good")
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, desc: String) {
        val notifyIntent = Intent(applicationContext, MainActivity::class.java)
        notifyIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // Create the PendingIntent
        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val name: CharSequence = "channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = desc
            channel.description = desc
            channel.setSound(
                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/weathersound"),
                audioAttributes
            )
            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.cloud)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true).build()
        val managerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        managerCompat.notify(NOTIFY_ID, notification)
    }

    fun checkDateIsInBetween(start: Long, end: Long): Boolean {
        var flag = false

        var calendar = Calendar.getInstance()
        var timeInSec = calendar.timeInMillis / 1000

        if (timeInSec in start..end) {
            flag = true
        }

        return flag
    }

    companion object {
        private val CHANNEL_ID = "CH55"
        private val NOTIFY_ID = 1
    }
}