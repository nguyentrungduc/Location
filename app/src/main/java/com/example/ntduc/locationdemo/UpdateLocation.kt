package com.example.ntduc.locationdemo

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.*
import java.text.DateFormat
import java.util.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import android.widget.Toast
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.app.Activity
import android.content.Intent


class UpdateLocation : AppCompatActivity() {

    companion object {

        private const val TAG = "UpdateLocation"

    }

    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    private lateinit var mLastUpdateTime: String
    private  lateinit var  mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private var mRequestUpdate: Boolean? = false
    private val REQUEST_CHECK_SETTINGS = 1122

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_location)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        mRequestUpdate = false
        createLocationCallback()
        createLocationRequest()

        findViewById<Button>(R.id.btn_update).setOnClickListener {
            startLocationUpdates()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            stopLocationUpdates()
        }
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                mCurrentLocation = locationResult!!.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        mLocationRequest.interval = 100000

        mLocationRequest.fastestInterval = 60000

        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    private fun stopLocationUpdates() {
        if (!mRequestUpdate!!) {
            return
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(this) {
                mRequestUpdate = false
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this) {

                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )

                updateUI()
            }
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                        try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this@UpdateLocation, REQUEST_CHECK_SETTINGS)
                        } catch (sie: IntentSender.SendIntentException) {
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        Toast.makeText(this@UpdateLocation, errorMessage, Toast.LENGTH_LONG).show()
                        mRequestUpdate = false
                    }
                }

                updateUI()
            }
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.tv_location).text = mCurrentLocation.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.d(TAG,
                    "OK OK")
                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "NOOOO")
                    mRequestUpdate = false
                    updateUI()
                }
            }
        }
    }


}
