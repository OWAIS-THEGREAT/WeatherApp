package com.example.weatherapp.Utils

import com.example.weatherapp.Modals.weatherdata
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterFace {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<weatherdata>

    @GET("data/2.5/weather")
    suspend fun getotherweather(
        @Query("q") city : String,
        @Query("appid") apiKey: String
    ) : Response<weatherdata>

}