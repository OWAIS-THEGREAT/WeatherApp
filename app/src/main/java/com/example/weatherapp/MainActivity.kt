package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weatherapp.Database.WeatherDatabase
import com.example.weatherapp.Modals.LocalDataWeather
import com.example.weatherapp.Modals.weatherdata
import com.example.weatherapp.Repository.WeatherRepository
import com.example.weatherapp.Utils.ApiInterFace
import com.example.weatherapp.Utils.RetrofitObject
import com.example.weatherapp.ViewModel.ViewModelFactory
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.adapter.WeatherAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var weatherviewmodel : WeatherViewModel

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var name : TextView
    private lateinit var image : ImageView
    private lateinit var weather : TextView
    private lateinit var date : TextView
    private lateinit var temperature : TextView
    private lateinit var humidity : TextView
    private lateinit var windspeed : TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var citylist : MutableList<String>

    private lateinit var weatherlist : MutableList<weatherdata>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = findViewById(R.id.CityName)
        image = findViewById(R.id.image)
        weather = findViewById(R.id.weather)
        date = findViewById(R.id.dateTime)
        temperature = findViewById(R.id.temperature)
        humidity = findViewById(R.id.humidityText)
        windspeed = findViewById(R.id.WindText)
        recyclerView = findViewById(R.id.recycler)

        citylist = mutableListOf()
        weatherlist = mutableListOf()

        citylist = mutableListOf("New York", "Singapore", "Mumbai", "Delhi", "Sydney", "Melbourne")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationpermission()

    }

    private fun checkLocationpermission() {

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }

        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener {
            if(it!=null){
                val apiInterface = RetrofitObject.instance.create(ApiInterFace::class.java)
                val database = WeatherDatabase.getDatabase(this)

                val weatherrepository = WeatherRepository(apiInterface,database)

                weatherviewmodel = ViewModelProvider(this, ViewModelFactory(this,weatherrepository, it.latitude, it.longitude, apikey)).get(WeatherViewModel::class.java)
                if(!isInternetAvailable(this)){
                    val othercity = findViewById<TextView>(R.id.othercities)
                    othercity.visibility = View.GONE
                    val data = weatherviewmodel.localweather
                    name.text = data.city
                    weather.text = data.weather
                    temperature.text = String.format("%.2f", data.temperature - 273)  + "°C"
                    humidity.text = data.humidity.toString() + "%"
                    windspeed.text = data.windSpeed.toString() + "\nKm/hr"

                    Glide.with(this).load(data.image).into(image)
                    date.text = data.date
                }
                else {
                    getWeather(it.latitude, it.longitude)
                    var counter = 0 // Counter to keep track of completed coroutines

                    for (data in citylist) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val weatherData = weatherviewmodel.getotherweathers(data)
                            weatherData?.let {
                                weatherlist.add(it)
                                counter++ // Increment counter when coroutine completes
                                if (counter == 6) {
                                    // All coroutines have completed, weatherlist size is 6
                                    withContext(Dispatchers.Main) {
                                        // Show toast on main thread
                                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                                        recyclerView.adapter = WeatherAdapter(this@MainActivity,weatherlist)
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the last known location and make the API call

                val task = fusedLocationProviderClient.lastLocation

                task.addOnSuccessListener {
                    if(it!=null){
                        val apiInterface = RetrofitObject.instance.create(ApiInterFace::class.java)
                        val database = WeatherDatabase.getDatabase(this)

                        val weatherrepository = WeatherRepository(apiInterface,database)

                        weatherviewmodel = ViewModelProvider(this, ViewModelFactory(this,weatherrepository, it.latitude, it.longitude, apikey)).get(WeatherViewModel::class.java)
                        if(!isInternetAvailable(this)){
                            val othercity = findViewById<TextView>(R.id.othercities)
                            othercity.visibility = View.GONE
                            val data = weatherviewmodel.localweather
                            name.text = data.city
                            weather.text = data.weather
                            temperature.text = String.format("%.2f", data.temperature - 273)  + "°C"
                            humidity.text = data.humidity.toString() + "%"
                            windspeed.text = data.windSpeed.toString() + "\nKm/hr"

                            Glide.with(this).load(data.image).into(image)
                            date.text = data.date
                        }
                        else {
                            getLastLocation()
                            var counter = 0 // Counter to keep track of completed coroutines

                            for (data in citylist) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val weatherData = weatherviewmodel.getotherweathers(data)
                                    weatherData?.let {
                                        weatherlist.add(it)
                                        counter++ // Increment counter when coroutine completes
                                        if (counter == 6) {
                                            // All coroutines have completed, weatherlist size is 6
                                            withContext(Dispatchers.Main) {
                                                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                                                recyclerView.adapter = WeatherAdapter(this@MainActivity,weatherlist)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@MainActivity,"Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                getWeather(location.latitude, location.longitude)

            }
        }
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

    private fun getWeather(latitude: Double, longitude: Double) {


        weatherviewmodel.weather.observe(this) {
            Log.d("@@@", it.toString())
            name.text = it.name
            weather.text = it.weather[0].main + "\n" + it.weather[0].description
            temperature.text = String.format("%.2f", it.main.temp - 273)  + "°C"
            humidity.text = it.main.humidity.toString() + "%"
            windspeed.text = it.wind.speed.toString() + "\nKm/hr"

            Glide.with(this).load("https://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png")
                .listener(object  : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        saveImageLocally(resource, this@MainActivity,it.name)
                        return false
                    }

                })
                .into(image)
            
            date.text = getdatatime()

            val name = it.name
            val weather = it.weather[0].main + "\n" + it.weather[0].description
            val temp = it.main.temp
            val humidity = it.main.humidity
            val windspeed = it.wind.speed
            val image = getLocalUri(name,this)
            val datetimes = getdatatime()

            val data = LocalDataWeather(0,name,image,weather,temp,humidity,windspeed,datetimes.toString())
            lifecycleScope.launch(Dispatchers.IO){
                weatherviewmodel.insert(data)
            }
        }
    }

    fun saveImageLocally(drawable: Drawable, context: Context, city: String) {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs() // Make sure the path exists
        val fileName = "$city.jpg" // Unique file name based on name and city
        val imageFile = File(cachePath, fileName)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save the bitmap to file
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    // Method to get the local URI of the saved image
    fun getLocalUri(city: String, context: Context): String {
        // Construct and return the local URI based on the saved location
        val cachePath = File(context.cacheDir, "images")
        val fileName = "$city.jpg" // Unique file name based on name and city
        return File(cachePath, fileName).path // Example local URI
    }

    private fun getdatatime(): CharSequence? {
        val currentTime = System.currentTimeMillis()

        // Define the date and time format
        val dateFormat = SimpleDateFormat("E MMM dd | hh:mma", Locale.getDefault())

        // Format the current date and time
        return dateFormat.format(currentTime)
    }

    companion object{
        val apikey = "7c9ceb8f5c89f6f6844a15459cc6b1f2"
    }
}