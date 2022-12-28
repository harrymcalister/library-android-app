package com.example.libdelivery

import android.app.Application
import com.example.libdelivery.database.AppDatabase

class LibDeliveryApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}