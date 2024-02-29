package com.example.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Modals.weatherdata
import com.example.weatherapp.R

class WeatherAdapter(private val context: Context,private val weatherlist : List<weatherdata>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val cityname = itemView.findViewById<TextView>(R.id.cityname)
        val temp = itemView.findViewById<TextView>(R.id.temperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.itemview,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return weatherlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityname.text = weatherlist[position].name
        holder.temp.text = String.format("%.2f", weatherlist[position].main.temp - 273)  + "°C"
    }
}