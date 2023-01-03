package com.example.libdelivery.utils.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(e: String): Exception()
}