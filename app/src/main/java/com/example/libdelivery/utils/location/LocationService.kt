package com.example.libdelivery.utils.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.session.PlaybackState.ACTION_STOP
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.libdelivery.R
import com.example.libdelivery.utils.location.LocationService.Companion.ACTION_START
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val NOTIFICATION_ID: Int = 101

class LocationService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        private val _lastLocation = MutableLiveData<Location>()
        val lastLocation: LiveData<Location> = _lastLocation
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Finding nearby libraries...")
            .setContentText("User location: Unknown")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                _lastLocation.postValue(location)
                val lat = lastLocation.value?.latitude?.toString() ?: "null"
                val long = lastLocation.value?.longitude?.toString() ?: "null"
                val notificationText = if (lat == "null") {
                    "Finding user location..."
                } else {
                    "User location: ($lat, $long)"
                }
                val updatedNotification = notification.setContentText(notificationText)
                notificationManager.notify(NOTIFICATION_ID, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}