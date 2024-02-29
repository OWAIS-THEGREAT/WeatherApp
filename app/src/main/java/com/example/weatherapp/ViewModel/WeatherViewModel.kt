package com.example.weatherapp.ViewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Modals.LocalDataWeather
import com.example.weatherapp.Modals.weatherdata
import com.example.weatherapp.Repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val context: Context,private val weatherRepository: WeatherRepository,val latitude : Double,val longitude : Double , val apikey : String): ViewModel() {

    init {

        viewModelScope.launch(Dispatchers.IO) {
            if(isInternetAvailable(context)) {
                weatherRepository.getWeatherData(latitude, longitude, apikey)
            }
        }

    }


    val weather : LiveData<weatherdata>
        get() = weatherRepository.weather

    val localweather : LocalDataWeather
        get() = weatherRepository.getdata()


    suspend fun insert(data : LocalDataWeather){
        weatherRepository.insert(data)
    }

    suspend fun getotherweathers(q : String) : weatherdata?{
        return weatherRepository.getotherweathers(q,apikey)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

}