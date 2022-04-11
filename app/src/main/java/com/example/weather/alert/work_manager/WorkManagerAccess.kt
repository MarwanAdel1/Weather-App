package com.example.weather.alert.work_manager

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weather.pojo.AlertTable
import java.util.*
import java.util.concurrent.TimeUnit

class WorkManagerAccess private constructor(private var myContext: Context) {

    private var workRequests: MutableList<WorkRequest>? = null

    init {
        workRequests = ArrayList()
    }

    companion object {
        private var workManagerAccess: WorkManagerAccess? = null

        fun getInstance(context: Context): WorkManagerAccess {
            if (workManagerAccess == null) {
                workManagerAccess = WorkManagerAccess(context)
            }
            return workManagerAccess as WorkManagerAccess
        }
    }

    fun cancelAllWork() {
        WorkManager.getInstance(myContext).cancelAllWork()
    }

    fun setWorkManager(lists: List<AlertTable>) {
        workRequests!!.clear()
        makeRequests(lists)
        WorkManager.getInstance(myContext).cancelAllWork()
        if (workRequests!!.size > 0) {
            WorkManager.getInstance(myContext).enqueue(workRequests!!)
        }
    }


    fun makeRequests(lists: List<AlertTable>) {
        for (i in lists.indices) {
            makeRequestsReady(lists[i])
        }
    }

    fun makeRequestsReady(alert: AlertTable) {
        val duration: Long = calcDiffInSec(alert)
        if (duration > 0) {
            Log.e("Work","Duration :$duration")
            val mywork = OneTimeWorkRequest.Builder(Work::class.java)
                .setInitialDelay(
                    duration.toLong(),
                    TimeUnit.SECONDS
                ) // Use this when you want to add initial delay or schedule initial work to `OneTimeWorkRequest` e.g. setInitialDelay(2, TimeUnit.HOURS)
                .build()
            workRequests!!.add(mywork)
        }
    }


    fun calcDiffInSec(alert: AlertTable): Long {
        var date = alert.date
        var dateSplit = date.split("-")
        var time = alert.time
        var timeSplit = time.split(":")

        var calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.YEAR, dateSplit[2].toInt())
        calendar1.set(Calendar.MONTH, dateSplit[1].toInt() - 1)
        calendar1.set(Calendar.DAY_OF_MONTH, dateSplit[0].toInt())
        calendar1.set(Calendar.MINUTE, timeSplit[1].toInt())
        calendar1.set(Calendar.HOUR_OF_DAY, timeSplit[0].toInt())
        calendar1.set(Calendar.SECOND, 0)

        var calendar2 = Calendar.getInstance()


        val diff: Long = calendar1.timeInMillis - calendar2.timeInMillis
        val seconds = diff / 1000
//        val minutes = seconds / 60
//        val hours = minutes / 60
//        val days = hours / 24

        return seconds
    }
}