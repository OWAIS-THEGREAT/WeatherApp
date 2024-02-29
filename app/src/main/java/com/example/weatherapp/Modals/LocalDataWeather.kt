package com.example.weatherapp.Modals

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class LocalDataWeather(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val city : String,
    val image : String,
    val weather : String,
    val temperature : Double,
    val humidity : Int,
    val windSpeed : Double,
    val date : String
)
