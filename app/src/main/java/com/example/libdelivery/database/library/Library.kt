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
/*
library_name= TableInfo.Column {
    name =
        'library_name', type = 'TEXT', affinity = '2', notNull = true, primaryKeyPosition = 0, defaultValue = 'null'
    'library_name', type='VARCHAR(50)', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'
id = TableInfo.Column {
    name =
        'id', type = 'INTEGER', affinity = '3', notNull = true, primaryKeyPosition = 1, defaultValue = 'null'
    'id', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=1, defaultValue='null'
latitude= TableInfo.Column {
    name =
        'latitude', type = 'REAL', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = 'null'
    'latitude', type = 'DOUBLE', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = 'null'
longitude= TableInfo.Column {
    name =
        'longitude', type = 'REAL', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = 'null'
        name='longitude', type='DOUBLE', affinity='4', notNull=true, primaryKeyPosition=0, defaultValue='null'
        , foreignKeys=[], indices=[]}
}
}, foreignKeys=[], indices=[]}

columns={latitude= TableInfo.Column {
    name =
        'latitude', type = 'DOUBLE', affinity = '4', notNull = true, primaryKeyPosition = 0, defaultValue = 'null'
}, library_name=Column{name='library_name', type='VARCHAR(50)', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, id=Column{name='id', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=1, defaultValue='null'}, longitude=Column{name='longitude', type='DOUBLE', affinity='4', notNull=true, primaryKeyPosition=0, defaultValue='null'}}, foreignKeys=[], indices=[]}

 */