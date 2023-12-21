package com.cloudangle.cloud_test

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View.inflate
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var mMap: GoogleMap
    private val locationPermissionCode = 2
    lateinit var routePolyline: Polyline
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.googlemap_layout)
        // get cusrrent location
        getLocation()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    fun createCustomMarker(bitmap: Bitmap): BitmapDescriptor {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    override fun onLocationChanged(location: Location) {
        val startPoint = LatLng(location.latitude, location.longitude)
        val endPoint = LatLng(location.latitude, location.longitude)
        val polylineOptions = PolylineOptions()
            .add(startPoint, endPoint)
            .color(Color.BLUE) // Set the color of the polyline
            .width(5f)
        routePolyline = mMap.addPolyline(polylineOptions)
        val customMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
        val customMarker = createCustomMarker(customMarkerBitmap)
        // Create a LatLng object from the item's latitude and longitude
        val position = LatLng(location.latitude, location.longitude)
        // Create a MarkerOptions and add it to the map
        val markerOptions = MarkerOptions()
            .position(position)
            .icon(customMarker)
        mMap.addMarker(markerOptions)
        // Adjust the camera position to fit all the marker on the screen
        val boundsBuilder = LatLngBounds.builder()
        boundsBuilder.include(position)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18f))
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}