package com.example.weatherapp.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Repository.WeatherRepository

class ViewModelFactory(private val context: Context,private val weatherRepository: WeatherRepository,
                       val latitude : Double,
                       val longitude : Double,
                       val apikey : String,
                       ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(context,weatherRepository,latitude, longitude, apikey) as T
    }
}