package com.example.ntduc.locationdemo

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.IOException
import android.location.Geocoder
import android.location.Location
import java.util.*


class GeocoderService : IntentService("Geocoder") {

    private val TAG = "FetchAddressService"

    private var receiver: ResultReceiver? = null


    override fun onHandleIntent(intent: Intent?) {
        var errorMessage = ""

        receiver = intent?.getParcelableExtra(Constants.RECEIVER)

        if (intent == null || receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.")
            return
        }

        val location = intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)

        if (location == null) {
            errorMessage = "No location to provide"
            Log.wtf(TAG, errorMessage)
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
            return
        }


        val geocoder = Geocoder(this, Locale.getDefault())

        var addresses: List<Address> = emptyList()

        try {

            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                10)
        } catch (ioException: IOException) {
            errorMessage = "Service not avariable"
            Log.d(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            errorMessage = "invalid latetude"
            Log.d(TAG, "$errorMessage. Latitude = $location.latitude , " +
                    "Longitude = $location.longitude", illegalArgumentException)
        }

        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address"
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }

            Log.d(TAG, "address found")
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                addressFragments.joinToString(separator = "\n"))
        }
    }


    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)
    }

}