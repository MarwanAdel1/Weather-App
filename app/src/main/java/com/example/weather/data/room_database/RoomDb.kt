package com.example.weather.data.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather.pojo.CityWeatherTable
import com.example.weather.pojo.FavouriteCityTable

@Database(entities = [FavouriteCityTable::class,CityWeatherTable::class], version = 1)
abstract class RoomDb : RoomDatabase() {
    abstract fun weatherDAO(): WeatherDAO

    companion object {
        private var INSTANCE: RoomDb? = null
        //one thread at a time to access this method
        @Synchronized
        fun getInstance(context: Context): RoomDb {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                RoomDb::class.java,
                "WeatherAppData"
            ).fallbackToDestructiveMigration().build()
        }
    }
}