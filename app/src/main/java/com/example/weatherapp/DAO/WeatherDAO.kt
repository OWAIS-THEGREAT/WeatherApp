package com.example.weatherapp.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.Modals.LocalDataWeather


@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertweather(data : LocalDataWeather)

    @Query("SELECT * FROM weather")
    fun getdata() : List<LocalDataWeather>
}