package com.cloudangle.cloud_test

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // location permission
        getLocationPermission()
    }

    private fun getApiCall() {
        val apiKey = "AIzaSyA5QJNhfqulzLCwmnHx0D1e6KraKqOILL0"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placesApiService = retrofit.create(PlacesApiService::class.java)

        // Replace the location with the user's current location
        val location = "$latitude,$longitude"
        val radius = 5000 // 5 kilometers
        val type = "cafe"

        GlobalScope.launch {
            try {
                val response = placesApiService.getNearbyPlaces(location, radius, type, apiKey)
                val places = response.results
                runOnUiThread {
                    val recyclerView: RecyclerView = findViewById(R.id.recyclicview)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerView.adapter = PlacesAdapter(places)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
                Toast.makeText(
                    applicationContext,
                    e.printStackTrace().toString(),
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    private fun getLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted
            requestLocationUpdates()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    println("Latitude: $latitude, Longitude: $longitude")

                    // After obtaining location, trigger API call
                    getApiCall()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                // Handle failure
                Toast.makeText(
                    applicationContext,
                    e.printStackTrace().toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}




