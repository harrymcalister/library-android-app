package com.example.libdelivery.database.library

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo

@Entity(tableName = "libraries")
data class Library (
    @PrimaryKey val id: Int,
    @NonNull @ColumnInfo(name = "library_name") val libName: String,
    @NonNull @ColumnInfo(name = "latitude", typeAffinity = 0) val libLatitude: Double,
    @NonNull @ColumnInfo(name = "longitude") val libLongitude: Double,
)