package com.example.weather.alert.view

import com.example.weather.pojo.AlertTable

interface AlertCommunicator {
    fun deleteAlert(alert: AlertTable)
}