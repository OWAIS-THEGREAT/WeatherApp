package com.example.weatherapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.DAO.WeatherDAO
import com.example.weatherapp.Modals.LocalDataWeather


@Database(entities = [LocalDataWeather::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getDao():WeatherDAO

    companion object{
        private var instanse : WeatherDatabase? = null

        fun getDatabase(context : Context) : WeatherDatabase{
            if(instanse==null){
                instanse = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    "Weatherdata"
                ).allowMainThreadQueries().build()
            }

            return instanse!!
        }
    }
}