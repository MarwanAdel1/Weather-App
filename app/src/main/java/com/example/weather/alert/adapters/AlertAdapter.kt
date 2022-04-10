package com.example.weather.alert.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.alert.view.AlertCommunicator
import com.example.weather.pojo.AlertTable

class AlertAdapter(private var communicator: AlertCommunicator) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    private var alerts: List<AlertTable> = ArrayList()

    fun setalertsList(data: List<AlertTable>) {
        this.alerts = data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.alert_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = alerts[position].time
        holder.date.text = alerts[position].date
        holder.delete.setOnClickListener {
            communicator.deleteAlert(alerts[position])
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView = itemView.findViewById(R.id.time_alert)
        var delete: ImageView = itemView.findViewById(R.id.delete_alert)
        var date: TextView = itemView.findViewById(R.id.date_alert)
    }
}