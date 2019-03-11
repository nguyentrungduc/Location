package com.example.ntduc.locationdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection



class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    companion object {

        private const val TAG = "MainActivity"

    }

    private var service = LocationService

    private val REQUEST_LOCATION_PERMISSION = 1

    private var mBound = false

    private lateinit var resultReceiver: AddressResultReceiver

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultReceiver = AddressResultReceiver(Handler())

        findViewById<TextView>(R.id.btn_update).setOnClickListener {
            startActivity(Intent(this, UpdateLocation::class.java))

        }

        initializeUI()
    }

    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            //service = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, LocationService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION ->
                // If the permission is granted, get the location,
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(
                        this,
                        "permission dennie",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun initializeUI() {

        findViewById<Button>(R.id.btn_location).setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            Log.d(TAG, "getLocation: permissions granted")
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mLastLocation = location

                Log.d(TAG, location.toString())

                startIntentService()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "permission_denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            Log.d(TAG, "onReceive")


            val addressOutput = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""
            Toast.makeText(applicationContext, addressOutput, Toast.LENGTH_LONG).show()

            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(applicationContext, "found", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun startIntentService() {

        val intent = Intent(this, GeocoderService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation)
        }
        startService(intent)
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

}
