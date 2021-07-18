package com.security.location

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(),LocationListener{
    lateinit var locationManager:LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        grantPermission()
        checkLocationIsEnabledOrNot()

    }

    private fun getLocation() {
        try {
            val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 500, 5f,
                (this as LocationListener)
            )
        }catch (e:SecurityException){
            e.printStackTrace()
        }
    }

    private fun checkLocationIsEnabledOrNot() {
        val lm:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled=false
        var networkEnabled=false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e:Exception){
            e.printStackTrace()
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(!gpsEnabled && !networkEnabled){
            AlertDialog.Builder(this)
                .setTitle("Enable GPS Service")
                .setCancelable(false)
                .setPositiveButton("Enable",DialogInterface.OnClickListener { dialog, id ->
                  startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }).setNegativeButton("Cancel",null)
                .show()
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            val geocoder=  Geocoder(applicationContext, Locale.getDefault())
            val address: List<Address> = geocoder.getFromLocation(
                location.latitude,
                location.longitude, 1
            )
            Toast.makeText(this,"${address.get(0).countryCode}----${address[0].adminArea}" +
                    "----${address[0].locality}----${address[0].postalCode}" +
                    "----${address[0].getAddressLine(0)}",Toast.LENGTH_LONG).show()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun grantPermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),100)
        }
    }

    fun LocationButton(view: View) {
        getLocation()
    }
}