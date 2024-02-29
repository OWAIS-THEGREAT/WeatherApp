package com.example.weatherapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Database.WeatherDatabase
import com.example.weatherapp.Modals.LocalDataWeather
import com.example.weatherapp.Modals.weatherdata
import com.example.weatherapp.Utils.ApiInterFace

class WeatherRepository(private val apiInterFace: ApiInterFace,private val weatherDatabase: WeatherDatabase) {

    private val weatherLivedata  = MutableLiveData<weatherdata>()

    private val otherLiveData = MutableLiveData<List<weatherdata>>()

    val weather : LiveData<weatherdata>
        get() = weatherLivedata


    suspend fun getWeatherData(latitude : Double,longitude : Double , apikey : String){
        val result = apiInterFace.getWeather(latitude,longitude,apikey)
        if(result.body()!=null){
            weatherLivedata.postValue(result.body())
        }
    }

    suspend fun getotherweathers(q : String,apikey: String) : weatherdata?{
        val result = apiInterFace.getotherweather(q, apikey)

        if (result.isSuccessful) {
            return result.body()!!
        }
        return null
    }

    suspend fun insert(localdata : LocalDataWeather){
        weatherDatabase.getDao().insertweather(localdata)
    }

    fun getdata() : LocalDataWeather{
        return weatherDatabase.getDao().getdata().last()
    }
}