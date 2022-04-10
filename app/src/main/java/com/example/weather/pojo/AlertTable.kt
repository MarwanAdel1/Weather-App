package com.example.weather.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "alert", primaryKeys = ["date", "time"])
data class AlertTable(
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "time")
    var time: String
)
