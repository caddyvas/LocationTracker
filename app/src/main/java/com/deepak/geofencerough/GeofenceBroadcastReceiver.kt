package com.deepak.geofencerough

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.deepak.geofencerough.activity.MapsActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG: String = GeofenceBroadcastReceiver::class.java.name

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Toast.makeText(context, "GEOFENCE TRIGGERED...", Toast.LENGTH_LONG).show()
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceList = geofencingEvent.triggeringGeofences

        for (geofence in geofenceList!!) {
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }

        // get the transition type
        val transitionType = geofencingEvent.geofenceTransition
        Log.d(TAG, "onReceive: $transitionType")


        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_LONG).show()
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendNotification2(
                    context,
                    "GEOFENCE_TRANSITION_ENTER",
                    MapsActivity::class.java
                )
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.d(TAG, "onReceive: GEOFENCE_TRANSITION_DWELL")
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "onReceive: GEOFENCE_TRANSITION_EXIT")
            }
        }


        /*val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification2(
            context,
            "GEOFENCE_TRANSITION_ENTER",
            MapsActivity::class.java
        )*/

    }
}